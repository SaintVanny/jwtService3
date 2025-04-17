package com.vanna.jwtservice7.dto;

import lombok.Data;

@Data
public class GenerateRequest {
    private String p12;
    private String password;
    private String payload;


}