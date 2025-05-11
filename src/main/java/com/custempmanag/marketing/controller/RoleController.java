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
@RequestMapping("/api/admin/roles")
@PreAuthorize("hasRole('ADMIN')")
public class RoleController {

    @Autowired
    private RoleService roleService;

    @PostMapping
    public ResponseEntity<MessageResponse> createRole(@RequestBody RoleRequest roleRequest) {
        MessageResponse messageResponse = roleService.createRole(roleRequest);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @PutMapping("/{roleId}")
    public ResponseEntity<MessageResponse> updateRole(@PathVariable Long roleId,
                                                      @RequestBody RoleRequest roleRequest) {
        MessageResponse messageResponse = roleService.updateRole(roleId, roleRequest);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @DeleteMapping("/{roleId}")
    public ResponseEntity<MessageResponse> deleteRole(@PathVariable Long roleId) {
        MessageResponse messageResponse = roleService.deleteRole(roleId);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @GetMapping()
    public ResponseEntity<MessageResponse> getRoles() {
        MessageResponse messageResponse = roleService.getAllRoles();
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @GetMapping("/{roleId}")
    public ResponseEntity<MessageResponse> getRole(@PathVariable Long roleId) {
        MessageResponse messageResponse = roleService.getRole(roleId);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }


    @PostMapping("/{roleId}/users/{userId}")
    public ResponseEntity<MessageResponse> addUserToRole(@PathVariable Long roleId, @PathVariable Long userId) {
        MessageResponse messageResponse = roleService.assignUserToRole(roleId, userId);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    @GetMapping("/{roleId}/users")
    public ResponseEntity<MessageResponse> getUsersInRole(@PathVariable Long roleId) {
        MessageResponse messageResponse = roleService.getUsersWithRole(roleId);
        return new ResponseEntity<>(messageResponse, HttpStatus.OK);
    }

    /*
        Endpoint	HTTP Method	Action
        POST /roles/{roleId}/users/{userId}	POST	Assign role to user
        DELETE /roles/{roleId}/users/{userId}	DELETE	Remove role from user
        GET /roles/{roleId}/users	GET	List all users with this role

     */







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


