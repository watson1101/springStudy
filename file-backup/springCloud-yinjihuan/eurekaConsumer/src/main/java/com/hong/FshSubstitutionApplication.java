package com.hong;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;

@SpringBootApplication
@EnableDiscoveryClient
public class FshSubstitutionApplication {

    public static void main(String[] args) {
        SpringApplication.run(FshSubstitutionApplication.class, args);
    }

}
