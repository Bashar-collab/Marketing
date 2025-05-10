package com.custempmanag.marketing.response;

import com.custempmanag.marketing.model.Permission;
import lombok.AllArgsConstructor;
import lombok.Data;

import java.util.List;
import java.util.Set;

@Data
@AllArgsConstructor
public class LoginResponse {
    private String token;
    private String role;
    private Set<String> permissions;
}
