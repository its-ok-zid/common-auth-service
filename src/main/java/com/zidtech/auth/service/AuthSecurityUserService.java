package com.zidtech.auth.service;

import com.zidtech.auth.model.User;
import com.zidtech.auth.repository.UserRepository;
import com.zidtech.common.security.model.SecurityUser;
import com.zidtech.common.security.service.SecurityUserService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AuthSecurityUserService implements SecurityUserService {

    private final UserRepository userRepo;

    @Override
    public SecurityUser loadByUsername(String username) {
        User user = userRepo.findByUsername(username)
                .orElseThrow();

        return SecurityUser.builder()
                .id(user.getId())
                .username(user.getUsername())
                .password(user.getPassword())
                .authorities(List.of(() -> "ROLE_USER"))
                .build();
    }
}
