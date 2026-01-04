package com.rudsi.paymtool.service;

import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.io.Resource;
import org.springframework.stereotype.Service;

import com.rudsi.paymtool.error.DecryptionException;

/**
 * Spring-managed service that performs RSA decryption using a private key loaded
 * from the application classpath.
 * <p>
 * The private key is expected to be in PEM format and located at
 * {@code classpath:keys/private_key.pem}. The key is read and converted into a
 * {@link PrivateKey} instance once, during bean initialization, and reused for
 * all subsequent decryption operations.
 */
@Service
public class RsaEncryptionService {

    /** RSA private key used to decrypt incoming ciphertexts. */
    private final PrivateKey privateKey;

    /**
     * Constructs a new {@code RsaService} and initializes the RSA private key.
     * <p>
     * The constructor reads the PEM-encoded private key from the provided
     * {@link Resource}, parses it using BouncyCastle's {@link PemReader}, and
     * converts it into a {@link PrivateKey} using a {@link KeyFactory}.
     *
     * @param privateKeyResource classpath resource pointing to the PEM-encoded
     *                           private key (e.g. {@code classpath:keys/private_key.pem})
     * @throws RuntimeException if the key cannot be read or parsed successfully
     */
    public RsaEncryptionService(@Value("classpath:keys/private_key.pem") Resource privateKeyResource) {
        try (PemReader pemReader = new PemReader(new InputStreamReader(privateKeyResource.getInputStream()))) {
            // Extract the raw key bytes from the PEM structure.
            PemObject pemObject = pemReader.readPemObject();
            byte[] keybytes = pemObject.getContent();

            // Build a key specification and generate a PrivateKey instance.
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keybytes);
            KeyFactory kf = KeyFactory.getInstance("RSA");
            this.privateKey = kf.generatePrivate(keySpec);
        } catch (Exception e) {
            // Fail fast at startup if the key cannot be loaded or parsed.
            throw new RuntimeException("Failed to initialize RSA service", e);
        }
    }

    /**
     * Decrypts a Base64-encoded RSA ciphertext using OAEP with SHA-256.
     *
     * @param base64Cipher Base64-encoded ciphertext produced using the matching
     *                     public key and algorithm
     * @return decrypted plaintext String (UTF-8)
     * @throws Exception if the cipher cannot be initialized or the decryption
     *                   operation fails
     */
    public String decrypt(String base64Cipher) {

        try {
        // Decode the incoming Base64 representation into raw cipher bytes.
           byte[] cipherBytes = Base64.getDecoder().decode(base64Cipher);

        // Configure the RSA cipher with OAEP (SHA-256) padding and initialize
        // it for decryption using the pre-loaded private key.
           Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
           cipher.init(Cipher.DECRYPT_MODE, privateKey);

        // Perform the decryption and convert the resulting bytes back to a String.
           byte[] plain = cipher.doFinal(cipherBytes);
           return new String(plain, java.nio.charset.StandardCharsets.UTF_8);
        } catch (Exception e) {
            throw new DecryptionException("Data decryption failed", e);
        }
    }  

}
