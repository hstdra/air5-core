package com.example.air5core.models.request;

import lombok.Data;

import java.util.List;

@Data
public class UserPointRequest {
    private String userId;
    private int point;
    private List<String> productNames;
}
