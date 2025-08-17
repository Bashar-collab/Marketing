package com.custempmanag.marketing.response;

import lombok.Data;

@Data
public class OwnerResponse extends UserResponse {
    private String bio;
    private Double rate;
}
