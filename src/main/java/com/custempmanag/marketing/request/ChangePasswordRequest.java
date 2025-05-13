package com.custempmanag.marketing.request;

import com.custempmanag.marketing.annotation.PasswordMatches;
import com.custempmanag.marketing.validator.PasswordConfirmable;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.Data;


@PasswordMatches
@Data
public class ChangePasswordRequest{
    @NotBlank(message = "{password.current.notblank}")
    private String currentPassword;

    @NotBlank(message = "{password.new.notblank}")
    @Size(min = 6, message = "{password.new.size}")
    private String newPassword;

    @NotBlank(message = "{password.confirm.notblank}")
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
