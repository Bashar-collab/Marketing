package com.custempmanag.marketing.repository;

import com.custempmanag.marketing.model.RefreshToken;
import com.custempmanag.marketing.model.User;
import jakarta.transaction.Transactional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.CrudRepository;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshToken, Long> {
    Optional<RefreshToken> findByToken(String token);

    Optional<RefreshToken> findByUserId(Long userId);

    boolean existsByUser(User user);

//    @Modifying
//    @Transactional
//    @Query("DELETE FROM RefreshToken r WHERE r.user = :user")
    void deleteByUser(User user);
}
