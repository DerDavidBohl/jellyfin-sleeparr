package org.davidbohl.jellyfin_sleeparr.security;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import javax.crypto.SecretKey;
import java.time.Duration;
import java.util.Date;

@Service
public class JwtService {


    @Value("${sleeparr.jwtSecret}")
    private String jwtSecret;

    public String generateToken(String subject, Date expiration) {
        return Jwts.builder()
                .subject(subject)
                .issuedAt(new Date())
                .expiration(expiration)
                .signWith(getSigningKey())
                .compact();
    }

    public boolean validate(String token) {
        return !isTokenExpired(token);

    }

    public String getUserId(String token) {
        return extractAllClaims(token).getSubject();
    }

    private Claims extractAllClaims(String token) {
        return Jwts.parser()
                .verifyWith(getSigningKey())
                .decryptWith(getSigningKey())
                .build()
                .parseSignedClaims(token)
                .getPayload();

    }

    private Boolean isTokenExpired(String token) {
        return extractAllClaims(token).getExpiration().before(new Date());
    }

    private SecretKey getSigningKey() {
        byte[] keyBytes = Decoders.BASE64.decode(jwtSecret);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
