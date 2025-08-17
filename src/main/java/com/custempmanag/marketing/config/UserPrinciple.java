package com.custempmanag.marketing.config;

import com.custempmanag.marketing.model.User;
import lombok.Data;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;

import java.nio.file.attribute.UserPrincipal;
import java.util.Collection;
import java.util.Collections;
import java.util.List;

@Data
@RequiredArgsConstructor
public class UserPrinciple implements UserDetails {

    @Getter
    private final Long id;
    private final String username;
    private final String password;
//    private final Long profileId;
    private final Collection<? extends GrantedAuthority> authorities;

    public static UserPrinciple create(User user) {
        List<GrantedAuthority> authorities = Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + user.getRole().getName().toUpperCase())
        );
        return new UserPrinciple(
                user.getId(),
                user.getUsername(),
                user.getPassword(),
//                user.getProfileId(),
                authorities // Or fetch roles
        );
    }

    @Override
    public Collection<? extends GrantedAuthority> getAuthorities() {
        return authorities;
    }

    @Override
    public String getPassword() {
        return password;
    }

    @Override
    public String getUsername() {
        return username;
    }


    public boolean isAdmin() {
        return getAuthorities().stream()
                .map(GrantedAuthority::getAuthority)
                .anyMatch(role -> role.equals("ROLE_ADMIN") || role.equals("ROLE_SUPER_ADMIN"));
    }


}
