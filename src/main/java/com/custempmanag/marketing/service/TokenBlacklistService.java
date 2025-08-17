package com.custempmanag.marketing.service;

import com.github.benmanes.caffeine.cache.Cache;
import com.github.benmanes.caffeine.cache.Caffeine;
//import com.google.common.cache.Cache;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Service;

import java.util.concurrent.TimeUnit;

@Service
public class TokenBlacklistService {

    private static final Logger log = LoggerFactory.getLogger(TokenBlacklistService.class);


    /*
    private final RedisTemplate<String, String> redisTemplate;

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    public void blacklistToken(String token, long expirationTimeMillis) {
        // Store token with TTL equal to token's remaining validity
        redisTemplate.opsForValue().set(token, "blacklisted", expirationTimeMillis, TimeUnit.MILLISECONDS);
    }

    public boolean isTokenBlacklisted(String token) {
        return Boolean.TRUE.equals(redisTemplate.hasKey(token));
    }
     */
    private final RedisTemplate<String, String> redisTemplate;
    private final Cache<String, Boolean> caffeineCache; // Backup

    public TokenBlacklistService(RedisTemplate<String, String> redisTemplate) {
        this.redisTemplate = redisTemplate;
        this.caffeineCache = Caffeine.newBuilder()
                .expireAfterWrite(5, TimeUnit.MINUTES) // Keep for 5 mins
                .build();
    }

    public void blacklistToken(String token, long expiryMs) {
        // (1) Store in Redis (primary)
        caffeineCache.put(token, true);
        redisTemplate.opsForValue().set(token, "blacklisted", expiryMs, TimeUnit.MILLISECONDS);
        // (2) Backup in Caffeine
    }

    public boolean isTokenBlacklisted(String token) {
        // (1) Check Caffeine first (fast + works offline)
        if (Boolean.TRUE.equals(caffeineCache.getIfPresent(token))) {
            return true; // Block token
        }

        // (2) Try Redis (if available)
        try {
            return Boolean.TRUE.equals(redisTemplate.hasKey(token));
        } catch (Exception e) {
            log.error("REDIS DOWN! Token bypassed (not in Caffeine).");
            return false; // Allow token (fail-open)
        }
    }
}
