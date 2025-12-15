package com.rudsi.paymtool.dto;

public record CustomerResponse(
        String status,
        String reasonCode,
        String cardNumber,
        String name,
        String mobile,
        String email) {
}