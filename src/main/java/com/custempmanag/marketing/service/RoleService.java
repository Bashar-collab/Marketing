package com.custempmanag.marketing.service;

import com.custempmanag.marketing.exception.CustomException;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Permission;
import com.custempmanag.marketing.model.Role;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.PermissionRepository;
import com.custempmanag.marketing.repository.RoleRepository;
import com.custempmanag.marketing.repository.UserRepository;
import com.custempmanag.marketing.request.RoleRequest;
import com.custempmanag.marketing.response.MessageResponse;
import com.custempmanag.marketing.response.RoleResponse;
import com.custempmanag.marketing.response.UserResponse;
import jakarta.transaction.Transactional;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.Optional;
import java.util.Set;
import java.util.stream.Collectors;

@Service
public class RoleService {

    @Autowired
    private RoleRepository roleRepository;

    @Autowired
    private PermissionRepository permissionRepository;
    @Autowired
    private UserRepository userRepository;
    @Autowired
    private ModelMapper modelMapper;

    @Transactional
    public MessageResponse createRole(RoleRequest roleRequest) {
        Role role = new Role();
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());

        roleRepository.save(role);

        return new MessageResponse(HttpStatus.CREATED.toString(), "Role created successfully", null);
    }

    @Transactional
    public MessageResponse updateRole(Long roleId, RoleRequest roleRequest) {

        Role role = roleRepository.findById(roleId)
                .orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        role.setName(roleRequest.getName());
        role.setDescription(roleRequest.getDescription());
        roleRepository.save(role);
        return new MessageResponse(HttpStatus.OK.toString(), "Role updated successfully", null);
    }

    @Transactional
    public MessageResponse deleteRole(Long roleId) {
        roleRepository.deleteById(roleId);
        return new MessageResponse(HttpStatus.OK.toString(), "Role deleted successfully", null);
    }

    public MessageResponse getAllRoles() {
        List<Role> roles = (List<Role>) roleRepository.findAll();
        return new MessageResponse(HttpStatus.OK.toString(), "Roles found", roles);
    }

    public MessageResponse getRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        return new MessageResponse(HttpStatus.OK.toString(), "Role found", role);
    }

    @Transactional
    public MessageResponse assignUserToRole(Long roleId, Long userId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        User user = userRepository.findById(userId)
                .orElseThrow(()-> new ResourceNotFoundException("User not found"));

        user.setRole(role);

        userRepository.save(user);

        return new MessageResponse(HttpStatus.OK.toString(), "Role assigned successfully", null);
    }

    public MessageResponse getUsersWithRole(Long roleId) {
        Role role = roleRepository.findById(roleId)
                .orElseThrow(()-> new ResourceNotFoundException("Role not found"));
        List<User> users = userRepository.findByRoleId(role.getId());

        return new MessageResponse(HttpStatus.OK.toString(), "Roles found", users.stream()
                .map(user -> modelMapper.map(user, UserResponse.class))
                .collect(Collectors.toList()));
    }



}

