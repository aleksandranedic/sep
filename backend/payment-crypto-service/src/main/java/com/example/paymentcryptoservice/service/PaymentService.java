package com.example.paymentcryptoservice.service;
import com.example.paymentcryptoservice.dto.TransactionResponseDTO;
import com.example.paymentcryptoservice.model.Transaction;
import com.example.paymentcryptoservice.model.TransactionStatus;
import com.example.paymentcryptoservice.repository.TransactionRepository;
import com.example.paymentcryptoservice.utils.BtcToUsdConverter;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.bitcoinj.core.*;
import org.bitcoinj.script.Script;
import org.bitcoinj.wallet.*;
import org.bitcoinj.store.*;
import org.bitcoinj.params.TestNet3Params;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.*;

@Service
public class PaymentService {

    @Autowired
    private TransactionRepository transactionRepository;

    public TransactionResponseDTO generateAddress(String email, Double amount) throws IOException {
        Transaction transaction = new Transaction();
        transaction.setId(UUID.randomUUID());
        transaction.setEmail(email);
        transaction.setAmount(amount);
        transaction.setTransactionStatus(TransactionStatus.WAIT);
        NetworkParameters params = TestNet3Params.get();
        Wallet wallet = Wallet.createDeterministic(params, Script.ScriptType.P2PKH);
        Address newAddress = wallet.freshReceiveAddress();
        transaction.setTargetAddress(newAddress.toString());
        transactionRepository.save(transaction);
        System.out.println(new File(".").getAbsolutePath());
        wallet.saveToFile(new File("./wallets/" + transaction.getId().toString() + ".wallet"));
        DeterministicSeed seed = wallet.getKeyChainSeed();
        List<String> mnemonicCode = seed.getMnemonicCode();
        if (mnemonicCode == null) {
            throw new IllegalStateException("Mnemonic code not available");
        }
        // You might want to return the mnemonic code securely
        // For demonstration purposes, I'm returning it as a string
        System.out.println(String.join(" ", mnemonicCode));
        return new TransactionResponseDTO(newAddress.toString(), transaction.getId());
    }

    public TransactionStatus check(UUID id) {
        Optional<Transaction> optTransaction = transactionRepository.findById(id);
//        Transaction transaction = new Transaction();
//        String address = "tb1qlqte3xx6zz3yxpnl6ruw27ckea3l0w0hvl982r";
//        transaction.setTargetAddress(address);

        if (optTransaction.isEmpty()) {
            throw new RuntimeException("No such transaction!");
        }
        Transaction transaction = optTransaction.get();
        String address = transaction.getTargetAddress();
        try {
            URL url = new URL("https://blockstream.info/testnet/api/address/" + address);

            HttpURLConnection conn = (HttpURLConnection) url.openConnection();
            conn.setRequestMethod("GET");
            conn.connect();
            int responseCode = conn.getResponseCode();

            if (responseCode != 200) {
                throw new RuntimeException("HttpResponseCode: " + responseCode);
            } else {
                StringBuilder inline = new StringBuilder();
                Scanner scanner = new Scanner(url.openStream());

                while (scanner.hasNext()) {
                    System.out.println();
                    inline.append(scanner.nextLine());
                }
                scanner.close();
                System.out.println(inline);
                ObjectMapper mapper = new ObjectMapper();
                Map<String, Object> response = mapper.readValue(inline.toString(), Map.class);
                Object innerMap  = response.get("mempool_stats");
                Integer satoshi = (Integer) ((Map)innerMap).get("funded_txo_sum");
                System.out.println("satoshiii " + satoshi.toString());
                Double btc = satoshi / 100000000.0;
                BtcToUsdConverter btcToUsdConverter = new BtcToUsdConverter();
                Double usd = btcToUsdConverter.convertBtcToUsd(btc);
                System.out.println("usdddd" + usd.toString());
                if (usd + 5 < transaction.getAmount()) {
                    transaction.setTransactionStatus(TransactionStatus.FAIL);
                    return TransactionStatus.FAIL;
                }
                transaction.setTransactionStatus(TransactionStatus.SUCCESS);
                return TransactionStatus.SUCCESS;
            }
        } catch (Exception e) {
            e.printStackTrace();
            return TransactionStatus.FAIL;
        }
    }
}
