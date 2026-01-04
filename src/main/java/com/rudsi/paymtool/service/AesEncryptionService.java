package com.rudsi.paymtool.service;

import java.io.InputStream;
import java.nio.charset.StandardCharsets;
import java.util.Base64;
import javax.crypto.Cipher;
import javax.crypto.spec.SecretKeySpec;

import org.springframework.stereotype.Service;

import com.rudsi.paymtool.error.DecryptionException;
import com.rudsi.paymtool.error.EncryptionException;

/**
 * Spring-managed service responsible for AES encryption and decryption.
 * <p>
 * The AES key is loaded once on bean construction from a Base64-encoded source
 * located on the classpath at {@code /keys/aes.key}. The same bean instance is
 * then injected wherever needed via Spring's dependency injection.
 */
@Service
public class AesEncryptionService {

    private final SecretKeySpec key;

    /**
     * Constructs a new {@code AesService} and initializes the AES key material.
     * <p>
     * If the key cannot be found or does not conform to the expected length for
     * AES-256 (32 bytes), an {@link IllegalStateException} is thrown during
     * application startup.
     */
    public AesEncryptionService() {
        String base64Key = null;

        // Attempt to read the Base64-encoded key from the classpath resource.
        try (InputStream is = getClass().getResourceAsStream("/keys/aes.key")) {
            if (is != null) {
                base64Key = new String(is.readAllBytes(), StandardCharsets.UTF_8).trim();
            }
        } catch (Exception e) {
            System.err.println("Warning: Could not read aes.key file: " + e.getMessage());
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

    /**
     * Encrypts the given plaintext using AES.
     *
     * @param plain plaintext value to encrypt
     * @return Base64-encoded ciphertext
     * @throws Exception if the cipher cannot be initialized or the encryption fails
     */
    public String encrypt(String plain) {

        try {
           Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
           cipher.init(Cipher.ENCRYPT_MODE, key);
           byte[] cipherBytes = cipher.doFinal(plain.getBytes(StandardCharsets.UTF_8));
           return Base64.getEncoder().encodeToString(cipherBytes); 
        } catch (Exception e) {
            throw new EncryptionException("Failed to encrypt data", e);
        }
        
    }

    /**
     * Decrypts the provided Base64-encoded AES ciphertext.
     *
     * @param base64Cipher Base64-encoded ciphertext to decrypt
     * @return decrypted plaintext
     * @throws Exception if the cipher cannot be initialized or the decryption fails
     */
    public String decrypt(String base64Cipher) {

        try {
           Cipher cipher = Cipher.getInstance("AES/ECB/PKCS5Padding");
           cipher.init(Cipher.DECRYPT_MODE, key);
           byte[] decoded = Base64.getDecoder().decode(base64Cipher);
           byte[] plainBytes = cipher.doFinal(decoded);
           return new String(plainBytes, StandardCharsets.UTF_8); 
        } catch (Exception e) {
           throw new DecryptionException("Failed to decrypt data", e);
        }
        
    }
}