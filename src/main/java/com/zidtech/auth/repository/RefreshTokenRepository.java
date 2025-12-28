package com.zidtech.auth.repository;

import com.zidtech.auth.model.RefreshTokenEntity;
import com.zidtech.auth.model.User;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface RefreshTokenRepository extends JpaRepository<RefreshTokenEntity, Long> {
    Optional<RefreshTokenEntity> findByToken(String token);
    void deleteByUser(User user);
}
