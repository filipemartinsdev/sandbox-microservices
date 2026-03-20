package com.auth_service.security.application.dto;

import java.time.Instant;

public record Token (String token, Instant expiration) {
}
