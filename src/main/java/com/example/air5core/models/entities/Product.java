package com.example.air5core.models.entities;

import com.example.air5core.models.others.ProductAttribute;
import lombok.Data;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.index.TextIndexed;
import org.springframework.data.mongodb.core.mapping.Document;
import org.springframework.data.mongodb.core.mapping.TextScore;

import java.util.List;

@Data
@Document
public class Product {
    @Id
    private String id;
    @TextIndexed
    private String name;
    @TextIndexed
    private String description;
    private String category;
    private String room;
    private double originalPrice;
    private double discountPrice;
    private String arLink;
    private String image;
    private List<ProductAttribute> productAttributes;
    @TextScore
    private double score;
}
