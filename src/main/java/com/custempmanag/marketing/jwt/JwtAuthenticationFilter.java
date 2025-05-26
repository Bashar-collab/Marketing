package com.custempmanag.marketing.jwt;

import com.custempmanag.marketing.config.JwtConfig;
import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.exception.ResourceNotFoundException;
import com.custempmanag.marketing.model.Permission;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.service.TokenBlacklistService;
import com.custempmanag.marketing.service.UserService;
import io.jsonwebtoken.JwtParser;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.*;
import java.util.stream.Collectors;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {
    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtConfig jwtConfig;

    @Autowired
    private JwtParser jwtParser;

    private final TokenBlacklistService tokenBlacklistService;
    @Autowired
    private UserService userService;

    public JwtAuthenticationFilter(JwtConfig jwtConfig, UserDetailsService userDetailsService,
                                   TokenBlacklistService tokenBlacklistService) {
        this.jwtConfig = jwtConfig;
        this.userDetailsService = userDetailsService;
        this.tokenBlacklistService = tokenBlacklistService;
    }

    @Override
    protected boolean shouldNotFilter(HttpServletRequest request) {
        return  request.getServletPath().startsWith("/api/auth/login") ||
                request.getServletPath().startsWith("/api/auth/register") ||
                request.getServletPath().startsWith("/api/files/**");
    }

    @Override
    protected void doFilterInternal(HttpServletRequest request, HttpServletResponse response, FilterChain filterChain) throws ServletException, IOException {
        String authHeader = request.getHeader("Authorization");

        if (authHeader != null && authHeader.startsWith("Bearer ")) {

            String token = authHeader.substring(7);

            // Check if token is blacklisted in Redis
            if (tokenBlacklistService.isTokenBlacklisted(token)) {
                filterChain.doFilter(request, response);
                return;
            }

            String username = jwtConfig.extractUsername(token, jwtParser);

            if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
                UserDetails userDetails = userDetailsService.loadUserByUsername(username);

                if (jwtConfig.validateToken(token, userDetails, jwtParser)) {

                    // Extract roles from token
//                    String role = jwtConfig.extractRoles(token, jwtParser);
                    String role = userService.getUserRoles(username);

                    // Create authority list
                    List<GrantedAuthority> authorities = new ArrayList<>();
                            authorities.add(new SimpleGrantedAuthority("ROLE_" + role.toUpperCase()));

                    User user = userService.getRoleAndPermissions(username)
                            .orElseThrow(() -> new ResourceNotFoundException("User " + username + " not found"));
                    Set<Permission> permissions = user.getRole().getPermissions();

                    permissions.forEach(perm ->
                            authorities.add(new SimpleGrantedAuthority(perm.getCode())));
                    UsernamePasswordAuthenticationToken authentication = new UsernamePasswordAuthenticationToken(userDetails, null, authorities);
                    authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
                    SecurityContextHolder.getContext().setAuthentication(authentication);
                }
            }

        }
        filterChain.doFilter(request, response);
    }
}