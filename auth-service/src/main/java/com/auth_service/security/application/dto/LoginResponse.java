package com.auth_service.security.application.dto;

public record LoginResponse(Token accessToken, Token refreshToken) {
}
