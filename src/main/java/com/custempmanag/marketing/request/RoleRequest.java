package com.custempmanag.marketing.request;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotEmpty;
import lombok.Data;

import java.util.Set;

@Data
public class RoleRequest {
    private String name;
    private String description;
    private String code;
    @NotEmpty
    private Set<@NotBlank String> permissions;

}
