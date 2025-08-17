package com.custempmanag.marketing.response;

import com.custempmanag.marketing.model.Customer;
import com.custempmanag.marketing.model.Role;
import lombok.Data;

@Data
public class AdminUserResponse {
    private Long id;
    private String username;
    private String email;
    private String phoneNumber;
    private String address;
    private Role role;
    private String roleName;
    private boolean verified;
    private Long profileId;
    private String profileType;
    private String bio;
    private Double rate;
    private Customer referredByPhoneNumber;
    private String referralCode;
//    private String phoneNumber;
}
