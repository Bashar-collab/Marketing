package com.custempmanag.marketing.request;

import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.Data;

@Data
public class UserRequest {

    @NotBlank(message = "{phone.notblank}")
    @Column(unique = true, name = "phone_number")  // Unique constraint on phone number
    @Pattern(regexp = "^(\\+|00)?[1-9]\\d{7,14}$", message = "{phone.pattern}") // Regex for phone number that should start with 09 and should be 10 digits
    private String phoneNumber;

    private String email;

    private String profilePicture;

    private String address;

//    private String type;

    private String preferredLanguage;

    // Owner-specific
    private String bio;
    private Double rate;

    // Customer-specific
    private String referralCode;
    private Integer loyaltyPoints;
    private Long referredById;
}
