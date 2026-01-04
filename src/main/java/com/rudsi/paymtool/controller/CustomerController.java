package com.rudsi.paymtool.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rudsi.paymtool.dto.CustomerDetailsResponse;
import com.rudsi.paymtool.dto.CustomerPersistanceRequest;
import com.rudsi.paymtool.dto.CustomerPersistanceResponse;
import com.rudsi.paymtool.dto.EncryptedCustomerDetailsRequest;
import com.rudsi.paymtool.service.CustomerDetailsService;
import com.rudsi.paymtool.service.CustomerPersistanceService;

/**
 * REST controller exposing APIs related to customer information.
 * <p>
 * Endpoints provided by this controller are versioned under
 * {@code /api/v1/customer}. It delegates all business logic to the underlying
 * service layer and focuses purely on HTTP request/response handling.
 */
@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {

    private static final Logger logger = LoggerFactory.getLogger(CustomerController.class);

    /** Service for handling encrypted customer detail lookup requests. */
    private final CustomerDetailsService customerDetailsService;

    /** Service for persisting customer information. */
    private final CustomerPersistanceService customerPersistanceService;

    /**
     * Constructs a new {@code CustomerController} with the required services.
     *
     * @param customerDetailsService     service used for customer detail retrieval
     * @param customerPersistanceService service used for customer persistence
     */
    public CustomerController(CustomerDetailsService customerDetailsService,
            CustomerPersistanceService customerPersistanceService) {
        this.customerDetailsService = customerDetailsService;
        this.customerPersistanceService = customerPersistanceService;
    }

    /**
     * Retrieves customer details from an encrypted request.
     * <p>
     * The body must contain an {@link EncryptedCustomerDetailsRequest} where the field
     * {@code encryptedData} holds an RSA-encrypted JSON payload. The decryption,
     * validation, and lookup are delegated to {@link CustomerDetailsService}.
     *
     * @param request encrypted request containing customer lookup information
     * @return HTTP 200 response containing a {@link CustomerDetailsResponse} with the
     *         customer data or error information
     */
    @PostMapping("/details")
    public ResponseEntity<CustomerDetailsResponse> getCustomerDetails(@RequestBody @Validated EncryptedCustomerDetailsRequest request) {
        logger.info("Received customer details lookup request");
        CustomerDetailsResponse response = customerDetailsService.processEncryptedRequest(request.encryptedData());
        logger.debug("Customer details lookup completed with status: {}", response.status());
        return ResponseEntity.ok(response);
    }

    /**
     * Persists customer data from an encrypted request payload.
     * <p>
     * This endpoint expects a {@link CustomerPersistanceRequest} containing an
     * RSA-encrypted JSON payload. The service layer handles decryption, validation,
     * and encryption of sensitive data before persistence.
     *
     * @param request request payload containing RSA-encrypted customer information
     * @return HTTP 200 response containing a {@link CustomerPersistanceResponse}
     *         describing the outcome of the persistence operation
     */
    @PostMapping("/persist")
    public ResponseEntity<CustomerPersistanceResponse> persistCustomerData(
            @RequestBody @Validated CustomerPersistanceRequest request) {
        logger.info("Received customer persistence request");
        CustomerPersistanceResponse response = customerPersistanceService.persistCustomerData(
                request.encryptedData());
        logger.debug("Customer persistence completed with status: {}", response.status());
        return ResponseEntity.ok(response);
    }

}
