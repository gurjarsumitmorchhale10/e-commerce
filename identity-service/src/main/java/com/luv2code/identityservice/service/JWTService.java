package com.luv2code.identityservice.service;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.stereotype.Service;

import java.security.Key;
import java.util.*;

@Service
public class JWTService {
    public static final String SECRET = "6BA5E962F9918CA87EE4684B29983AFEC39CD9DB80A4BB87C96B5E2BC5A65957";

    public Claims validateToken(String token) {
        Jwt<Header, Claims> headerClaimsJwt = Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build().parseClaimsJwt(token);
        return headerClaimsJwt.getBody();
    }

    public String generateToken(String username, Collection<? extends GrantedAuthority> roles) {
        Map<String, ?> claims = Map.of("roles", roles);
        return createToken(claims, username);
    }

    private String createToken(Map<String, ?> claims, String username) {
        return Jwts.builder()
                .setClaims(claims)
                .setSubject(username)
                .setIssuedAt(new Date(System.currentTimeMillis()))
                .setExpiration(new Date(System.currentTimeMillis() + 1000*60*30))
                .signWith(signingKey(), SignatureAlgorithm.HS256)
                .compact();
    }

    private Key signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }
}
