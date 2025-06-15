package com.custempmanag.marketing.request;

import com.custempmanag.marketing.model.CurrencyTypes;

import lombok.Data;

@Data
public class OfferingFilterRequest {
    private String name;
    private Double minPrice;
    private Double maxPrice;
    private String categoryName;
    private CurrencyTypes currency;
    private Double minRating;
    private int page = 1;
    private int size = 10;
    private String sortBy = "id";
    private String direction = "asc";
} 