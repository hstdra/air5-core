package com.example.air5core.models.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document(collection = "recommendProduct")
public class RecommendProduct {
    @Id
    private String id;
    private String userId;
    private List<String> productNames;
}
