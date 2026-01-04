package com.rudsi.paymtool.dto;

/**
 * Data Transfer Object representing an encrypted customer persistence request.
 * <p>
 * This record encapsulates an RSA-encrypted JSON payload that contains customer
 * information to be persisted. The encrypted payload must be decrypted by the
 * service layer to extract the actual customer data fields (card number, name,
 * mobile, email).
 * <p>
 * The encrypted payload is expected to be a Base64-encoded string containing
 * RSA-encrypted JSON. After decryption, the JSON should contain the following
 * fields:
 * <ul>
 * <li>{@code cardNumber} - the customer's payment card number (plaintext, will be
 * encrypted with AES before storage)</li>
 * <li>{@code name} - the customer's full name</li>
 * <li>{@code mobile} - the customer's mobile phone number</li>
 * <li>{@code email} - the customer's email address</li>
 * </ul>
 * <p>
 * This design ensures that sensitive customer data (especially card numbers) is
 * encrypted in transit and only decrypted server-side for processing.
 *
 * @param encryptedData Base64-encoded RSA-encrypted JSON payload containing
 *                       customer information to be persisted
 * @author rudsi
 * @since 1.0
 */
public record CustomerPersistanceRequest(
        String encryptedData) {
}
