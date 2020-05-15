package com.example.air5core.entities;

import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.List;

@Data
@Document
public class Product {
    @Id
    private String id;
    private String name;
    private String description;
    private String category;
    private String room;
    private double originalPrice;
    private double discountPrice;
    private String arLink;
    private String image;
    private List<ProductAttribute> productAttributes;
}
