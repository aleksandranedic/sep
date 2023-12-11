package com.example.bank.service;

import com.example.bank.dto.*;
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
import java.util.Map;
import java.util.Optional;
import java.util.Random;

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

    public ResponseEntity<?> createFullPayment(String method, PSPPaymentDTO pspPaymentDTO) {
        if (validMerchant(new MerchantDTO(pspPaymentDTO.getMerchantId(), pspPaymentDTO.getMerchantPassword()))) {
            Optional<User> optionalUser = userRepo.findById(pspPaymentDTO.getMerchantId());
            if (optionalUser.isEmpty()) {
                return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
            }
            User merchant = optionalUser.get();
            PaymentResponseDTO paymentResponseDTO = createNewPayment(method, new PaymentDTO(pspPaymentDTO.getMerchantId(), pspPaymentDTO.getMerchantPassword(), pspPaymentDTO.getAmount(), pspPaymentDTO.getMerchantOrderId(), pspPaymentDTO.getMerchantTimestamp()), merchant.getBankId());
            savePossiblePaymentResponses(paymentResponseDTO.getPaymentId(), pspPaymentDTO.getSuccessUrl(), pspPaymentDTO.getFailedUrl(), pspPaymentDTO.getErrorUrl());
            return ResponseEntity.ok(paymentResponseDTO);
        }
        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Merchant not found", "url", pspPaymentDTO.getErrorUrl()));
    }

    public ResponseEntity<?> payWithCard(String method, CardPaymentDTO cardPaymentDTO) {
        if (isSameBank(cardPaymentDTO)) {
            return pay(cardPaymentDTO);
        } else {
            return proceedTransactionViaPCC(cardPaymentDTO);
        }
    }

    public ResponseEntity<?> issuerPay(PCCPayloadDTO pccPayloadDTO) {
        Optional<User> optionalBuyer = userRepo.findByCardInfo(pccPayloadDTO.getCardInfo());
        if (optionalBuyer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Issuer not found"));

        }
        User buyer = optionalBuyer.get();
        System.out.println(buyer.getId());
        Transaction transaction = createNewTransaction(new PaymentDTO(buyer.getId(), buyer.getPassword(), pccPayloadDTO.getAmount(), pccPayloadDTO.getAcquirerOrderId(), LocalDateTime.parse(pccPayloadDTO.getAcquirerTimestamp())), buyer.getBankId());
        System.out.println("napravljena transakcija nova");
        if (isValidCardInfoAndAmount(pccPayloadDTO.getCardInfo(), buyer.getAmount(), transaction.getAmount())) {
            buyer.setAmount(buyer.getAmount() - transaction.getAmount());
            userRepo.save(buyer);
            System.out.println("uzete pare kupcu");
            transaction = generateAndSaveIssuerInfo(transaction);
            System.out.println("generisani issuer");
            return ResponseEntity.ok().body(new PCCResponseDTO(pccPayloadDTO.getAcquirerOrderId(), pccPayloadDTO.getAcquirerTimestamp(), transaction.getIssuerOrderId(), transaction.getIssuerTimeStamp().toString(), pccPayloadDTO.getAmount()));
        }  else {
            System.out.println("nema para");
            return ResponseEntity.badRequest().body(Map.of("message", "Can't proceed payment. Check card info and amount on the account"));
        }
    }

    private ResponseEntity<?> proceedTransactionViaPCC(CardPaymentDTO cardPaymentDTO) {
        Optional<Response> optionalResponse = responseRepo.findByPaymentId(cardPaymentDTO.getPaymentId());
        if (optionalResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Response url not found"));
        }
        Response response = optionalResponse.get();
        Optional<Transaction> optionalTransaction = transactionRepo.findByPaymentId(cardPaymentDTO.getPaymentId());
        if (optionalTransaction.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Transaction not found", "url", response.getErrorUrl()));
        }
        Transaction transaction = optionalTransaction.get();
        transaction = generateAndSaveAcquirerInfo(transaction);
        CardInfo cardInfo = getCardInfoFromCardPaymentDTO(cardPaymentDTO);
        return callPCC(response, new PCCPayloadDTO(cardInfo, transaction.getAmount(), transaction.getAcquirerOrderId(), transaction.getAcquirerTimestamp()));
    }

    private ResponseEntity<?> callPCC(Response responseUrl, PCCPayloadDTO pccPayloadDTO) { //CardPaymentResponseDTO
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
                    PCCResponseDTO res = (PCCResponseDTO)response.body();
                    return proceedAcquirerTransaction(responseUrl, res);
                }
                return ResponseEntity.status(500).body(Map.of("message", "Transaction failed, didn't get info from PCC", "url", responseUrl.getFailedUrl()));
            } else {
                System.out.println(response);
                System.out.println(response.body());
                // Handle unsuccessful response
                return ResponseEntity.status(response.code()).body(Map.of("message", "Transaction failed", "url", responseUrl.getFailedUrl()));
            }
        } catch (IOException e) {
            e.printStackTrace();
            return ResponseEntity.status(500).body(new CardPaymentResponseDTO(responseUrl.getErrorUrl()));
        }
    }

    private ResponseEntity<?> proceedAcquirerTransaction(Response response, PCCResponseDTO pccResponseDTO) { //CardPaymentResponseDTO
        Optional<Transaction> transactionOptional = transactionRepo.findByAcquirerOrderId(pccResponseDTO.getAcquirerOrderId());
        if (transactionOptional.isEmpty()) {
            //vrati pare na racun
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Transaction not found", "url", response.getErrorUrl()));

        }
        Transaction transaction = transactionOptional.get();
        transaction.setIssuerOrderId(pccResponseDTO.getIssuerOrderId());
        transaction.setIssuerTimeStamp(LocalDateTime.parse(pccResponseDTO.getIssuerTimestamp()));
        transactionRepo.save(transaction);
        Optional<User> optionalMerchant = userRepo.findById(transaction.getUserId());
        if (optionalMerchant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Merchant not found", "url", response.getErrorUrl()));
        }
        User merchant = optionalMerchant.get();
        addMerchantAmount(merchant, pccResponseDTO.getAmount());
        return ResponseEntity.ok().body(new CardPaymentResponseDTO(response.getSuccessUrl(), transaction.getMerchantOrderId(), transaction.getAcquirerOrderId(), transaction.getAcquirerTimestamp(), transaction.getPaymentId()));
    }

    public ResponseEntity<?> pay(CardPaymentDTO cardPaymentDTO) { //CardPaymentResponseDTO
        Optional<Response> optionalResponse = responseRepo.findByPaymentId(cardPaymentDTO.getPaymentId());
        if (optionalResponse.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Response URL not found"));
        }
        Response response = optionalResponse.get();
        Optional<Transaction> optionalTransaction = transactionRepo.findByPaymentId(cardPaymentDTO.getPaymentId());
        if (optionalTransaction.isEmpty()) {
            return ResponseEntity.badRequest().body(Map.of("message", "Transaction not found", "url", response.getErrorUrl()));
        }
        Transaction transaction = optionalTransaction.get();
        CardInfo cardInfo = getCardInfoFromCardPaymentDTO(cardPaymentDTO);
        transaction.setIssuerCardInfo(cardInfo);
        transactionRepo.save(transaction);
        Optional<User> optionalBuyer = userRepo.findByCardInfo(cardInfo);
        if (optionalBuyer.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Issuer not found", "url", response.getErrorUrl()));
        }
        User buyer = optionalBuyer.get();
        Optional<User> optionalMerchant = userRepo.findById(transaction.getUserId());
        if (optionalMerchant.isEmpty()) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(Map.of("message", "Merchant not found", "url", response.getErrorUrl()));
        }
        User merchant = optionalMerchant.get();
        if (isValidCardInfoAndAmount(cardInfo, buyer.getAmount(), transaction.getAmount())) {
            buyer.setAmount(buyer.getAmount() - transaction.getAmount());
            userRepo.save(buyer);
            addMerchantAmount(merchant, transaction.getAmount());
            transaction = generateAndSaveAcquirerInfo(transaction);
            return ResponseEntity.ok().body(new CardPaymentResponseDTO(response.getSuccessUrl(), transaction.getMerchantOrderId(), transaction.getAcquirerOrderId(), transaction.getAcquirerTimestamp(), transaction.getPaymentId()));
        }  else {
            return ResponseEntity.badRequest().body(Map.of("message", "Can't proceed payment. Check card info and amount on the account", "url", response.getFailedUrl()));
        }
    }

    private void addMerchantAmount(User merchant, double amount) {
        merchant.setAmount(merchant.getAmount() + amount);
        userRepo.save(merchant);
    }

    private CardInfo getCardInfoFromCardPaymentDTO(CardPaymentDTO cardPaymentDTO) {
        return new CardInfo(cardPaymentDTO.getPan(), cardPaymentDTO.getSecurityCode(), cardPaymentDTO.getCardHolderName(), cardPaymentDTO.getExpiryMonth(), cardPaymentDTO.getExpiryYear());
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
        if (currentYear > cardInfo.getExpiryYear()) return false;
        return currentYear != cardInfo.getExpiryYear() || currentMonth <= cardInfo.getExpiryMonth();
    }

    private boolean isSameBank(CardPaymentDTO paymentDTO) {
        String pan = paymentDTO.getPan().substring(0,3);
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

}
