package com.luv2code.apigateway.util;

import io.jsonwebtoken.*;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.stereotype.Service;

import java.security.Key;

@Service
public class JWTUtil {
    private static final String SECRET = "6BA5E962F9918CA87EE4684B29983AFEC39CD9DB80A4BB87C96B5E2BC5A65957";

    public static void validateToken(String token) {
        Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build().parseClaimsJws(token);

    }

    public static String extractUsername(String token) {
        return Jwts.parserBuilder()
                .setSigningKey(signingKey())
                .build().parseClaimsJws(token).getBody().getSubject();

    }

    private static Key signingKey() {
        return Keys.hmacShaKeyFor(Decoders.BASE64.decode(SECRET));
    }
}