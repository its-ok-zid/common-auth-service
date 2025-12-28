package com.zidtech.auth.service;

import com.zidtech.auth.model.RefreshTokenEntity;
import com.zidtech.auth.model.User;
import com.zidtech.auth.repository.RefreshTokenRepository;
import com.zidtech.auth.repository.UserRepository;
import com.zidtech.common.security.model.RefreshToken;
import com.zidtech.common.security.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.time.Instant;
import java.util.Optional;
import java.util.UUID;

@Service
@RequiredArgsConstructor
public class JpaRefreshTokenService implements RefreshTokenService {

    private final RefreshTokenRepository repo;
    private final UserRepository userRepo;

    @Override
    public RefreshToken issue(String username) {
        User user = userRepo.findByUsername(username).orElseThrow();
        repo.deleteByUser(user);

        RefreshTokenEntity entity = new RefreshTokenEntity();
        entity.setToken(UUID.randomUUID().toString());
        entity.setExpiry(Instant.now().plusSeconds(7 * 24 * 3600));
        entity.setUser(user);

        repo.save(entity);

        return RefreshToken.builder()
                .token(entity.getToken())
                .username(username)
                .expiry(entity.getExpiry())
                .build();
    }

    @Override
    public Optional<RefreshToken> validate(String token) {
        return repo.findByToken(token)
                .map(t -> RefreshToken.builder()
                        .token(t.getToken())
                        .username(t.getUser().getUsername())
                        .expiry(t.getExpiry())
                        .build());
    }

    @Override
    public RefreshToken rotate(String token) {
        RefreshToken old = validate(token).orElseThrow();
        invalidateAll(old.getUsername());
        return issue(old.getUsername());
    }

    @Override
    public void invalidateAll(String username) {
        userRepo.findByUsername(username)
                .ifPresent(repo::deleteByUser);
    }
}
