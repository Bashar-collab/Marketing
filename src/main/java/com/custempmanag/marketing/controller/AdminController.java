package com.custempmanag.marketing.controller;


import com.custempmanag.marketing.request.RoleRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin")
@PreAuthorize("hasRole('ADMIN')")
public class AdminController {

    @Autowired
    private RoleService roleService;

    // Endpoint to create a role with specific permissions
    @PostMapping("/roles")
    public ResponseEntity<MessageResponse> createRole(@RequestBody RoleRequest request) {
            // Create role and associate it with permissions
            MessageResponse messageResponse = roleService.createRoleWithPermissions(request);
            return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
        }


    @PatchMapping("/{roleId}/permissions")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<MessageResponse> assignPermissions(
            @PathVariable Long roleId,
            @RequestBody @Valid RoleRequest request
    ) {
        MessageResponse messageResponse = roleService.assignPermissionsToRole(roleId, request.getPermissions());
        return ResponseEntity.status(HttpStatus.OK).body(messageResponse);
    }
    // Endpoint to get a role's details (including permissions)
    /*
    @GetMapping("/roles/{roleName}")
    public ResponseEntity<Role> getRole(@PathVariable String roleName) {
        try {
            Role role = roleService.getRole(roleName);
            return ResponseEntity.ok(role);
        } catch (Exception e) {
            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(null);
        }
    }
     */
}


