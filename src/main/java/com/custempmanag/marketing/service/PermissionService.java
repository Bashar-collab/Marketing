package com.custempmanag.marketing.service;

import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Permission;
import com.custempmanag.marketing.model.Role;
import com.custempmanag.marketing.repository.PermissionRepository;
import com.custempmanag.marketing.repository.RoleRepository;
import com.custempmanag.marketing.request.RoleRequest;
import com.custempmanag.marketing.response.MessageResponse;
import jakarta.transaction.Transactional;
import lombok.Data;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

@Data
@Service
public class PermissionService {
    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;

    @Transactional
    public MessageResponse createPermission(RoleRequest roleRequest) {

        Permission permission = new Permission();
        permission.setCode(roleRequest.getCode());
        permission.setDescription(roleRequest.getDescription());
        permissionRepository.save(permission);

        return new MessageResponse(HttpStatus.CREATED.toString(), "Permission created successfully", null);
    }

    @Transactional
    public MessageResponse updatePermission(Long permissionId, RoleRequest roleRequest) {
        Permission permission = permissionRepository.findById(permissionId)
                        .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        permission.setCode(roleRequest.getCode());
        permission.setDescription(roleRequest.getDescription());
        permissionRepository.save(permission);

        return new MessageResponse(HttpStatus.OK.toString(), "Permission updated successfully", null);
    }

    @Transactional
    public MessageResponse deletePermission(Long permissionId) {
        permissionRepository.deleteById(permissionId);
        return new MessageResponse(HttpStatus.OK.toString(), "Permission deleted successfully", null);
    }

    // MUST CHECK THIS NECESSARY
    public MessageResponse getAllPermissions() {
        Set<Permission> permissions = permissionRepository.findAll();
        return new MessageResponse(HttpStatus.OK.toString(), "Permissions found", permissions);
    }

    public MessageResponse getPermissionById(Long permissionId) {
        Permission permission = permissionRepository.findById(permissionId)
                .orElseThrow(() -> new ResourceNotFoundException("Permission not found"));
        return new MessageResponse(HttpStatus.OK.toString(), "Permission found", permission);
    }
}
