package com.example.air5core.models.others;

import lombok.Data;

import java.util.List;

@Data
public class Paging<T> {
    private List<T> data;
    private long totalElements;
    private int totalPages;
    private boolean hasNext;
}
