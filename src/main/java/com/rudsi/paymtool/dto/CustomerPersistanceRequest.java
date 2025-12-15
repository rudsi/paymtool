package com.rudsi.paymtool.dto;

public record CustomerPersistanceRequest(
        String cardNumber,
        String encryptedCardNumber,
        String name,
        String mobile,
        String email) {
}
