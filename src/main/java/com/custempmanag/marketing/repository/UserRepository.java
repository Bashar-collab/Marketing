package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.User;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.repository.CrudRepository;

import java.util.Optional;

public interface UserRepository extends JpaRepository<User, Long> {
        Optional<User> findByUsername(String username);

        boolean existsByUsername(String username);

        boolean existsByPhoneNumber(String phoneNumber);
}
