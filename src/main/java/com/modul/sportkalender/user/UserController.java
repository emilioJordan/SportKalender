package com.modul.sportkalender.user;

import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("api/user")
public class UserController {

    @GetMapping("me")
    public String getCurrentUser(@AuthenticationPrincipal Jwt jwt) {
        return "Angemeldet als: " + jwt.getClaimAsString("preferred_username");
    }
}
