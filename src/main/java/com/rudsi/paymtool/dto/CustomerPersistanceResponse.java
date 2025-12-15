package com.rudsi.paymtool.dto;

public record CustomerPersistanceResponse(
        String status,
        String reasonCode,
        String cardNumber,
        String customerName,
        String mobile,
        String email) {
}
