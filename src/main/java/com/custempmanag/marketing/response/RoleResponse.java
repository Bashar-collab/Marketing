package com.custempmanag.marketing.response;

import com.custempmanag.marketing.model.Role;
import com.custempmanag.marketing.model.Permission;
import lombok.Data;

import java.util.Set;
import java.util.stream.Collectors;

@Data
public class RoleResponse {
    private Long id;
    private String name;
    private String description;
    private Set<String> permissions;  // Permission codes

    public static RoleResponse fromEntity(Role role) {
        RoleResponse response = new RoleResponse();
        response.setId(role.getId());
        response.setName(role.getName());
        response.setDescription(role.getDescription());
        response.setPermissions(
                role.getPermissions().stream()
                        .map(Permission::getCode)
                        .collect(Collectors.toSet())
        );
        return response;
    }
}