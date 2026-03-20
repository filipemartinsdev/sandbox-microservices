package com.auth_service.security.domain;

import jakarta.persistence.*;
import jakarta.validation.constraints.NotNull;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.Instant;
import java.util.UUID;

@Entity @Table(name = "refresh_token")
@AllArgsConstructor @NoArgsConstructor @Data
public class RefreshToken {
    @Id
    private UUID id;

    @Column(name = "user_id")
    @NotNull
    private UUID userId;

    @Column(name = "issued_at")
    private Instant issuedAt;

    @Column(name = "expires_at")
    private Instant expiresAt;

    @NotNull
    private boolean revoked;
}
