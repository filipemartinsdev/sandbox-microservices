package com.auth_service.security.application.service;

import com.auth_service.security.application.dto.*;
import com.auth_service.security.domain.RefreshToken;
import com.auth_service.security.domain.UserCredentials;
import com.auth_service.security.domain.UserRole;
import com.auth_service.security.infra.persistence.RefreshTokenRepository;
import com.auth_service.security.infra.persistence.UserCredentialsRepository;
import jakarta.transaction.Transactional;
import jakarta.validation.Valid;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.oauth2.jwt.*;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Set;
import java.util.UUID;

@Service
public class AuthService {
    private static final long ACCESS_TOKEN_EXPIRATION_SECONDS = 3600;
    private static final long REFRESH_TOKEN_EXPIRATION_SECONDS = 604_800; // ONE WEEK
    private static final String ISSUER = "AUTH_MICROSERVICE";

    private final UserCredentialsRepository userCredentialsRepository;
    private final RefreshTokenRepository refreshTokenRepository;
    private final PasswordEncoder passwordEncoder;
    private final JwtEncoder jwtEncoder;
    private final JwtDecoder jwtDecoder;

    public AuthService(
            UserCredentialsRepository userCredentialsRepository,
            RefreshTokenRepository refreshTokenRepository,
            PasswordEncoder passwordEncoder,
            JwtEncoder jwtEncoder,
            JwtDecoder jwtDecoder
    ) {
        this.userCredentialsRepository = userCredentialsRepository;
        this.refreshTokenRepository = refreshTokenRepository;
        this.passwordEncoder = passwordEncoder;
        this.jwtEncoder = jwtEncoder;
        this.jwtDecoder = jwtDecoder;
    }

    public LoginResponse login(@Valid LoginRequest request) {
        UserCredentials userCredentials = userCredentialsRepository.findByEmail(request.email())
                .orElseThrow(() -> new BadCredentialsException("Invalid email or password"));

        if(!isLoginCorrect(request, userCredentials))
            throw new BadCredentialsException("Invalid email or password");

        return generateTokens(userCredentials.getUserId());
    }

    private boolean isLoginCorrect(LoginRequest request, UserCredentials userCredentials) {
        return passwordEncoder.matches(request.password(), userCredentials.getEncryptedPassword());
    }

    private LoginResponse generateTokens(UUID userId) {
        Token accessToken = generateAccessToken(userId);
        Token refreshToken = generateRefreshToken(userId);

        return new LoginResponse(accessToken, refreshToken);
    }

    private Token generateAccessToken(UUID userId){
        var now = Instant.now();
        var expiration = now.plusSeconds(ACCESS_TOKEN_EXPIRATION_SECONDS);

        var claims = JwtClaimsSet.builder()
                .issuer(ISSUER)
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(expiration)
                .claim("type", "access_token")
                .build();

        String jwtValue = jwtEncoder.encode(
                JwtEncoderParameters.from(claims)
        ).getTokenValue();

        return new Token(jwtValue, expiration);
    }

    private Token generateRefreshToken(UUID userId){
        var now = Instant.now();
        var expiration = now.plusSeconds(REFRESH_TOKEN_EXPIRATION_SECONDS);

        UUID tokenId = UUID.randomUUID();

        refreshTokenRepository.save(new RefreshToken(tokenId, userId, now, expiration, false));

        var claims = JwtClaimsSet.builder()
                .id(tokenId.toString())
                .issuer(ISSUER)
                .subject(userId.toString())
                .issuedAt(now)
                .expiresAt(expiration)
                .claim("type", "refresh_token")
                .build();

        String jwtValue = jwtEncoder.encode(
                JwtEncoderParameters.from(claims)
        ).getTokenValue();

        return new Token(jwtValue, expiration);
    }

    @Transactional
    public LoginResponse refresh(@Valid RefreshRequest request) {
        Jwt jwt = decodeToken(request.refreshToken());

        UUID tokenId = getTokenId(jwt);
        UUID userId = getUserId(jwt);
        String type = getClaimType(jwt);

        if (!"refresh_token".equals(type) || !userCredentialsRepository.existsById(userId))
            throw new BadJwtException("Invalid refresh token");

        revokeRefreshToken(tokenId);

        Token newAccessToken = generateAccessToken(userId);
        Token newRefreshToken = generateRefreshToken(userId);

        return new LoginResponse(newAccessToken, newRefreshToken);
    }

    private Jwt decodeToken(String token) {
        try {
            return jwtDecoder.decode(token);
        }
        catch (Exception e){
            throw new BadJwtException("Invalid token");
        }
    }

    private UUID getTokenId(Jwt jwt){
        try {
            return UUID.fromString(jwt.getId());
        } catch (Exception e){
            throw new BadJwtException("Invalid token");
        }
    }

    private UUID getUserId(Jwt jwt){
        try {
            return UUID.fromString(jwt.getSubject());
        } catch (Exception e){
            throw new BadJwtException("Invalid token");
        }
    }

    private String getClaimType(Jwt jwt){
        try {
            return jwt.getClaimAsString("type");
        } catch (Exception e){
            throw new BadJwtException("Invalid token");
        }
    }

    private void revokeRefreshToken(UUID tokenId){
        RefreshToken refreshToken = refreshTokenRepository.findById(tokenId)
                .orElseThrow(() -> new BadJwtException("Invalid refresh token"));

        if (refreshToken.isRevoked())
            throw new BadJwtException("Refresh token is revoked");

        refreshToken.setRevoked(true);

        refreshTokenRepository.save(refreshToken);
    }

    public void register(@Valid RegisterRequest registerRequest) {
        if(userCredentialsRepository.existsByEmail(registerRequest.email()))
            throw new BadCredentialsException("Email in use");

        String encryptedPassword = passwordEncoder.encode(registerRequest.password());

        var user = new UserCredentials();
        user.setEmail(registerRequest.email());
        user.setEncryptedPassword(encryptedPassword);
        user.setRole(UserRole.USER);

        userCredentialsRepository.save(user);
    }
}
