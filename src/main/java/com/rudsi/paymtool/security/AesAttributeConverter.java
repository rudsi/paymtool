package com.rudsi.paymtool.security;

import jakarta.persistence.AttributeConverter;
import jakarta.persistence.Converter;

@Converter
public class AesAttributeConverter implements AttributeConverter<String, String> {

    private final AesService aesService = AesService.getInstance();

    @Override
    public String convertToDatabaseColumn(String attribute) {
        if (attribute == null) {
            return null;
        }

        try {
            return aesService.encryptToBase64(attribute);
        } catch (Exception e) {
            throw new RuntimeException("AES encryption failed", e);
        }
    }

    @Override
    public String convertToEntityAttribute(String dbData) {
        if (dbData == null)
            return null;
        try {
            return aesService.decryptBase64(dbData);
        } catch (Exception ex) {
            throw new RuntimeException("AES decryption failed", ex);
        }
    }

}
