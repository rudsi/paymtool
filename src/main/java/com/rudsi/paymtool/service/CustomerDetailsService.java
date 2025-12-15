package com.rudsi.paymtool.service;

import java.util.Optional;
import org.springframework.stereotype.Service;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.rudsi.paymtool.dto.CustomerResponse;
import com.rudsi.paymtool.entity.CustomerDetails;
import com.rudsi.paymtool.repository.CustomerDetailsRepository;
import com.rudsi.paymtool.security.AesService;
import com.rudsi.paymtool.security.RsaService;

@Service
public class CustomerDetailsService {

    private final RsaService rsaService;
    private final AesService aesService;
    private final CustomerDetailsRepository repo;
    private final ObjectMapper mapper = new ObjectMapper();

    public CustomerDetailsService(
            RsaService rsaService,
            CustomerDetailsRepository repo) {

        this.rsaService = rsaService;
        this.aesService = AesService.getInstance();
        this.repo = repo;
    }

    public CustomerResponse processEncryptedRequest(String encryptedData) {
        try {
            String decryptedJson = rsaService.decryptBase64(encryptedData);

            var node = mapper.readTree(decryptedJson);
            String cardNumber = node.get("cardNumber").asText();

            validateCardNumber(cardNumber);

            String encryptedCardForLookup = aesService.encryptToBase64(cardNumber);

            Optional<CustomerDetails> existing = repo.findByEncryptedCardNumber(encryptedCardForLookup);

            if (existing.isPresent()) {
                CustomerDetails c = existing.get();
                return new CustomerResponse(
                        "APPROVED",
                        null,
                        cardNumber,
                        c.getName(),
                        c.getMobile(),
                        c.getEmail());
            }

            return new CustomerResponse(
                    "DECLINED",
                    "CARD_NOT_FOUND",
                    cardNumber,
                    null,
                    null,
                    null);

        } catch (IllegalArgumentException ex) {
            return new CustomerResponse(
                    "DECLINED",
                    "INVALID_CARD",
                    null,
                    null,
                    null,
                    null);
        } catch (Exception ex) {
            throw new RuntimeException("Processing failed", ex);
        }
    }

    private void validateCardNumber(String cardNumber) {
        if (cardNumber == null || !cardNumber.matches("\\d{16}")) {
            throw new IllegalArgumentException("cardNumber must be numeric and 16 digits");
        }
    }

}