package com.atlpay.android;

/**
 * Created by OneOSloution on 8/10/2017.
 */

public class ATLPay {
    private static String secretKey;
    public static String getSecretKey() {
        return secretKey;
    }
    public static void setSecretKey(String secretKey) {
        ATLPay.secretKey = secretKey;
    }
}