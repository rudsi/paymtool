package com.rudsi.paymtool;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
public class PaymtoolApplication {

	public static void main(String[] args) {
		SpringApplication.run(PaymtoolApplication.class, args);
	}

	@Bean
	public RestTemplate restTemplate() {
		return new RestTemplate();
	}

}

//Pavan's Feedback --->

// Refactored the APIResponse DTO and merged it into the CustomerDetailsResponse itself.
// Refactored the system to return the appropriate responses when it encounters an exception.

// Things which i refactored by myself (need feedback)

// 1. Earlier "processCustomerDetails" method of the CustomerDetailsService was using a lot
// of try and catch for almost every logical snippet. but now each of the exception is handled
// by their respective service like
// Encryption service is thrown by the AesEncryptionService similarly for RsaEncryption and database related
// exceptions