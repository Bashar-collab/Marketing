package com.custempmanag.marketing.controller;

import com.custempmanag.marketing.request.RoleRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.service.PermissionService;
import com.custempmanag.marketing.service.RoleService;
import jakarta.validation.Valid;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/admin/permissions")
@PreAuthorize("hasRole('ADMIN')")
public class PermissionController {

    @Autowired
    private PermissionService permissionService;


    @PostMapping()
    public ResponseEntity<MessageResponse> createPermission(@RequestBody RoleRequest roleRequest) {
        MessageResponse messageResponse = permissionService.createPermission(roleRequest);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @PutMapping("/{permissionId}")
    public ResponseEntity<MessageResponse> updatePermission(@PathVariable Long permissionId,
                                                            @RequestBody RoleRequest roleRequest) {
        MessageResponse messageResponse = permissionService.updatePermission(permissionId, roleRequest);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{permissionId}")
    public ResponseEntity<MessageResponse> deletePermission(@PathVariable Long permissionId) {
        MessageResponse messageResponse = permissionService.deletePermission(permissionId);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<MessageResponse> getPermissions() {
        MessageResponse messageResponse = permissionService.getAllPermissions();
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @GetMapping("/{permissionId}")
    public ResponseEntity<MessageResponse> getPermissionById(@PathVariable Long permissionId) {
        MessageResponse messageResponse = permissionService.getPermissionById(permissionId);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    /*
        Endpoint	HTTP Method	Action same but remove roles and put permissions
        POST /roles/{roleId}/users/{userId}	POST	Assign role to user
        DELETE /roles/{roleId}/users/{userId}	DELETE	Remove role from user
        GET /roles/{roleId}/users	GET	List all users with this role

     */

}


