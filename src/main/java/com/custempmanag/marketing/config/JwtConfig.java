package com.custempmanag.marketing.config;

import com.custempmanag.marketing.service.CustomUserDetailsService;
import com.custempmanag.marketing.utils.KeyPairUtils;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtParser;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.io.IOException;
import java.security.Key;
import java.security.KeyPair;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.function.Function;
import java.util.stream.Collectors;

@Configuration
public class JwtConfig {

    @Value("${jwt.expiration}")
    private Long expiration;

    @Autowired
    private CustomUserDetailsService customUserDetailsService;

    @Bean
    public KeyPair keyPair(
            @Value("${jwt.public-key.path}") String publicKeyPath,
            @Value("${jwt.private-key.path}") String privateKeyPath) throws IOException {
        return KeyPairUtils.getKeyPairFromFiles(publicKeyPath, privateKeyPath);
    }

    @Bean
    public JwtParser jwtParser(KeyPair keyPair) {
        return Jwts.parser()
                .setSigningKey(keyPair.getPublic())
                .build();
    }

//    @Autowired
//    private KeyPair keyPair;

    public String generateToken(String username, KeyPair keyPair) {
        Map<String, Object> claims = new HashMap<>();
        UserDetails userDetails = customUserDetailsService.loadUserByUsername(username);
//        KeyPair keyPair = new KeyPair;

        String role = userDetails.getAuthorities().stream()
                .findFirst()
                .map(GrantedAuthority::getAuthority)
                .map(auth -> auth.replace("ROLE_", ""))
                .orElse("USER");

        claims.put("role", role);
        return createToken(claims, userDetails.getUsername(), keyPair);
    }

    private String createToken(Map<String, Object> claims, String subject, KeyPair keyPair) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(subject)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(keyPair.getPrivate(), SignatureAlgorithm.RS256)
                .compact();
    }

    public Boolean validateToken(String token, UserDetails userDetails, JwtParser jwtParser) {
        final String username = extractUsername(token, jwtParser);
        return (username.equals(userDetails.getUsername()) && !isTokenExpired(token, jwtParser));
    }

    public String extractUsername(String token, JwtParser jwtParser) {
        return extractClaim(token, Claims::getSubject, jwtParser);
    }

    public Date extractExpiration(String token, JwtParser jwtParser) {
        return extractClaim(token, Claims::getExpiration, jwtParser);
    }

    public <T> T extractClaim(String token, Function<Claims, T> claimsResolver, JwtParser jwtParser) {
        final Claims claims = extractAllClaims(token, jwtParser);
        return claimsResolver.apply(claims);
    }

    private Claims extractAllClaims(String token, JwtParser jwtParser) {
        return jwtParser.parseClaimsJws(token).getBody();
    }

    private Boolean isTokenExpired(String token, JwtParser jwtParser) {
        return extractExpiration(token, jwtParser).before(new Date());
    }

    public String extractRoles(String token, JwtParser jwtParser) {
        Claims claims = extractAllClaims(token, jwtParser);
        return claims.get("role", String.class);
    }

    public long getRemainingTime(String token, KeyPair keyPair) {
        Date expiration = extractExpiration(token, jwtParser(keyPair));
        return expiration.getTime() - System.currentTimeMillis();
    }
}