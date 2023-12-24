package com.example.paymentcryptoservice.utils;

import org.bitcoinj.core.Context;
import org.bitcoinj.core.NetworkParameters;
import org.bitcoinj.params.TestNet3Params;
import org.bitcoinj.wallet.Wallet;

import java.io.File;

public class WalletManager {
    public static void main(String[] args) {
        // Setup Network Parameters for MainNet
        NetworkParameters params = TestNet3Params.get();
        Context context = new Context(params);

        // Load the wallet from the file
        File walletFile = new File("./wallets/1d2a615d-bd0c-4c0c-b4e2-35d122621d2a.wallet");
        try {
            Wallet wallet = Wallet.loadFromFile(walletFile);
            // Use the wallet object as needed
            System.out.println("Wallet loaded successfully.");
            // For example, print the wallet's current balance
            System.out.println("Wallet balance: " + wallet.getBalance());
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
}
