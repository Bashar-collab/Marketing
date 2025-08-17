package com.custempmanag.marketing.response;

import com.custempmanag.marketing.model.Customer;
import com.fasterxml.jackson.annotation.JsonView;
import lombok.Data;

@Data
public class CustomerResponse extends UserResponse {

    @JsonView(Views.Public.class)
    private Customer referredById;

    @JsonView(Views.Public.class)
    private String referralCode;

    @JsonView(Views.Public.class)
    private String loyaltyPoints;
}
