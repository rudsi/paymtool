package com.rudsi.paymtool.util;

/**
 * Utility class providing validation for payment card numbers using the Luhn
 * algorithm (also known as the mod-10 algorithm).
 * <p>
 * The Luhn algorithm is a simple checksum formula used to validate a variety of
 * identification numbers, most notably credit card numbers. It is designed to
 * detect single-digit errors and most transpositions of adjacent digits.
 * <p>
 * This implementation processes digits from right to left, doubling every second
 * digit (starting from the second-to-last digit). If doubling results in a
 * two-digit number, the digits are summed (equivalent to subtracting 9). The sum
 * of all processed digits must be divisible by 10 for the number to be valid.
 * <p>
 * This class is thread-safe as it contains only static methods with no mutable
 * state.
 *
 * @author rudsi
 * @since 1.0
 */
public final class LunhValidation {

    /**
     * Private constructor to prevent instantiation of this utility class.
     */
    private LunhValidation() {
        throw new UnsupportedOperationException("Utility class cannot be instantiated");
    }

    /**
     * Validates a card number using the Luhn (mod-10) checksum algorithm.
     * <p>
     * The algorithm works as follows:
     * <ol>
     * <li>Process digits from right to left.</li>
     * <li>For every second digit (starting from the second-to-last), double the
     * value.</li>
     * <li>If doubling results in a value greater than 9, subtract 9 (equivalent
     * to summing the digits).</li>
     * <li>Sum all processed digits.</li>
     * <li>The card number is valid if the sum is divisible by 10.</li>
     * </ol>
     * <p>
     * This method performs basic format validation (non-null, numeric-only,
     * length between 12-19 digits) before applying the Luhn algorithm.
     *
     * @param cardNumber the card number to validate, must contain only digits
     *                   and be between 12 and 19 characters in length
     * @return {@code true} if the card number passes both format and Luhn
     *         checksum validation, {@code false} otherwise
     * @throws IllegalArgumentException if the card number is null, contains
     *                                  non-digit characters, or has an invalid
     *                                  length (not between 12 and 19 digits)
     */
    public static boolean isValid(String cardNumber) {
        if (cardNumber == null) {
            throw new IllegalArgumentException("Card number cannot be null");
        }

        // Validate format: must contain only digits and be within standard PAN length range
        if (!cardNumber.matches("\\d{12,19}")) {
            throw new IllegalArgumentException(
                    "Card number must contain only digits and be between 12 and 19 characters");
        }

        int sum = 0;
        boolean doubleDigit = false;

        // Process digits from right to left as required by the Luhn algorithm
        for (int i = cardNumber.length() - 1; i >= 0; i--) {
            int digit = cardNumber.charAt(i) - '0';

            // Double every second digit starting from the second-to-last
            if (doubleDigit) {
                digit *= 2;
                // If doubling results in a two-digit number, subtract 9 (equivalent to summing digits)
                if (digit > 9) {
                    digit -= 9;
                }
            }

            sum += digit;
            doubleDigit = !doubleDigit;
        }

        // The card number is valid if the sum is divisible by 10
        return sum % 10 == 0;
    }

    /**
     * Validates a card number and throws an exception if validation fails.
     * <p>
     * This is a convenience method that calls {@link #isValid(String)} and throws
     * an {@link IllegalArgumentException} with a descriptive message if the card
     * number is invalid. Useful when validation failure should be treated as an
     * exceptional condition.
     *
     * @param cardNumber the card number to validate
     * @throws IllegalArgumentException if the card number is null, has invalid
     *                                  format, or fails the Luhn checksum
     */
    public static void validate(String cardNumber) {
        if (cardNumber == null) {
            throw new IllegalArgumentException("Card number cannot be null");
        }

        if (!cardNumber.matches("\\d{12,19}")) {
            throw new IllegalArgumentException(
                    "Card number must be numeric and between 12 and 19 digits");
        }

        if (!isValid(cardNumber)) {
            throw new IllegalArgumentException("Card number failed Luhn checksum validation");
        }
    }
}
