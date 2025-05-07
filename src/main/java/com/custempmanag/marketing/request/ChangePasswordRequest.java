package com.custempmanag.marketing.request;

import com.custempmanag.marketing.annotation.PasswordMatches;
import com.custempmanag.marketing.validator.PasswordConfirmable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@PasswordMatches
@Data
public class ChangePasswordRequest{
    @NotBlank(message = "Current password is required")
    private String currentPassword;

    @NotBlank(message = "New password is required")
    @Size(min = 6, message = "New password must be at least 6 characters")
    private String newPassword;

    @NotBlank(message = "Password confirmation is required")
    private String confirmPassword;

//    // Getters and Setters
//    public String getCurrentPassword() {
//        return currentPassword;
//    }
//
//    public void setCurrentPassword(String currentPassword) {
//        this.currentPassword = currentPassword;
//    }
//
//    public String getNewPassword() {
//        return newPassword;
//    }
//
//    public void setNewPassword(String newPassword) {
//        this.newPassword = newPassword;
//    }
//    public void setConfirmPassword(String confirmPassword) {
//        this.confirmPassword = confirmPassword;
//    }
}
