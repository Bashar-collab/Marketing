package com.custempmanag.marketing.service;

import com.custempmanag.marketing.exception.CustomException;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Permission;
import com.custempmanag.marketing.model.Role;
import com.custempmanag.marketing.repository.PermissionRepository;
import com.custempmanag.marketing.repository.RoleRepository;
import com.custempmanag.marketing.repository.UserRepository;
import com.custempmanag.marketing.request.RoleRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.response.RoleResponse;
import jakarta.transaction.Transactional;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
@Data
@RequiredArgsConstructor
public class RolePermissionService {

    private final RoleService roleService;
    private final PermissionService permissionService;
    private final UserService userService;
    private final RoleRepository roleRepository;
    private final PermissionRepository permissionRepository;
    private final UserRepository userRepository;


    @Transactional
    public MessageResponse createRoleWithPermissions(RoleRequest roleRequest) {
        // Validate if role already exists
        Role role = roleRepository.findByName(roleRequest.getName())
                .orElseThrow(() -> new ResourceNotFoundException("Role not found"));

        // Fetch permissions from DB
        Set<Permission> permissions = permissionRepository.findByCodeIn(roleRequest.getPermissions());
        if (permissions.size() != roleRequest.getPermissions().size()) {
            throw new CustomException("One or more permission codes are invalid!");
        }

        // Build and save the role
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        role.setPermissions(permissions);
        roleRepository.save(role);
        return new MessageResponse(HttpStatus.CREATED.toString(), "Role with permissions created successfully!", RoleResponse.fromEntity(role));

    }

    // Assign additional permissions to an existing role
    @Transactional
    public MessageResponse assignPermissionsToRole(Long roleId, Set<String> permissionCodes) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found!"));

        Set<Permission> newPermissions = permissionRepository.findByCodeIn(permissionCodes);
        role.getPermissions().addAll(newPermissions);

        roleRepository.save(role);
        return new MessageResponse(HttpStatus.OK.toString(), "Permissions assigned successfully!", RoleResponse.fromEntity(role));
    }

    public MessageResponse getPermissionsWithThisRole(Long roleId)
    {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(() -> new ResourceNotFoundException("Role not found!"));

        Set<Permission> permissions = role.getPermissions();

        return new MessageResponse(HttpStatus.OK.toString(), "Permissions with this role is retrieved successfully!", permissions);

    }
}
