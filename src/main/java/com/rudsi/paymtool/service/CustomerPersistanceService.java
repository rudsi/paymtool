package com.rudsi.paymtool.service;

import com.rudsi.paymtool.dto.CustomerPersistanceResponse;
import com.rudsi.paymtool.entity.CustomerDetails;
import com.rudsi.paymtool.repository.CustomerDetailsRepository;

public class CustomerPersistanceService {

    private final CustomerDetailsRepository repo;

    public CustomerPersistanceService(CustomerDetailsRepository repo) {
        this.repo = repo;
    }

    public CustomerPersistanceResponse persistCustomerData(
            String cardNumber,
            String encryptedCardNumber,
            String name,
            String mobile,
            String email) {
        CustomerDetails newCustomer = new CustomerDetails();
        newCustomer.setEncryptedCardNumber(encryptedCardNumber);
        newCustomer.setName(name);
        newCustomer.setMobile(mobile);
        newCustomer.setEmail(email);
        repo.save(newCustomer);

        return new CustomerPersistanceResponse(
                "SUCCESS",
                null,
                cardNumber,
                name,
                mobile,
                email);
    }

}
