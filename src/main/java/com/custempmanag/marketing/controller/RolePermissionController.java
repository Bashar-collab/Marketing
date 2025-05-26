package com.custempmanag.marketing.controller;

import com.custempmanag.marketing.request.RoleRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.service.PermissionService;
import com.custempmanag.marketing.service.RolePermissionService;
import com.custempmanag.marketing.service.RoleService;
import jakarta.validation.Valid;
import org.checkerframework.checker.units.qual.A;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('SUPER_ADMIN')")
public class RolePermissionController {

    @Autowired
    private RolePermissionService rolePermissionService;

    // Endpoint to create a role with specific permissions
    @PostMapping("/roles/permissions")
    public ResponseEntity<MessageResponse> createRoleWithPermissions(@RequestBody RoleRequest request) {
        // Create role and associate it with permissions
        MessageResponse messageResponse = rolePermissionService.createRoleWithPermissions(request);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @PatchMapping("/{roleId}/permissions")
    public ResponseEntity<MessageResponse> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody @Valid RoleRequest request
    ) {
        MessageResponse messageResponse = rolePermissionService.assignPermissionsToRole(roleId, request.getPermissions());
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @GetMapping("/permissions/{roleId}/roles")
    public ResponseEntity<MessageResponse> getRolePermissions(@PathVariable Long roleId) {
        MessageResponse messageResponse = rolePermissionService.getPermissionsWithThisRole(roleId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @PostMapping("/permissions/{permissionId}/roles/{roleId}")
    public ResponseEntity<MessageResponse> assignRoleToPermission(@PathVariable Long roleId, @PathVariable Long permissionId) {
        MessageResponse messageResponse = rolePermissionService.assignPermissionToRole(roleId, permissionId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }

    @DeleteMapping("/permissions/{permissionId}/roles/{roleId}")
    public ResponseEntity<MessageResponse> deleteRolePermission(@PathVariable Long roleId, @PathVariable Long permissionId) {
        MessageResponse messageResponse = rolePermissionService.removePermissionFromRole(roleId, permissionId);
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }
}
