package com.zidtech.auth.controller;

import com.zidtech.auth.model.User;
import com.zidtech.auth.service.AuthService;
import com.zidtech.common.security.model.RefreshToken;
import com.zidtech.common.security.service.RefreshTokenService;
import com.zidtech.common.security.service.SecurityUserService;
import com.zidtech.common.security.util.CookieUtil;
import com.zidtech.common.security.util.JwtUtil;
import com.zidtech.common.security.util.TokenPair;
import jakarta.servlet.http.HttpServletResponse;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.CookieValue;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;
@RestController
@RequestMapping("/api/auth")
@RequiredArgsConstructor
public class AuthController {

    private final AuthService authService;
    private final RefreshTokenService refreshTokenService;
    private final JwtUtil jwtUtil;

    @PostMapping("/signup")
    public ResponseEntity<?> signup(@RequestBody Map<String, String> body,
                                    HttpServletResponse res) {

        User user = authService.signup(body.get("username"), body.get("password"));
        RefreshToken refresh = refreshTokenService.issue(user.getUsername());

        TokenPair pair = jwtUtil.generate(user.getUsername(), refresh.getToken());
        CookieUtil.add(res, "ACCESS_TOKEN", pair.getAccessToken(), 3600);
        CookieUtil.add(res, "REFRESH_TOKEN", pair.getRefreshToken(), 7 * 24 * 3600);

        return ResponseEntity.status(201).body("User registered");
    }

    @PostMapping("/signin")
    public ResponseEntity<?> signin(@RequestBody Map<String, String> body,
                                    HttpServletResponse res) {

        User user = authService.authenticate(body.get("username"), body.get("password"));
        RefreshToken refresh = refreshTokenService.issue(user.getUsername());

        TokenPair pair = jwtUtil.generate(user.getUsername(), refresh.getToken());
        CookieUtil.add(res, "ACCESS_TOKEN", pair.getAccessToken(), 3600);
        CookieUtil.add(res, "REFRESH_TOKEN", pair.getRefreshToken(), 7 * 24 * 3600);

        return ResponseEntity.ok("Login success");
    }

    @PostMapping("/refresh")
    public ResponseEntity<?> refresh(@CookieValue("REFRESH_TOKEN") String token,
                                     HttpServletResponse res) {

        RefreshToken rotated = refreshTokenService.rotate(token);
        TokenPair pair = jwtUtil.generate(rotated.getUsername(), rotated.getToken());

        CookieUtil.add(res, "ACCESS_TOKEN", pair.getAccessToken(), 3600);
        CookieUtil.add(res, "REFRESH_TOKEN", pair.getRefreshToken(), 7 * 24 * 3600);

        return ResponseEntity.ok("Token refreshed");
    }

    @PostMapping("/logout")
    public ResponseEntity<?> logout(Authentication auth, HttpServletResponse res) {

        if (auth != null) {
            refreshTokenService.invalidateAll(auth.getName());
        }

        CookieUtil.clear(res, "ACCESS_TOKEN");
        CookieUtil.clear(res, "REFRESH_TOKEN");

        return ResponseEntity.ok("Logged out");
    }
}
