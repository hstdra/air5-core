package com.example.air5core.models.others;

import lombok.Data;

import java.util.List;

@Data
public class ProductMeta {
    private List<TagCount> rooms;
    private List<TagCount> categories;
    private List<TagCount> attributes;
    private List<TagCount> values;
}
