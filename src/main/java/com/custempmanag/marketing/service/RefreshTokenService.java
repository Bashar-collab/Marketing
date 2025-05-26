package com.custempmanag.marketing.service;

import com.custempmanag.marketing.config.JwtConfig;
import com.custempmanag.marketing.exception.CustomException;
import com.custempmanag.marketing.model.RefreshToken;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.RefreshTokenRepository;
import com.custempmanag.marketing.repository.UserRepository;
import com.custempmanag.marketing.request.RefreshTokenRequest;
import com.custempmanag.marketing.response.RefreshTokenResponse;
import jakarta.transaction.Transactional;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import java.security.KeyPair;
import java.time.Instant;
import java.util.Map;
import java.util.UUID;

@Service
public class RefreshTokenService {

    @Value("${app.jwt.refresh-expiration-ms}")
    private Long refreshTokenDurationMs;

    @Autowired
    private RefreshTokenRepository refreshTokenRepository;

    @Autowired
    private UserRepository userRepository;
    @Autowired
    private JwtConfig jwtConfig;
    @Autowired
    private KeyPair keyPair;

    @Transactional
    public RefreshToken createRefreshToken(String username) {
        User user = userRepository.findByUsername(username).orElseThrow();

        if(refreshTokenRepository.existsByUser(user)) {
            refreshTokenRepository.deleteByUser(user);
            refreshTokenRepository.flush();
        }

        RefreshToken refreshToken = new RefreshToken();
        refreshToken.setUser(user);
        refreshToken.setToken(UUID.randomUUID().toString());
        refreshToken.setExpiryDate(Instant.now().plusMillis(refreshTokenDurationMs));
        refreshToken.setRevoked(false);

        return refreshTokenRepository.save(refreshToken);
    }

    @Transactional
    public RefreshTokenResponse getRefreshToken(RefreshTokenRequest request) {

        String token = request.getRefreshToken();
        RefreshToken refreshToken = verifyToken(token);

        RefreshToken newRefreshToken =  createRefreshToken(refreshToken.getUser().getUsername());

        String newAccessToken = jwtConfig.generateToken(refreshToken.getUser().getUsername(), keyPair);

        return new RefreshTokenResponse(newAccessToken, newRefreshToken.getToken());
    }

    private RefreshToken verifyToken(String token) {
        RefreshToken refreshToken = refreshTokenRepository.findByToken(token)
                .orElseThrow(() -> new CustomException("Invalid refresh token"));

        if (refreshToken.getExpiryDate().isBefore(Instant.now()) || refreshToken.isRevoked()) {
            throw new CustomException("Refresh token expired or revoked");
        }

        return refreshToken;
    }

    public void revokeToken(Long userId) {
        RefreshToken refreshToken = refreshTokenRepository.findByUserId(userId)
                .orElseThrow(() -> new CustomException("Invalid refresh token"));
        refreshToken.setRevoked(true);
        refreshTokenRepository.save(refreshToken);
    }
}
