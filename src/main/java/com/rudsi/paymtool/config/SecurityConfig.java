package com.rudsi.paymtool.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.io.ClassPathResource;

import com.rudsi.paymtool.security.AesService;
import com.rudsi.paymtool.security.RsaService;

@Configuration
public class SecurityConfig {

    @Bean
    public AesService aesService() {
        return AesService.getInstance();
    }

    @Bean
    public RsaService rsaService() throws Exception {
        ClassPathResource res = new ClassPathResource(
                "keys/private_key.pem");
        return new RsaService(res.getInputStream());
    }
}