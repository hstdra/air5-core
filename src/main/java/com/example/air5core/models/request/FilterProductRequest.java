package com.example.air5core.models.request;

import com.example.air5core.models.others.ProductAttribute;
import lombok.Data;

import java.util.List;

@Data
public class FilterProductRequest {
    private String query;
    private String room;
    private String category;
    private List<ProductAttribute> productAttributes;
    private double minPrice = 0;
    private double maxPrice = Double.MAX_VALUE;

    public boolean isFresh() {
        return (query == null || query.trim().isEmpty())
                && (room == null || room.trim().isEmpty())
                && (category == null || category.trim().isEmpty())
                && (productAttributes == null || productAttributes.isEmpty());
    }
}
