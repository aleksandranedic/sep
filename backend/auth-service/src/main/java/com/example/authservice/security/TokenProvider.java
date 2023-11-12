package com.example.authservice.security;

import com.example.authservice.config.AppProperties;
import com.example.authservice.dto.response.BlacklistedTokenInfo;
import com.example.authservice.model.User;
import io.jsonwebtoken.*;
import io.jsonwebtoken.security.Keys;
import io.jsonwebtoken.security.SignatureException;
import org.springframework.security.core.Authentication;
import org.springframework.stereotype.Service;

import java.nio.charset.StandardCharsets;
import java.security.Key;
import java.sql.Date;
import java.time.Instant;
import java.util.*;

@Service
public class TokenProvider {

    private final AppProperties.Auth authProperties;

    private final Set<String> tokenBlacklist = new HashSet<>();

    private final Map<String, BlacklistedTokenInfo> tokenBlacklistInfo = new HashMap<>();

    public TokenProvider(AppProperties appProperties) {
        this.authProperties = appProperties.getAuth();
    }

    public String createAccessToken(Authentication authentication) {
        User user = (User) authentication.getPrincipal();

        Instant now = Instant.now();
        Instant expiresAt = now.plusSeconds(authProperties.getTokenExpirationSeconds());

        return Jwts.builder()
                .setSubject(user.getId().toString())
                .setIssuedAt(Date.from(now))
                .setExpiration(Date.from(expiresAt))
                .signWith(getKey())
                .compact();
    }

    public UUID getUserIdFromToken(String token) {
        JwtParser parser = Jwts.parserBuilder().setSigningKey(getKey()).build();
        Claims claims = parser.parseClaimsJws(token).getBody();

        return UUID.fromString(claims.getSubject());
    }

    public Claims readClaims(String token) throws Exception {
        try {
            JwtParser parser = Jwts.parserBuilder().setSigningKey(getKey()).build();
            return parser.parseClaimsJws(token).getBody();
        } catch (IllegalArgumentException e) {
            throw new Exception("Access token is not provided.");
        } catch (ExpiredJwtException e) {
            throw new Exception("Access token has expired.");
        } catch (UnsupportedJwtException e) {
            throw new Exception("Access token format is unsupported.");
        } catch (MalformedJwtException e) {
            throw new Exception("Access token is malformed.");
        } catch (SignatureException e) {
            throw new Exception("Access token signature is invalid.");
        }
    }

    public void addTokenToBlacklist(String token) {
        tokenBlacklist.add(token);

        try {
            Claims claims = readClaims(token);
            tokenBlacklistInfo.put(token, new BlacklistedTokenInfo(
                    claims.getSubject(),
                    claims.getIssuedAt().toString(),
                    claims.getExpiration().toString()
            ));
        } catch (Exception e) {
            tokenBlacklistInfo.put(token, new BlacklistedTokenInfo(
                    e.getMessage()
            ));
        }
    }

    public boolean isTokenBlacklisted(String token) {
        return token != null && tokenBlacklist.contains(token);
    }

    public List<BlacklistedTokenInfo> getTokenBlacklistInfo() {
        return tokenBlacklistInfo.values().stream().toList();
    }

    private Key getKey() {
        byte[] keyBytes = authProperties.getTokenSecret().getBytes(StandardCharsets.UTF_8);
        return Keys.hmacShaKeyFor(keyBytes);
    }

}
