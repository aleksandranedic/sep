package com.example.paymentcryptoservice.utils;

import java.io.IOException;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.Map;
import java.util.Scanner;

import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;

public class BtcToUsdConverter {

    private static double lastExchangeRate = 0.0;
    private static long lastCheckedTime = 0;

    public static double convertBtcToUsd(double btcAmount) throws IOException {
        long currentTime = System.currentTimeMillis();
        if (lastExchangeRate == 0.0 || (currentTime - lastCheckedTime) > 300000) {
            lastExchangeRate = fetchBtcToUsdRate();
            lastCheckedTime = currentTime;
        }
        return btcAmount * lastExchangeRate;
    }

    private static double fetchBtcToUsdRate() throws IOException {
        URL url = new URL("https://api.coingecko.com/api/v3/simple/price?ids=bitcoin&vs_currencies=usd");
        HttpURLConnection conn = (HttpURLConnection) url.openConnection();
        conn.setRequestMethod("GET");
        conn.connect();

        StringBuilder inline = new StringBuilder();
        Scanner scanner = new Scanner(url.openStream());
        while (scanner.hasNext()) {
            inline.append(scanner.nextLine());
        }
        scanner.close();

        ObjectMapper mapper = new ObjectMapper();
        Map<String, Map<String, Double>> responseMap = mapper.readValue(inline.toString(),
                new TypeReference<Map<String, Map<String, Double>>>() {});
        return responseMap.get("bitcoin").get("usd");
    }


    public static void main(String[] args) {
        try {
            double btcAmount = 0.01442738; // Example BTC amount
            double usdAmount = convertBtcToUsd(btcAmount);
            System.out.println("USD Amount: " + usdAmount);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}

