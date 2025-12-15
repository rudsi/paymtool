package com.rudsi.paymtool.controller;

import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import com.rudsi.paymtool.dto.ApiResponse;
import com.rudsi.paymtool.dto.CustomerPersistanceRequest;
import com.rudsi.paymtool.dto.CustomerPersistanceResponse;
import com.rudsi.paymtool.dto.EncryptedRequest;
import com.rudsi.paymtool.service.CustomerDetailsService;
import com.rudsi.paymtool.service.CustomerPersistanceService;

@RestController
@RequestMapping("/api/v1/customer")
public class CustomerController {
    private final CustomerDetailsService customerDetailsService;
    private final CustomerPersistanceService customerPersistanceService;

    public CustomerController(CustomerDetailsService customerDetailsService,
            CustomerPersistanceService customerPersistanceService) {
        this.customerDetailsService = customerDetailsService;
        this.customerPersistanceService = customerPersistanceService;
    }

    @PostMapping("/details")
    public ResponseEntity<ApiResponse> getCustomerDetails(@RequestBody @Validated EncryptedRequest req) {
        var customer = customerDetailsService.processEncryptedRequest(req.encryptedData());
        var response = new ApiResponse("SUCCESS", customer);
        return ResponseEntity.ok(response);
    }

    @PostMapping("/persist")
    public ResponseEntity<CustomerPersistanceResponse> persistCustomerData(
            @RequestBody @Validated CustomerPersistanceRequest req) {
        CustomerPersistanceResponse customer = customerPersistanceService.persistCustomerData(
                req.cardNumber(),
                req.encryptedCardNumber(),
                req.name(),
                req.mobile(),
                req.email());

        return ResponseEntity.ok(customer);
    }

}
