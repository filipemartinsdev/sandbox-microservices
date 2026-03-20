package com.auth_service.security.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;
import java.util.UUID;

@Entity @Table(name = "user_credentials")
@AllArgsConstructor @NoArgsConstructor @Data
public class UserCredentials {
    @Id @GeneratedValue(strategy = GenerationType.UUID)
    @Column(name = "user_id")
    private UUID userId;

    @Email @Column(unique = true)
    private String email;

    @NotBlank
    @Column(name = "encrypted_password")
    private String encryptedPassword;

    @NotNull
    @Column(name = "user_role")
    @Enumerated(EnumType.STRING)
    private UserRole role;
}
