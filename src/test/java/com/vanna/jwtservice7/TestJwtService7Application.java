package com.vanna.jwtservice7;

import org.springframework.boot.SpringApplication;

public class TestJwtService7Application {

    public static void main(String[] args) {
        SpringApplication.from(JwtService7Application::main).with(TestcontainersConfiguration.class).run(args);
    }

}
