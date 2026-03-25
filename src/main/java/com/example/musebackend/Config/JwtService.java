package com.example.musebackend.Config;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import java.io.InputStream;
import java.security.PrivateKey;
import java.security.PublicKey;
import java.util.Date;
import java.util.Map;

@Service
public class JwtService {

    private static final String TOKEN_TYPE = "token_type";
    private final PrivateKey privateKey;
    private final PublicKey publicKey;

    private final long accessTokenExpiration;
    private final long refreshTokenExpiration;



    public JwtService(
            @Value("${JWT_PRIVATE_KEY_CONTENT:}") String privateKeyEnv,
            @Value("${JWT_PUBLIC_KEY_CONTENT:}") String publicKeyEnv,
            @Value("${jwt-access-token-expiration}") long accessTokenExpiration,
            @Value("${jwt-refresh-token-expiration}") long refreshTokenExpiration
    ) throws Exception {
        this.accessTokenExpiration = accessTokenExpiration;
        this.refreshTokenExpiration = refreshTokenExpiration;

        this.privateKey = resolveKey(true, "jwt_locals/private.pem", privateKeyEnv);
        this.publicKey = resolveKey(false, "jwt_locals/public.pem", publicKeyEnv);
    }

    private <T> T resolveKey(boolean isPrivate, String path, String env) throws Exception {
        // 1. Try Environment Variable first (if provided)
        if (env != null && !env.isBlank()) {
            return isPrivate ? (T) KeyUtils.parsePrivateKey(env) : (T) KeyUtils.parsePublicKey(env);
        }

        // 2. Fallback to Classpath Resource
        try (InputStream is = getClass().getClassLoader().getResourceAsStream(path)) {
            if (is == null) throw new Exception("Key not found in resources");
            String content = new String(is.readAllBytes());
            return isPrivate ? (T) KeyUtils.parsePrivateKey(content) : (T) KeyUtils.parsePublicKey(content);
        }
    }

    public String generateAccessToken(final String username){
        final Map<String , Object> claims = Map.of(TOKEN_TYPE, "ACCESS_TOKEN");
        return buildToken(username,claims,this.accessTokenExpiration);
    }
    public String generateRefreshToken(final String username){
        final Map<String , Object> claims = Map.of(TOKEN_TYPE, "REFRESH_TOKEN");
        return buildToken(username,claims,this.refreshTokenExpiration);

    }
    // Add this to your JwtService.java
    public String generateToken(com.example.musebackend.Models.User user) {
        final Map<String, Object> claims = Map.of(
                TOKEN_TYPE, "ACCESS_TOKEN",
                "role", user.getRole().name()
        );
        return buildToken(user.getEmail(), claims, this.accessTokenExpiration);
    }

    private String buildToken(String username, Map<String, Object> claims, long expiration) {
        return Jwts.builder()
                .claims(claims)
                .subject(username)
                .issuedAt(new Date(System.currentTimeMillis()))
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(this.privateKey)  //WHEN SIGNING IN ALWAYS USE PRIVATE KEY AND USE PUBLIC KEY FOR DECODING
                .compact();
    }

    public boolean isTokenValid(final String token, final String expectedUsername){
        final String username = extractUsername(token);
        return username.equals(expectedUsername) && !isTokenExpired(token);
    }

    private boolean isTokenExpired(String token) {
        return extactClaims(token).getExpiration()
                .before(new Date());
    }

    public String extractUsername(final String token) {
        return extactClaims(token).getSubject();
    }

    private Claims extactClaims(final String token) {
        try{
            return Jwts.parser()
                    .verifyWith(this.publicKey)
                    .build()
                    .parseSignedClaims(token)
                    .getPayload();
        } catch (final JwtException e) {
            throw new RuntimeException("Invalid JWT token",e);
        }
    }

    public String refreshAccessToken(final String refreshToken){
        final Claims claims = extactClaims(refreshToken);
        if (!"REFRESH_TOKEN".equals(claims.get(TOKEN_TYPE))){
            throw new RuntimeException("Refresh Token expired");
        }
        final String username = claims.getSubject();
        return generateAccessToken(username);
    }
}