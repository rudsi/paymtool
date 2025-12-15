package com.rudsi.paymtool.util;

import java.io.FileOutputStream;
import java.security.KeyPair;
import java.security.KeyPairGenerator;
import java.util.Base64;

public class KeyGeneratorUtility {
    public static void main(String[] args) throws Exception {
        KeyPairGenerator generator = KeyPairGenerator.getInstance("RSA");
        generator.initialize(2048);
        KeyPair pair = generator.generateKeyPair();

        String privateKeyPem = "-----BEGIN PRIVATE KEY-----\n"
                + Base64.getMimeEncoder().encodeToString(pair.getPrivate().getEncoded())
                + "\n-----END PRIVATE KEY-----";

        String publicKeyPem = "-----BEGIN PUBLIC KEY-----\n"
                + Base64.getMimeEncoder().encodeToString(pair.getPublic().getEncoded())
                + "\n-----END PUBLIC KEY-----";

        try (FileOutputStream fos = new FileOutputStream(
                "C:\\Users\\akshs\\desktop\\paymtool\\paymtool\\src\\main\\resources\\keys\\private_key.pem")) {
            fos.write(privateKeyPem.getBytes());
        }
        try (FileOutputStream fos = new FileOutputStream(
                "C:\\Users\\akshs\\desktop\\paymtool\\paymtool\\src\\main\\resources\\keys\\public_key.pem")) {
            fos.write(publicKeyPem.getBytes());
        }

        System.out.println("Keys generated in src/main/resources/keys/");
    }
}