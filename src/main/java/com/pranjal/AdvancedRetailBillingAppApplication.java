package com.pranjal;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cache.annotation.EnableCaching;
import org.springframework.data.web.config.EnableSpringDataWebSupport;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
@EnableSpringDataWebSupport(pageSerializationMode =
        EnableSpringDataWebSupport.PageSerializationMode.VIA_DTO)
@EnableWebSecurity
@EnableCaching
@SpringBootApplication
public class AdvancedRetailBillingAppApplication {

    public static void main(String[] args) {
        SpringApplication.run(AdvancedRetailBillingAppApplication.class, args);
    }

}
