package com.custempmanag.marketing.response;

import com.custempmanag.marketing.model.Role;
import com.fasterxml.jackson.annotation.JsonIgnore;
import com.fasterxml.jackson.annotation.JsonView;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
public class UserResponse {

    private Long id;

    private String username;

    private String email;

    private String phoneNumber;

    private String address;

    @JsonIgnore
    private boolean verified;

    @JsonIgnore
    private Long profileId;

    @JsonIgnore
    private String profileType;

    @JsonIgnore
    private Role role;

    @JsonIgnore
    private String roleName;

    private String profilePicture;

    private String preferredLanguage;

    private Instant createdAt;

    private Instant updatedAt;
}

