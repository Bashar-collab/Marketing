package com.custempmanag.marketing.response;

import com.custempmanag.marketing.model.Role;
import jakarta.persistence.Column;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.time.Instant;

@Data
public class UserResponse {
    private String id;

    private String username;

    private String email;

    private String phoneNumber;

    private String address;

    private boolean verified;

    private Long profileId;

    private String profileType;

    private Role role;

    private String profilePicture;

    private String preferredLanguage;

    private Instant createdAt;

    private Instant updatedAt;

}
