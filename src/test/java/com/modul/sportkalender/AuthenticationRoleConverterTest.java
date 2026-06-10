package com.modul.sportkalender;

import com.modul.sportkalender.security.AuthenticationRoleConverter;
import com.modul.sportkalender.security.Roles;
import org.junit.jupiter.api.Test;
import org.springframework.security.core.Authentication;
import org.springframework.security.oauth2.jwt.Jwt;

import java.time.Instant;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertTrue;

class AuthenticationRoleConverterTest {

    private final AuthenticationRoleConverter converter = new AuthenticationRoleConverter("sportkalender-frontend");

    @Test
    void extractsRealmRoles() {
        Jwt jwt = jwtWithClaims(Map.of(
                "realm_access", Map.of("roles", List.of("ROLE_USER", "ROLE_ADMIN"))
        ));

        Authentication authentication = converter.convert(jwt);

        assertTrue(hasAuthority(authentication, Roles.USER));
        assertTrue(hasAuthority(authentication, Roles.ADMIN));
    }

    @Test
    void extractsClientRolesAndSupportsLegacyReadUpdateRoles() {
        Jwt jwt = jwtWithClaims(Map.of(
                "resource_access", Map.of(
                        "sportkalender-frontend", Map.of("roles", List.of("READ", "UPDATE"))
                )
        ));

        Authentication authentication = converter.convert(jwt);

        assertTrue(hasAuthority(authentication, Roles.READ));
        assertTrue(hasAuthority(authentication, Roles.UPDATE));
        assertTrue(hasAuthority(authentication, Roles.USER));
        assertTrue(hasAuthority(authentication, Roles.ADMIN));
    }

    private Jwt jwtWithClaims(Map<String, Object> claims) {
        Jwt.Builder builder = Jwt.withTokenValue("token")
                .header("alg", "none")
                .issuedAt(Instant.now())
                .expiresAt(Instant.now().plusSeconds(60));
        claims.forEach(builder::claim);
        return builder.build();
    }

    private boolean hasAuthority(Authentication authentication, String authority) {
        return authentication.getAuthorities().stream()
                .anyMatch(grantedAuthority -> authority.equals(grantedAuthority.getAuthority()));
    }
}
