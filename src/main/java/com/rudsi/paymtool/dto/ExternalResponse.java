package com.rudsi.paymtool.dto;

public record ExternalResponse(
        String status,
        String transactionId) {
}