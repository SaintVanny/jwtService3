package com.vanna.jwtservice7.dto;

import lombok.Data;

import java.util.Map;

@Data
public class GenerateRequest {
    private String p12;
    private String password;
    private Map<String,Object> payload;


}