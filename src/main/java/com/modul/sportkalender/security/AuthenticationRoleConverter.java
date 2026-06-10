package com.modul.sportkalender.security;

import org.springframework.core.convert.converter.Converter;
import org.springframework.security.authentication.AbstractAuthenticationToken;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.jwt.Jwt;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;
import org.springframework.security.oauth2.server.resource.authentication.JwtGrantedAuthoritiesConverter;

import java.util.Collection;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class AuthenticationRoleConverter implements Converter<Jwt, AbstractAuthenticationToken> {
    private final JwtGrantedAuthoritiesConverter defaultGrantedAuthoritiesConverter = new JwtGrantedAuthoritiesConverter();

    private final String clientId;

    public AuthenticationRoleConverter(String clientId) {
        this.clientId = clientId;
    }

    private Collection<? extends GrantedAuthority> extractKeycloakRoles(final Jwt jwt) {
        Set<String> roles = new HashSet<>();
        roles.addAll(extractRealmRoles(jwt));
        roles.addAll(extractClientRoles(jwt));

        return roles.stream()
                .flatMap(role -> normalizeRole(role).stream())
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toSet());
    }

    private Collection<String> extractRealmRoles(final Jwt jwt) {
        Map<String, Object> realmAccess = jwt.getClaim("realm_access");
        return extractRoles(realmAccess);
    }

    private Collection<String> extractClientRoles(final Jwt jwt) {
        Map<String, Object> resourceAccess = jwt.getClaim("resource_access");
        if (resourceAccess == null) {
            return Collections.emptySet();
        }

        Object clientAccess = resourceAccess.get(clientId);
        if (!(clientAccess instanceof Map<?, ?> clientAccessMap)) {
            return Collections.emptySet();
        }

        return extractRoles(clientAccessMap);
    }

    private Collection<String> extractRoles(final Map<?, ?> accessMap) {
        if (accessMap == null) {
            return Collections.emptySet();
        }

        Object roles = accessMap.get("roles");
        if (!(roles instanceof Collection<?> roleCollection)) {
            return Collections.emptySet();
        }

        return roleCollection.stream()
                .filter(String.class::isInstance)
                .map(String.class::cast)
                .collect(Collectors.toSet());
    }

    private Collection<String> normalizeRole(String role) {
        return switch (role.toUpperCase()) {
            case "ROLE_USER", "USER" -> List.of(Roles.USER);
            case "ROLE_ADMIN", "ADMIN" -> List.of(Roles.ADMIN);
            case "READ" -> List.of(Roles.READ, Roles.USER);
            case "UPDATE" -> List.of(Roles.UPDATE, Roles.ADMIN);
            default -> role.startsWith("ROLE_") ? List.of(role) : List.of("ROLE_" + role);
        };
    }

    @Override
    public AbstractAuthenticationToken convert(final Jwt source) {
        Collection<GrantedAuthority> authorities = Stream.concat(defaultGrantedAuthoritiesConverter.convert(source)
                                .stream(),
                        extractKeycloakRoles(source).stream())
                .collect(Collectors.toSet());
        return new JwtAuthenticationToken(source, authorities);
    }
}