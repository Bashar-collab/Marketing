package com.custempmanag.marketing.request;

import com.custempmanag.marketing.annotation.PasswordMatches;
import com.custempmanag.marketing.validator.PasswordConfirmable;
import jakarta.persistence.Column;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import jakarta.validation.constraints.Size;
import lombok.Data;

//@PasswordMatches
@Data
public class RegisterRequest {
    @NotBlank(message = "Username cannot be empty")
    @Size(min = 3, max = 20, message = "Username must be between 3 and 20 characters")
    @Column(unique = true, nullable = false)
    private String username;

    //    @NotBlank(message = "Email cannot be empty")
//    @Email(message = "Invalid Email")  // Ensure email is in a valid format
//    @Column(unique = true)
//    private String email;

    @NotBlank(message = "Password cannot be empty")
    @Size(min = 6, message = "Password must be at least 6 characters")
    private String password;

    @NotBlank(message = "Phone number cannot be empty")
    @Column(unique = true, name = "phone_number")  // Unique constraint on phone number
    @Pattern(regexp = "^09\\d{8}$") // Regex for phone number that should start with 09 and should be 10 digits
    private String phoneNumber;

    private String address;

    private String profileType;
}
