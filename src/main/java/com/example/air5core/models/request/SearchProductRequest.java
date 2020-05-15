package com.example.air5core.models.request;

import com.example.air5core.models.entities.ProductAttribute;
import lombok.Data;

import java.util.List;

@Data
public class SearchProductRequest {
    private String query;
    private String room;
    private String category;
    private List<ProductAttribute> productAttributes;
}
