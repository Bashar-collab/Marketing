package com.custempmanag.marketing.response;

import lombok.Data;

@Data
public class RatingResponse {
    private Long id;
    private double ratingValue;
    private String data;
}
