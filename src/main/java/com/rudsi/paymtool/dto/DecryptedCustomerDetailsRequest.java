package com.rudsi.paymtool.dto;

/**
 * DTO representing the decrypted customer details request payload.
 * <p>
 * This DTO is used to map the decrypted JSON payload that contains
 * customer information for lookup and persistence operations.
 */
public record DecryptedCustomerDetailsRequest(
        String cardNumber,
        String name,
        String mobile,
        String email
) {
}
