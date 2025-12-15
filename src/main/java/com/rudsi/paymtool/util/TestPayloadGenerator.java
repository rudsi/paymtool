package com.rudsi.paymtool.util;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.security.KeyFactory;
import java.security.PublicKey;
import java.security.spec.X509EncodedKeySpec;
import java.util.Base64;
import javax.crypto.Cipher;

public class TestPayloadGenerator {

    public static void main(String[] args) throws Exception {

        String jsonPayload = """
                {
                    "cardNumber": "4512567890123456",
                    "name": "Rudsi Test User",
                    "mobile": "9876543210",
                    "email": "testuser@rudsi.com"
                }
                """;

        System.out.println("--- Original JSON Payload ---");
        System.out.println(jsonPayload);

        InputStream keyStream = TestPayloadGenerator.class.getResourceAsStream("/keys/public_key.pem");
        if (keyStream == null) {
            throw new RuntimeException("Error: public_key.pem not found in src/main/resources/keys/");
        }

        String publicKeyContent = new String(keyStream.readAllBytes(), StandardCharsets.UTF_8);
        publicKeyContent = publicKeyContent
                .replace("-----BEGIN PUBLIC KEY-----", "")
                .replace("-----END PUBLIC KEY-----", "")
                .replaceAll("\\s", "");

        byte[] keyBytes = Base64.getDecoder().decode(publicKeyContent);
        X509EncodedKeySpec spec = new X509EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        PublicKey publicKey = kf.generatePublic(spec);

        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.ENCRYPT_MODE, publicKey);

        byte[] encryptedBytes = cipher.doFinal(jsonPayload.getBytes(StandardCharsets.UTF_8));
        String encryptedBase64 = Base64.getEncoder().encodeToString(encryptedBytes);

        System.out.println("\n--- ENCRYPTED PAYLOAD ---");
        System.out.println(encryptedBase64);
        System.out.println("-----------------------------------------------------");
    }
}