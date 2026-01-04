package com.rudsi.paymtool.dto;

public record CustomerDetailsResponse(
        String status,
        String reasonCode,
        String cardNumber,
        String name,
        String mobile,
        String email) {
}