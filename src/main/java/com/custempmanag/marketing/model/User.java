package com.custempmanag.marketing.model;

import jakarta.persistence.*;
import jakarta.validation.constraints.*;
import lombok.Data;
import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import java.sql.Timestamp;
import java.time.Instant;
import java.time.LocalDate;

@Entity
@Table(name = "\"user\"")
@Data
public class User {
    @Id
    @GeneratedValue(strategy = GenerationType.SEQUENCE)
    private long id;

    @Column(unique = true, nullable = false)
    private String username;

    private String email;

    private String password;

    @Column(unique = true, nullable = false)
    private String phoneNumber;

    private String address;

    @Column(nullable = false)
    private boolean verified = true;

    @Column(name = "profile_id")
    private Long profileId;

    @Column(name = "profile_type")
    private String profileType;

    @ManyToOne
    @JoinColumn(name = "role_id")
    private Role role;

    @Column(name = "profile_picture")
    private String profilePicture;

    @Column(name = "preferred_language")
    private String preferredLanguage = "en";

    @Column(name = "fcm_token", columnDefinition = "TEXT")
    private String fcmToken;

    @CreationTimestamp
    @Column(name = "created_at", updatable = false)
    private Instant createdAt;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Instant updatedAt;


}
