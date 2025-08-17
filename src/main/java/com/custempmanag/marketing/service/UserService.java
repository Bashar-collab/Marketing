package com.custempmanag.marketing.service;

import java.util.List;
import java.util.Optional;
import java.util.Set;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.exception.CustomException;
import com.custempmanag.marketing.model.Customer;
import com.custempmanag.marketing.model.Owner;
import com.custempmanag.marketing.repository.CustomerRepository;
import com.custempmanag.marketing.repository.OwnerRepository;
import com.custempmanag.marketing.request.UserRequest;
import com.custempmanag.marketing.response.*;
import com.fasterxml.jackson.annotation.JsonView;
import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.modelmapper.ModelMapper;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.CacheEvict;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.context.MessageSource;
import org.springframework.context.i18n.LocaleContextHolder;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Permission;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.UserRepository;

import lombok.RequiredArgsConstructor;
import org.springframework.transaction.annotation.Transactional;

@Service
@RequiredArgsConstructor
public class UserService {
    private final UserRepository userRepository;
    
    private final ModelMapper modelMapper;

    private final ObjectMapper objectMapper;

    @Autowired
    private MessageSource messageSource;
    @Autowired
    private OwnerRepository ownerRepository;
    @Autowired
    private CustomerRepository customerRepository;

    @Cacheable(value = "users", key = "#username")
    public User findByUsername(String username) {
        return userRepository.findByUsername(username)
                .orElseThrow(() ->
                        new ResourceNotFoundException(messageSource.getMessage("user.not.found", null, LocaleContextHolder.getLocale()))); // I SHOULD RETURN TO HERE

    }

    @Cacheable(value = "userByIdCache", key = "#userId")
    public User validateAndGetUserById(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));
    }

    @Cacheable(value = "userRoleCache", key = "#username")
    public String getUserRoles(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + username));
        return user.getRole().getName();
    }

    @Cacheable(value = "userPermissionsCache", key = "#username")
    public Set<Permission> getUserPermissions(String username){
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with name: " + username));
        return user.getRole().getPermissions();
    }

    @Cacheable(value = "userWithRoleAndPermissionsCache", key = "#username")
    public Optional<User> getRoleAndPermissions(String username)
    {
        return userRepository.findByUsernameWithRolesAndPermissions(username);
    }

    @Cacheable(value = "allUsersCache")
    public MessageResponse getAllUsers() {
        List<User> users = userRepository.findAll();
        return new MessageResponse(HttpStatus.OK.toString(), "Users fetched successfully",
                users.stream()
                        .map(this::toUserResponse)
                        .toList());
    }


    @Cacheable(value = "userDetails", key = "#userId")
    public MessageResponse getUserDetails(Long userId, UserPrinciple currentUser) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        UserResponse responseDTO;
        String roleName = user.getRole() != null ? user.getRole().getName().toUpperCase() : null;

        if ("OWNER".equals(roleName)) {
            Owner owner = ownerRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found for user id: " + userId));
            responseDTO = modelMapper.map(owner, OwnerResponse.class);
            // Copy User fields to OwnerResponse
            modelMapper.map(user, responseDTO);
        } else if ("CUSTOMER".equals(roleName)) {
            Customer customer = customerRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user id: " + userId));
            responseDTO = modelMapper.map(customer, CustomerResponse.class);
            // Copy User fields to CustomerResponse
            modelMapper.map(user, responseDTO);
        } else {
            responseDTO = modelMapper.map(user, UserResponse.class);
        }

        return new MessageResponse(HttpStatus.OK.toString(), "User fetched successfully", responseDTO);
    }


    public MessageResponse getAdminUserDetails(Long userId) {
        User user = userRepository.findById(userId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + userId));

        AdminUserResponse responseDto = modelMapper.map(user, AdminUserResponse.class);
//        responseDto.setType(user instanceof Owner ? "Owner" : user instanceof Customer ? "Customer" : "User");

        return new MessageResponse(HttpStatus.OK.toString(), "User fetched successfully", responseDto);
    }

    @CacheEvict(value = {"userByIdCache", "userWithRoleAndPermissionsCache", "userRoleCache", "userPermissionsCache"}, key = "#userId")
    @Transactional
    public MessageResponse updateUser(Long userId, UserRequest updatedUser) {
        User existingUser = validateAndGetUserById(userId);

        // Update User fields
        if (updatedUser.getEmail() != null) {
            existingUser.setEmail(updatedUser.getEmail());
        }
        if (updatedUser.getPhoneNumber() != null) {
            existingUser.setPhoneNumber(updatedUser.getPhoneNumber());
        }
        if (updatedUser.getAddress() != null) {
            existingUser.setAddress(updatedUser.getAddress());
        }
        if (updatedUser.getProfilePicture() != null) {
            existingUser.setProfilePicture(updatedUser.getProfilePicture());
        }

        if (updatedUser.getPreferredLanguage() != null) {
            existingUser.setPreferredLanguage(updatedUser.getPreferredLanguage());
        }

        userRepository.save(existingUser);

        // Update Owner or Customer based on role
        String roleName = existingUser.getRole() != null ? existingUser.getRole().getName().toUpperCase() : null;
        if ("OWNER".equals(roleName)) {
            Owner owner = ownerRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Owner not found for user id: " + userId));
            if (updatedUser.getBio() != null) {
                owner.setBio(updatedUser.getBio());
            }
            if (updatedUser.getRate() != null) {
                owner.setRate(updatedUser.getRate());
            }
            ownerRepository.save(owner);
        } else if ("CUSTOMER".equals(roleName)) {
            Customer customer = customerRepository.findByUserId(userId)
                    .orElseThrow(() -> new ResourceNotFoundException("Customer not found for user id: " + userId));
            if (updatedUser.getReferralCode() != null) {
                customer.setReferralCode(updatedUser.getReferralCode());
            }
            if (updatedUser.getLoyaltyPoints() != null) {
                customer.setLoyaltyPoints(updatedUser.getLoyaltyPoints());
            }
            if (updatedUser.getReferredById() != null) {
                Customer referredBy = customerRepository.findById(updatedUser.getReferredById())
                        .orElseThrow(() -> new CustomException("Referred-by customer not found"));
                customer.setReferredById(referredBy);
            }
            customerRepository.save(customer);
        } else if (roleName != null) {
            throw new CustomException("Invalid role name: " + roleName);
        }

        return new MessageResponse("success", "User updated successfully", null);
    }

    // NEED TO MODIFY IT LATER.
    public MessageResponse deleteUser(Long userId) {
//        userRepository.deleteById(userId);
        return new MessageResponse("success", "User deleted successfully", userId);
    }

    public ResponseEntity<UserResponse> getProfileDetails(Long profileId)
    {
        User user = userRepository.findById(profileId)
                .orElseThrow(() -> new ResourceNotFoundException("User not found with id: " + profileId));

        return new ResponseEntity<>(modelMapper.map(user, UserResponse.class), HttpStatus.OK);
    }

    private UserResponse toUserResponse(User user) {
        UserResponse res = new UserResponse();
        res.setId(user.getId());
        res.setUsername(user.getUsername());
//        res.setProfileType(user.getProfileType());
        res.setVerified(user.isVerified());
        res.setCreatedAt(user.getCreatedAt());
        res.setPhoneNumber(user.getPhoneNumber());
        res.setRoleName(user.getRole() != null ? user.getRole().getName() : null);
        return res;
    }

}
