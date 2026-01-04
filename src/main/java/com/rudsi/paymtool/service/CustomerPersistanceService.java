package com.rudsi.paymtool.service;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rudsi.paymtool.dto.DecryptedCustomerDetailsRequest;
import com.rudsi.paymtool.dto.CustomerPersistanceResponse;
import com.rudsi.paymtool.entity.CustomerDetails;
import com.rudsi.paymtool.error.DecryptionException;
import com.rudsi.paymtool.error.EncryptionException;
import com.rudsi.paymtool.error.DataProcessingException;
import com.rudsi.paymtool.repository.CustomerDetailsRepository;
import com.rudsi.paymtool.util.LunhValidation;

/**
 * Service responsible for persisting customer details in the data store.
 * <p>
 * This service handles the complete flow of processing encrypted customer data:
 * <ol>
 * <li>Decrypts the RSA-encrypted payload received from the client</li>
 * <li>Parses the JSON to extract customer information (card number, name, mobile, email)</li>
 * <li>Validates the card number using the Luhn algorithm</li>
 * <li>Encrypts the card number with AES for secure database storage</li>
 * <li>Persists the customer details to the repository</li>
 * </ol>
 * <p>
 * The service ensures that sensitive card data is encrypted at rest while maintaining
 * the ability to return non-sensitive customer information in the response.
 */
@Service
public class CustomerPersistanceService {

    private static final Logger logger = LoggerFactory.getLogger(CustomerPersistanceService.class);

    /** Service used to decrypt the incoming RSA-encrypted payload. */
    private final RsaEncryptionService rsaService;

    /** Service used to encrypt card numbers with AES for database storage. */
    private final AesEncryptionService aesService;

    /** Repository used to create and update {@link CustomerDetails} records. */
    private final CustomerDetailsRepository repo;

    /** Object mapper used to parse the decrypted JSON request payload. */
    private final ObjectMapper mapper = new ObjectMapper();

    /**
     * Creates a new {@code CustomerPersistanceService} with the required dependencies.
     *
     * @param rsaService RSA service used to decrypt incoming encrypted payloads
     * @param aesService AES service used to encrypt card numbers for storage
     * @param repo       repository used for customer persistence operations
     */
    public CustomerPersistanceService(
            RsaEncryptionService rsaService,
            AesEncryptionService aesService,
            CustomerDetailsRepository repo) {
        this.rsaService = rsaService;
        this.aesService = aesService;
        this.repo = repo;
    }

    /**
     * Processes an encrypted customer persistence request and persists the data.
     * <p>
     * The method performs the following steps:
     * <ol>
     * <li>Decrypts the RSA-encrypted payload to obtain a JSON string</li>
     * <li>Parses the JSON to extract customer fields (cardNumber, name, mobile, email)</li>
     * <li>Validates the card number format and Luhn checksum</li>
     * <li>Encrypts the card number with AES for secure database storage</li>
     * <li>Persists the customer entity to the repository</li>
     * <li>Returns a success response with the persisted customer information</li>
     * </ol>
     *
     * @param encryptedData Base64-encoded RSA-encrypted JSON payload containing customer
     *                       information to be persisted
     * @return response object describing the outcome of the persistence operation,
     *         including the persisted customer details
     * @throws DecryptionException      if RSA decryption fails
     * @throws DataProcessingException  if JSON parsing fails or required fields are missing
     * @throws IllegalArgumentException if card number validation fails
     * @throws EncryptionException     if AES encryption fails
     * @throws PersistenceException     if database persistence fails
     */
    public CustomerPersistanceResponse persistCustomerData(String encryptedData) {
        logger.debug("Processing encrypted customer persistence request");

        String decryptedJson = rsaService.decrypt(encryptedData);

        DecryptedCustomerDetailsRequest request;
        try {
            request = mapper.readValue(decryptedJson, DecryptedCustomerDetailsRequest.class);
            logger.debug("Successfully parsed decrypted JSON payload");
        } catch (JsonProcessingException ex) {
            logger.error("Failed to parse decrypted JSON payload", ex);
            throw new DataProcessingException("Failed to parse decrypted JSON payload", ex);
        }

       
        LunhValidation.validate(request.cardNumber());
            
        String encryptedCardNumber = aesService.encrypt(request.cardNumber());

        CustomerDetails newCustomer = new CustomerDetails();
        newCustomer.setEncryptedCardNumber(encryptedCardNumber);
        newCustomer.setName(request.name());
        newCustomer.setMobile(request.mobile());
        newCustomer.setEmail(request.email());

        repo.save(newCustomer);

        return new CustomerPersistanceResponse(
                "SUCCESS",
                null,
                request.cardNumber(),
                request.name(),
                request.mobile(),
                request.email());
    }

}
