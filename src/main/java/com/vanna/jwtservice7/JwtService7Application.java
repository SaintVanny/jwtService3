package com.vanna.jwtservice7;

import jakarta.annotation.PostConstruct;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import kz.gov.pki.kalkan.jce.provider.KalkanProvider;
import org.springframework.boot.autoconfigure.jdbc.DataSourceAutoConfiguration;


import java.security.Provider;
import java.security.Security;

@SpringBootApplication(exclude = {DataSourceAutoConfiguration.class})
public class JwtService7Application {

    public static void main(String[] args) {
        SpringApplication.run(JwtService7Application.class, args);
    }
    @PostConstruct
    public void addProvider() {
        Provider provider = new KalkanProvider();
        Security.addProvider(provider);
    }
}
