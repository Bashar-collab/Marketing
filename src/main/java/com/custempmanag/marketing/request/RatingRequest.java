package com.custempmanag.marketing.request;


import lombok.Data;

@Data
public class RatingRequest {
    private double ratingValue = 1;
    private String data;
}
