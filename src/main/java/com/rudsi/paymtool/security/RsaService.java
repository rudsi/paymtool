package com.rudsi.paymtool.security;

import java.io.InputStream;
import java.io.InputStreamReader;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;

import javax.crypto.Cipher;

import org.bouncycastle.util.io.pem.PemObject;
import org.bouncycastle.util.io.pem.PemReader;

public class RsaService {

    private final PrivateKey privateKey;

    public RsaService(InputStream privateKeyPemStream) throws Exception {
        PemReader pemReader = new PemReader(new InputStreamReader(privateKeyPemStream));
        PemObject pemObject = pemReader.readPemObject();
        byte[] keyBytes = pemObject.getContent();
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        KeyFactory kf = KeyFactory.getInstance("RSA");
        this.privateKey = kf.generatePrivate(keySpec);
    }

    public String decryptBase64(String base64Cipher) throws Exception {
        byte[] cipherBytes = Base64.getDecoder().decode(base64Cipher);
        Cipher cipher = Cipher.getInstance("RSA/ECB/OAEPWithSHA-256AndMGF1Padding");
        cipher.init(Cipher.DECRYPT_MODE, privateKey);
        byte[] plain = cipher.doFinal(cipherBytes);
        return new String(plain, java.nio.charset.StandardCharsets.UTF_8);
    }

}
