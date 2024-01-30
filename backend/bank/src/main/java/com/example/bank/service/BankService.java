package com.example.bank.service;

import com.example.bank.dto.*;
import com.example.bank.logger.Logger;
import com.example.bank.model.*;
import com.example.bank.repository.BanksRepo;
import com.example.bank.repository.ResponseRepo;
import com.example.bank.repository.TransactionRepo;
import com.example.bank.repository.UserRepo;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;
import retrofit2.Call;
import retrofit2.Retrofit;
import retrofit2.converter.gson.GsonConverterFactory;

import java.io.IOException;
import java.time.LocalDateTime;
import java.util.*;

@Service
public class BankService {

    @Autowired
    UserRepo userRepo;
    @Autowired
    TransactionRepo transactionRepo;
    @Autowired
    ResponseRepo responseRepo;
    @Autowired
    BanksRepo banksRepo;
    @Autowired
    Logger logger;

    public ResponseEntity<?> createFullPayment(String method, PSPPaymentDTO pspPaymentDTO) {
        logger.info("Creating new payment", new Date().toString(), "BankService", pspPaymentDTO);
        if (validMerchant(new MerchantDTO(pspPaymentDTO.getMerchantId(), pspPaymentDTO.getMerchantPassword()))) {
            Optional<User> optionalUser = userRepo.findById(pspPaymentDTO.getMerchantId());
            if (optionalUser.isEmpty()) {
                logger.error("Merchant not found", new Date().toString(), "BankService", pspPaymentDTO);
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            User merchant = optionalUser.get();
            PaymentResponseDTO paymentResponseDTO = createNewPayment(method, new PaymentDTO(pspPaymentDTO.getMerchantId(), pspPaymentDTO.getMerchantPassword(), pspPaymentDTO.getAmount(), pspPaymentDTO.getMerchantOrderId(), pspPaymentDTO.getMerchantTimestamp()), merchant.getBankId());
            savePossiblePaymentResponses(paymentResponseDTO.getPaymentId(), pspPaymentDTO.getSuccessUrl(), pspPaymentDTO.getFailedUrl(), pspPaymentDTO.getErrorUrl());
            logger.info("Payment created", new Date().toString(), "BankService", pspPaymentDTO);
            return ResponseEntity.ok(paymentResponseDTO);
        }
        logger.error("Merchant not found", new Date().toString(), "BankService", pspPaymentDTO);
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Merchant not found", "url", pspPaymentDTO.getErrorUrl()));
    }

    public ResponseEntity<?> payWithCard(CardPaymentDTO cardPaymentDTO) {
        if (isSameBank(cardPaymentDTO)) {
            return pay(cardPaymentDTO);
        } else {
            return proceedTransactionViaPCC(cardPaymentDTO);
        }
    }

    public ResponseEntity<?> payWithQr(QRPaymentDTO qrCode) {
        logger.info("Starting QR payment", new Date().toString(), "BankService", qrCode);
        QrCodePaymentData data = decodePaymentString(qrCode.getQrCode());

        Transaction transaction = new Transaction();
        transaction.setAmount(data.getAmount());
        User user = userRepo.findById(qrCode.getUserId()).get();

        CardInfo cardInfo = new CardInfo();
        cardInfo.setExpiryYear(user.getCardInfo().getExpiryYear());
        cardInfo.setExpiryMonth(user.getCardInfo().getExpiryMonth());
        cardInfo.setPan(user.getCardInfo().getPan());
        cardInfo.setCardHolderName(user.getCardInfo().getCardHolderName());
        cardInfo.setSecurityCode(user.getCardInfo().getSecurityCode());

        transaction.setBankId(user.getBankId());
        transaction.setIssuerCardInfo(cardInfo);
        transaction.setUserId(user.getId());
        transactionRepo.save(transaction);

        Optional<User> optionalBuyer = userRepo.findByCardInfo(cardInfo);
        if (optionalBuyer.isEmpty()) {
            logger.error("Issuer not found", new Date().toString(), "BankService", qrCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Issuer not found"));
        }
        User buyer = optionalBuyer.get();
        Optional<User> optionalMerchant = userRepo.findById(data.getReceiverAccount());
        if (optionalMerchant.isEmpty()) {
            logger.error("Merchant not found", new Date().toString(), "BankService", qrCode);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Merchant not found"));
        }
        User merchant = optionalMerchant.get();
        if (isValidCardInfoAndAmount(cardInfo, buyer.getAmount(), transaction.getAmount())) {
            buyer.setAmount(buyer.getAmount() - transaction.getAmount());
            userRepo.save(buyer);
            addMerchantAmount(merchant, transaction.getAmount());
            transaction = generateAndSaveAcquirerInfo(transaction);
            logger.info("QR payment successful", new Date().toString(), "BankService", qrCode);
            return ResponseEntity.ok().body(new CardPaymentResponseDTO("payment/success", transaction.getMerchantOrderId(), transaction.getAcquirerOrderId(), transaction.getAcquirerTimestamp(), transaction.getPaymentId()));
        }
        logger.error("Can't proceed payment. Check card info and amount on the account", new Date().toString(), "BankService", qrCode);
        return ResponseEntity.badRequest().body(Map.of("message", "Can't proceed payment. Check card info and amount on the account", "url", "payment/failed"));
    }

    public static QrCodePaymentData decodePaymentString(String paymentString) {
        String[] pairs = paymentString.split("\\|");
        Map<String, String> decodedValues = new HashMap<>();

        for (String pair : pairs) {
            String[] keyValue = pair.split(":");
            if (keyValue.length == 2) {
                decodedValues.put(keyValue[0], keyValue[1]);
            }
        }

        QrCodePaymentData paymentData = new QrCodePaymentData();
        paymentData.setVersion(decodedValues.get("V"));
        paymentData.setCurrency(decodedValues.get("I").substring(0, 3));
        paymentData.setAmount(Double.parseDouble(decodedValues.get("I").substring(3)));
        paymentData.setReceiverAccount(decodedValues.get("R"));
        paymentData.setReceiverName(decodedValues.get("N"));
        paymentData.setPayerCity(decodedValues.get("P"));
        paymentData.setPaymentCode(decodedValues.get("SF"));
        paymentData.setPaymentPurpose(decodedValues.get("S"));

        return paymentData;
    }

    public ResponseEntity<?> issuerPay(PCCPayloadDTO pccPayloadDTO) {
        logger.info("Starting issuer payment", new Date().toString(), "BankService", pccPayloadDTO);
        CardInfo cardInfo = new CardInfo();
        cardInfo.setExpiryYear(pccPayloadDTO.getCardInfo().getExpiryYear());
        cardInfo.setExpiryMonth(pccPayloadDTO.getCardInfo().getExpiryMonth());
        cardInfo.setPan(pccPayloadDTO.getCardInfo().getPan());
        cardInfo.setCardHolderName(pccPayloadDTO.getCardInfo().getCardHolderName());
        cardInfo.setSecurityCode(pccPayloadDTO.getCardInfo().getSecurityCode());

        Optional<User> optionalBuyer = userRepo.findByCardInfo(cardInfo);
        if (optionalBuyer.isEmpty()) {
            logger.error("Issuer not found", new Date().toString(), "BankService - issuerPay function", pccPayloadDTO);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Issuer not found"));
        }
        User buyer = optionalBuyer.get();
        System.out.println(buyer.getId());
        Transaction transaction = createNewTransaction(new PaymentDTO(buyer.getId(), buyer.getPassword(), pccPayloadDTO.getAmount(), pccPayloadDTO.getAcquirerOrderId(), LocalDateTime.parse(pccPayloadDTO.getAcquirerTimestamp())), buyer.getBankId());
        System.out.println("napravljena transakcija nova");
        logger.info("Transaction created", new Date().toString(), "BankService", pccPayloadDTO);
        if (isValidCardInfoAndAmount(pccPayloadDTO.getCardInfo(), buyer.getAmount(), transaction.getAmount())) {
            buyer.setAmount(buyer.getAmount() - transaction.getAmount());
            userRepo.save(buyer);
            System.out.println("uzete pare kupcu");
            transaction = generateAndSaveIssuerInfo(transaction);
            System.out.println("generisani issuer");
            logger.info("Payment made and issuer created.", new Date().toString(), "BankService", pccPayloadDTO);
            return ResponseEntity.ok().body(new PCCResponseDTO(pccPayloadDTO.getAcquirerOrderId(), pccPayloadDTO.getAcquirerTimestamp(), transaction.getIssuerOrderId(), transaction.getIssuerTimeStamp().toString(), pccPayloadDTO.getAmount()));
        } else {
            System.out.println("nema para");
            logger.error("Can't proceed payment. Check card info and amount on the account", new Date().toString(), "BankService", pccPayloadDTO);
            return ResponseEntity.badRequest().body(Map.of("message", "Can't proceed payment. Check card info and amount on the account"));
        }
    }

    private ResponseEntity<?> proceedTransactionViaPCC(CardPaymentDTO cardPaymentDTO) {
        logger.info("Proceed issuer payment via PCC", new Date().toString(), "BankService", cardPaymentDTO);
        Optional<Response> optionalResponse = responseRepo.findByPaymentId(cardPaymentDTO.getPaymentId());
        if (optionalResponse.isEmpty()) {
            logger.error("Response url not found", new Date().toString(), "BankService", cardPaymentDTO);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Response url not found"));
        }
        Response response = optionalResponse.get();
        Optional<Transaction> optionalTransaction = transactionRepo.findByPaymentId(cardPaymentDTO.getPaymentId());
        if (optionalTransaction.isEmpty()) {
            logger.error("Transaction not found", new Date().toString(), "BankService", cardPaymentDTO);
            return ResponseEntity.badRequest().body(Map.of("message", "Transaction not found", "url", response.getErrorUrl()));
        }
        Transaction transaction = optionalTransaction.get();
        transaction = generateAndSaveAcquirerInfo(transaction);
        CardInfo cardInfo = getCardInfoFromCardPaymentDTO(cardPaymentDTO);
        return callPCC(response, new PCCPayloadDTO(cardInfo, transaction.getAmount(), transaction.getAcquirerOrderId(), transaction.getAcquirerTimestamp()));
    }

    private ResponseEntity<?> callPCC(Response responseUrl, PCCPayloadDTO pccPayloadDTO) { //CardPaymentResponseDTO
        logger.info("Creating PCC request", new Date().toString(), "BankService", pccPayloadDTO);
        String url = "http://localhost:8085/api/payment/issuer/";
        Retrofit retrofit = new Retrofit.Builder()
                .baseUrl(url)
                .addConverterFactory(GsonConverterFactory.create())
                .build();

        RetrofitPCC retrofitPayment = retrofit.create(RetrofitPCC.class);

        Call<?> call = retrofitPayment.getIssuerBankResult(url, pccPayloadDTO);

        try {
            retrofit2.Response<?> response = call.execute();
            System.out.println("Response od pcc, nakon sto issuer vrati odg da nema para");
            System.out.println(response);
            if (response.isSuccessful()) {
                if (response.body() != null) {
                    PCCResponseDTO res = (PCCResponseDTO) response.body();
                    logger.info("Issuer transaction finished successfully", new Date().toString(), "BankService", res);
                    return proceedAcquirerTransaction(responseUrl, res);
                }
                logger.error("Transaction failed, didn't get info from PCC", new Date().toString(), "BankService", pccPayloadDTO);
                return ResponseEntity.status(500).body(Map.of("message", "Transaction failed, didn't get info from PCC", "url", responseUrl.getFailedUrl()));
            } else {
                System.out.println(response);
                System.out.println(response.body());
                // Handle unsuccessful response
                logger.error("Transaction failed, didn't get info from PCC", new Date().toString(), "BankService", pccPayloadDTO);
                return ResponseEntity.status(response.code()).body(Map.of("message", "Transaction failed", "url", responseUrl.getFailedUrl()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            logger.error("Transaction failed, didn't get info from PCC", new Date().toString(), "BankService", pccPayloadDTO);
            return ResponseEntity.status(500).body(new CardPaymentResponseDTO(responseUrl.getErrorUrl()));
        }
    }

    private ResponseEntity<?> proceedAcquirerTransaction(Response response, PCCResponseDTO pccResponseDTO) { //CardPaymentResponseDTO
        Optional<Transaction> transactionOptional = transactionRepo.findByAcquirerOrderId(pccResponseDTO.getAcquirerOrderId());
        if (transactionOptional.isEmpty()) {
            //vrati pare na racun
            logger.error("Transaction not found", new Date().toString(), "BankService - proceedAcquirerTransaction", pccResponseDTO);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Transaction not found", "url", response.getErrorUrl()));

        }
        Transaction transaction = transactionOptional.get();
        transaction.setIssuerOrderId(pccResponseDTO.getIssuerOrderId());
        transaction.setIssuerTimeStamp(LocalDateTime.parse(pccResponseDTO.getIssuerTimestamp()));
        transactionRepo.save(transaction);
        Optional<User> optionalMerchant = userRepo.findById(transaction.getUserId());
        if (optionalMerchant.isEmpty()) {
            logger.info("Merchant not found", new Date().toString(), "BankService", pccResponseDTO);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Merchant not found", "url", response.getErrorUrl()));
        }
        User merchant = optionalMerchant.get();
        addMerchantAmount(merchant, pccResponseDTO.getAmount());
        logger.info("Payment successfully finished", new Date().toString(), "BankService", pccResponseDTO);
        return ResponseEntity.ok().body(new CardPaymentResponseDTO(response.getSuccessUrl(), transaction.getMerchantOrderId(), transaction.getAcquirerOrderId(), transaction.getAcquirerTimestamp(), transaction.getPaymentId()));
    }

    public ResponseEntity<?> pay(CardPaymentDTO cardPaymentDTO) { //CardPaymentResponseDTO
        logger.info("Starting card payment", new Date().toString(), "BankService", cardPaymentDTO);
        Optional<Response> optionalResponse = responseRepo.findByPaymentId(cardPaymentDTO.getPaymentId());
        if (optionalResponse.isEmpty()) {
            logger.error("Response URL not found", new Date().toString(), "BankService - pay", cardPaymentDTO);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Response URL not found"));
        }
        Response response = optionalResponse.get();
        Optional<Transaction> optionalTransaction = transactionRepo.findByPaymentId(cardPaymentDTO.getPaymentId());
        if (optionalTransaction.isEmpty()) {
            logger.error("Transaction not found", new Date().toString(), "BankService - pay", cardPaymentDTO);
            return ResponseEntity.badRequest().body(Map.of("message", "Transaction not found", "url", response.getErrorUrl()));
        }
        Transaction transaction = optionalTransaction.get();
        CardInfo cardInfo = getCardInfoFromCardPaymentDTO(cardPaymentDTO);
        transaction.setIssuerCardInfo(cardInfo);
        transactionRepo.save(transaction);
        Optional<User> optionalBuyer = userRepo.findByCardInfo(cardInfo);
        if (optionalBuyer.isEmpty()) {
            logger.error("Issuer not found", new Date().toString(), "BankService - pay", cardPaymentDTO);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Issuer not found", "url", response.getErrorUrl()));
        }
        User buyer = optionalBuyer.get();
        Optional<User> optionalMerchant = userRepo.findById(transaction.getUserId());
        if (optionalMerchant.isEmpty()) {
            logger.error("Merchant not found", new Date().toString(), "BankService - pay", cardPaymentDTO);
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Merchant not found", "url", response.getErrorUrl()));
        }
        User merchant = optionalMerchant.get();
        if (isValidCardInfoAndAmount(cardInfo, buyer.getAmount(), transaction.getAmount())) {
            buyer.setAmount(buyer.getAmount() - transaction.getAmount());
            userRepo.save(buyer);
            addMerchantAmount(merchant, transaction.getAmount());
            transaction = generateAndSaveAcquirerInfo(transaction);
            logger.error("Card payment successfully finished", new Date().toString(), "BankService - pay", cardPaymentDTO);
            return ResponseEntity.ok().body(new CardPaymentResponseDTO(response.getSuccessUrl(), transaction.getMerchantOrderId(), transaction.getAcquirerOrderId(), transaction.getAcquirerTimestamp(), transaction.getPaymentId()));
        } else {
            logger.error("Can't proceed payment. Check card info and amount on the account", new Date().toString(), "BankService - pay", cardPaymentDTO);
            return ResponseEntity.badRequest().body(Map.of("message", "Can't proceed payment. Check card info and amount on the account", "url", response.getFailedUrl()));
        }
    }

    private void addMerchantAmount(User merchant, double amount) {
        merchant.setAmount(merchant.getAmount() + amount);
        userRepo.save(merchant);
    }

    private CardInfo getCardInfoFromCardPaymentDTO(CardPaymentDTO cardPaymentDTO) {
        CardInfo cardInfo = new CardInfo();
        cardInfo.setCardHolderName(cardPaymentDTO.getCardHolderName());
        cardInfo.setSecurityCode(cardPaymentDTO.getSecurityCode());
        cardInfo.setPan(cardPaymentDTO.getPan());
        cardInfo.setExpiryMonth(cardPaymentDTO.getExpiryMonth());
        cardInfo.setExpiryYear(cardPaymentDTO.getExpiryYear());
        return cardInfo;
    }

    private Transaction generateAndSaveAcquirerInfo(Transaction transaction) {
        UserOrderInfo acquirerInfo = generateAcquirerInfo();
        transaction.setAcquirerOrderId(acquirerInfo.getId());
        transaction.setAcquirerTimestamp(acquirerInfo.getTimestamp());
        return transactionRepo.save(transaction);
    }

    private Transaction generateAndSaveIssuerInfo(Transaction transaction) {
        UserOrderInfo issuerInfo = generateIssuerInfo();
        transaction.setIssuerOrderId(issuerInfo.getId());
        transaction.setIssuerTimeStamp(LocalDateTime.parse(issuerInfo.getTimestamp()));
        return transactionRepo.save(transaction);
    }

    private UserOrderInfo generateIssuerInfo() {
        String issuerOrderId = createIssuerOrderId();
        String issuerTimestamp = LocalDateTime.now().toString();
        return new UserOrderInfo(issuerOrderId, issuerTimestamp);
    }

    private UserOrderInfo generateAcquirerInfo() {
        String acquirerId = createAcquirerOrderId();
        String acquirerTimeStamp = LocalDateTime.now().toString();
        return new UserOrderInfo(acquirerId, acquirerTimeStamp);
    }

    private String createAcquirerOrderId() {
        Random random = new Random();
        String acquirerOrderId;
        boolean uniqueIdFound = false;
        do {
            acquirerOrderId = String.format("%010d", random.nextInt(1_000_000_000));
            Optional<Transaction> transactionOptional = transactionRepo.findByAcquirerOrderId(acquirerOrderId);
            if (transactionOptional.isEmpty()) {
                uniqueIdFound = true;
            }
        } while (!uniqueIdFound);
        return acquirerOrderId;
    }

    private String createIssuerOrderId() {
        Random random = new Random();
        String issuerOrderId;
        boolean uniqueIdFound = false;
        do {
            issuerOrderId = String.format("%010d", random.nextInt(1_000_000_000));
            Optional<Transaction> transactionOptional = transactionRepo.findByIssuerOrderId(issuerOrderId);
            if (transactionOptional.isEmpty()) {
                uniqueIdFound = true;
            }
        } while (!uniqueIdFound);
        return issuerOrderId;
    }

    private boolean isValidCardInfoAndAmount(CardInfo cardInfo, double buyerAmount, double transactionAmount) {
        if (transactionAmount > buyerAmount) return false;
        LocalDateTime currentDate = LocalDateTime.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        if (currentYear > 2000 + cardInfo.getExpiryYear()) return false;
        return currentYear != 2000 + cardInfo.getExpiryYear() || currentMonth <= cardInfo.getExpiryMonth();
    }

    private boolean isSameBank(CardPaymentDTO paymentDTO) {
        String pan = paymentDTO.getPan().substring(0, 3);
        Optional<Banks> optionalBanks = banksRepo.findByPan(pan);
        if (optionalBanks.isEmpty()) {
            return true;
        }
        String buyerBankId = optionalBanks.get().getBankId();
        Optional<Transaction> optionalTransaction = transactionRepo.findByPaymentId(paymentDTO.getPaymentId());
        if (optionalTransaction.isEmpty()) {
            return true;
        }
        Transaction transaction = optionalTransaction.get();
        Optional<User> optionalUser = userRepo.findById(transaction.getUserId());
        if (optionalUser.isEmpty()) {
            return true;
        }
        String merchantBankId = optionalUser.get().getBankId();
        return buyerBankId.equals(merchantBankId);
    }

    private void savePossiblePaymentResponses(String paymentId, String successUrl, String failedUrl, String errorUrl) {
        responseRepo.save(new Response(paymentId, successUrl, failedUrl, errorUrl));
    }

    private boolean validMerchant(MerchantDTO merchantDTO) {
        Optional<User> optionalUser = userRepo.findById(merchantDTO.getMerchantId());
        if (optionalUser.isEmpty()) {
            return false;
        }
        User merchant = optionalUser.get();
        return merchant.getPassword().equals(merchantDTO.getMerchantPassword());
    }

    private PaymentResponseDTO createNewPayment(String method, PaymentDTO paymentDTO, String bankId) {
        Transaction transaction = createNewTransaction(paymentDTO, bankId);
        String url = findUrl(method, paymentDTO);
        return new PaymentResponseDTO(url, transaction.getPaymentId());
    }

    private Transaction createNewTransaction(PaymentDTO paymentDTO, String bankId) {
        String paymentId = createPaymentId();
        Transaction transaction = new Transaction(paymentDTO.getMerchantId(), paymentId, paymentDTO.getAmount(), paymentDTO.getMerchantOrderId(), paymentDTO.getMerchantTimestamp(), bankId);
        return transactionRepo.save(transaction);
    }

    private String findUrl(String method, PaymentDTO paymentDTO) {
        if (method.equalsIgnoreCase("card")) {
            return "http://localhost:4200/pay/card";
        }
        Optional<User> optionalUser = userRepo.findById(paymentDTO.getMerchantId());
        if (optionalUser.isEmpty()) {
            return "http://localhost:4200/pay/qr?currency=RSD&amount=" + paymentDTO.getAmount() + "&userId=" + paymentDTO.getMerchantId();
        }
        User merchant = optionalUser.get();
        return "http://localhost:4200/pay/qr?currency=RSD&amount=" + paymentDTO.getAmount() + "&userId=" + paymentDTO.getMerchantId() + "&userName=" + merchant.getName();
    }

    private String createPaymentId() {
        Random random = new Random();
        String paymentId;
        boolean uniqueIdFound = false;
        do {
            paymentId = String.format("%010d", random.nextInt(1_000_000_000));
            Optional<Transaction> transactionOptional = transactionRepo.findByPaymentId(paymentId);
            if (transactionOptional.isEmpty()) {
                uniqueIdFound = true;
            }
        } while (!uniqueIdFound);
        return paymentId;
    }

    public boolean editCardData(CardPaymentDTO cardPaymentDTO) {
        LocalDateTime currentDate = LocalDateTime.now();
        int currentYear = currentDate.getYear();
        int currentMonth = currentDate.getMonthValue();
        if (currentYear > 2000 + cardPaymentDTO.getExpiryYear())
            return false;
        if (currentYear == 2000 + cardPaymentDTO.getExpiryYear() && currentMonth >= cardPaymentDTO.getExpiryMonth())
            return false;

        CardInfo cardInfo = new CardInfo();
        cardInfo.setCardHolderName(cardPaymentDTO.getCardHolderName());
        cardInfo.setExpiryMonth(cardPaymentDTO.getExpiryMonth());
        cardInfo.setExpiryYear(cardPaymentDTO.getExpiryYear());
        cardInfo.setPan(cardPaymentDTO.getPan());
        cardInfo.setSecurityCode(cardPaymentDTO.getSecurityCode());

        Optional<User> optionalUser = userRepo.findById(cardPaymentDTO.getPaymentId());
        if (optionalUser.isEmpty()) {
            User user = new User();
            user.setBankId("1");
            user.setCardInfo(cardInfo);
            user.setAmount(0.0);
            user.setName("User name");
            userRepo.save(user);
            return true;
        }
        optionalUser.get().setCardInfo(cardInfo);
        userRepo.save(optionalUser.get());
        return true;
    }
}
