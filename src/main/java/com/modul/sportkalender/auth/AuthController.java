package com.modul.sportkalender.auth;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.Map;

@RestController
@RequestMapping("api/auth")
public class AuthController {

    @GetMapping("claims")
    public Map<String, Object> getClaims(@AuthenticationPrincipal Jwt jwt) {
        return jwt.getClaims();
    }
}
