package com.rudsi.paymtool.util;

import javax.crypto.KeyGenerator;
import javax.crypto.SecretKey;
import java.util.Base64;

public class AesKeyGen {
    public static void main(String[] args) throws Exception {
        // 1. Get the Key Generator for AES
        KeyGenerator keyGen = KeyGenerator.getInstance("AES");

        // 2. Initialize it for 256 bits (32 bytes)
        keyGen.init(256);

        // 3. Generate the secret key
        SecretKey secretKey = keyGen.generateKey();

        // 4. Encode it to Base64 (This is what your service expects)
        String base64Key = Base64.getEncoder().encodeToString(secretKey.getEncoded());

        System.out.println("=== COPY THE LINE BELOW ===");
        System.out.println(base64Key);
        System.out.println("===========================");
    }
}