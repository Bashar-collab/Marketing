package com.custempmanag.marketing.service;

import com.custempmanag.marketing.config.UserPrinciple;
import com.custempmanag.marketing.model.User;
import com.custempmanag.marketing.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.cache.annotation.Cacheable;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import javax.management.relation.Role;
import java.util.Collection;
import java.util.Collections;
import java.util.stream.Collectors;

@Service
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    @Cacheable(value = "userDetailsCache", key = "#username")
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        User user = userRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return UserPrinciple.create(user);
    }

    private Collection<? extends GrantedAuthority> getAuthorities(String profileType) {
        return Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_" + profileType.toUpperCase())
        );
    }
}
