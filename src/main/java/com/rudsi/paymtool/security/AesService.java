package com.rudsi.paymtool.security;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

public class AesService {

    private static final AesService INSTANCE = new AesService();
    private final SecretKeySpec key;

    private AesService() {
        String base64Key = null;

        if (base64Key == null) {
            try (InputStream is = getClass().getResourceAsStream("/keys/aes.key")) {
                if (is != null) {
                    base64Key = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
                }
            } catch (Exception e) {
                System.err.println("Warning: Could not read aes.key file: " + e.getMessage());
            }
        }

        if (base64Key == null || base64Key.isEmpty()) {
            throw new IllegalStateException(
                    "AES-256 key not found! Please set 'app.crypto.aes.key.base64' property, 'APP_AES_KEY_BASE64' env var, or create 'src/main/resources/keys/aes.key'");
        }

        byte[] keyBytes = Base64.getDecoder().decode(base64Key);

        if (keyBytes.length != 32) {
            throw new IllegalStateException("AES-256 requires 32-byte key, found: " + keyBytes.length);
        }

        this.key = new SecretKeySpec(keyBytes, "AES");
    }

    public static AesService getInstance() {
        return INSTANCE;
    }

    public String encrypt(String plain) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.ENCRYPT_MODE, key);
        byte[] cipherBytes = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
        return Base64.getEncoder().encodeToString(cipherBytes);
    }

    public String decrypt(String base64Cipher) throws Exception {
        Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
        cipher.init(Cipher.DECRYPT_MODE, key);
        byte[] decoded = Base64.getDecoder().decode(base64Cipher);
        byte[] plainBytes = cipher.doFinal(decoded);
        return new String(plainBytes, StandardCharsets.UTF_8);
    }
}