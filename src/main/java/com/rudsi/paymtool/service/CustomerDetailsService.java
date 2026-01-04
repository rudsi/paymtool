package com.rudsi.paymtool.service;

import java.util.Optional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rudsi.paymtool.dto.CustomerDetailsResponse;
import com.rudsi.paymtool.dto.DecryptedCustomerDetailsRequest;
import com.rudsi.paymtool.entity.CustomerDetails;
import com.rudsi.paymtool.error.DataProcessingException;
import com.rudsi.paymtool.repository.CustomerDetailsRepository;
import com.rudsi.paymtool.util.LunhValidation;

/**
 * Service responsible for handling customer detail lookups from encrypted requests.
 * <p>
 * The incoming payload is expected to be encrypted with RSA. Once decrypted,
 * the payload must contain a JSON field {@code cardNumber}. The card number is
 * then validated and re-encrypted with AES for secure database lookup.
 * <p>
 * If a matching customer is found, a response with status {@code APPROVED} is
 * returned. Otherwise, an appropriate decline response is produced.
 */
@Service
public class CustomerDetailsService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerDetailsService.class);

    /** Service used to decrypt the incoming RSA-encrypted payload. */
    private final RsaEncryptionService rsaService;

    /** AES service used to encrypt card data for persistence / lookup. */
    private final AesEncryptionService aesService;

    /** Repository used to retrieve persisted customer details. */
    private final CustomerDetailsRepository repo;

    /** Object mapper used to parse the decrypted JSON request payload. */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a new {@code CustomerDetailsService} with the given collaborators.
     *
     * @param rsaService RSA service used to decrypt incoming data
     * @param aesService AES service used to encrypt card data for persistence / lookup
     * @param repo       repository used for reading customer details
     */
    public CustomerDetailsService(
            RsaEncryptionService rsaService,
            AesEncryptionService aesService,
            CustomerDetailsRepository repo) {

        this.rsaService = rsaService;
        this.aesService = aesService;
        this.repo = repo;
    }

    /**
     * Processes an encrypted customer lookup request.
     * <p>
     * Steps:
     * <ol>
     * <li>Decrypt the RSA-encrypted payload.</li>
     * <li>Parse the JSON and extract the {@code cardNumber} field.</li>
     * <li>Validate the card number format and Luhn checksum.</li>
     * <li>Encrypt the card number with AES for secure database lookup.</li>
     * <li>Return an {@code APPROVED} response if the customer exists or a
     * corresponding decline response otherwise.</li>
     * </ol>
     *
     * @param encryptedData RSA-encrypted JSON payload representing the customer lookup request
     * @return Customer details response containing the status and customer data
     * @throws DataProcessingException if JSON parsing fails or required fields are missing
     */
    public CustomerDetailsResponse processEncryptedRequest(String encryptedData) {
        logger.debug("Processing encrypted customer lookup request");
        
        String decryptedJson = rsaService.decrypt(encryptedData);

        DecryptedCustomerDetailsRequest request;
        try {
            request = mapper.readValue(decryptedJson, DecryptedCustomerDetailsRequest.class);
            logger.debug("Extracted card number from decrypted payload");
        } catch (JsonProcessingException ex) {
            logger.error("Failed to parse decrypted JSON payload", ex);
            throw new DataProcessingException("Failed to parse decrypted JSON payload", ex);
        }

        if (request.cardNumber() == null || request.cardNumber().trim().isEmpty()) {
            logger.warn("Card number is null or empty");
            throw new DataProcessingException("Card number is required but was missing or empty");
        }

        LunhValidation.validate(request.cardNumber());
         
        String encryptedCardNumber = aesService.encrypt(request.cardNumber());

        Optional<CustomerDetails> existing = repo.findByEncryptedCardNumber(encryptedCardNumber);
         
        if (existing.isPresent()) {
            logger.info("Customer found for card number lookup");
            return new CustomerDetailsResponse(
                    "APPROVED",
                    null,
                    request.cardNumber(),
                    existing.get().getName(),
                    existing.get().getMobile(),
                    existing.get().getEmail());
        }

        logger.info("No customer found for card number lookup");
        return new CustomerDetailsResponse(
                "DECLINED",
                "CARD_NOT_FOUND",
                request.cardNumber(),
                null,
                null,
                null);
    }

}