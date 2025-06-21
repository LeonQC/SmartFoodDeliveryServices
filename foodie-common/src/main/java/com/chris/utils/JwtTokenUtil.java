package com.chris.utils;

import com.chris.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.annotation.PostConstruct;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

import static com.chris.constant.MessageConstant.*;

@Component
public class JwtTokenUtil {

    @Value("${jwt.secret-base64}")
    private String secretKey;

    @Value("${jwt.access-expiration-seconds}")
    private long accessExpirationSeconds;

    @Value("${jwt.refresh-expiration-seconds}")
    private long refreshExpirationSeconds;

    private static SecretKey accessKey;
    private static SecretKey refreshKey;

    @PostConstruct
    public void init() {
        // Base64 解码成 byte[]
        byte[] keyBytes = Decoders.BASE64.decode(secretKey);

        // 创建 HMAC 密钥
        accessKey = Keys.hmacShaKeyFor(keyBytes);
        refreshKey = Keys.hmacShaKeyFor(keyBytes);
    }

    public String generateAccessToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + accessExpirationSeconds * 1000);

        return Jwts.builder()
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(accessKey, Jwts.SIG.HS256)
                .compact();
    }

    public String generateRefreshToken(Map<String, Object> claims) {
        Date now = new Date();
        Date expiryDate = new Date(now.getTime() + refreshExpirationSeconds * 1000);

        return Jwts.builder()
                .claim("refresh", true)
                .claims(claims)
                .issuedAt(now)
                .expiration(expiryDate)
                .signWith(refreshKey, Jwts.SIG.HS512)
                .compact();
    }

    public Claims parseAccessToken(String accessToken) {

        try {
            return Jwts.parser()
                    .verifyWith(accessKey)
                    .build()
                    .parseSignedClaims(accessToken)
                    .getPayload();
        } catch (JwtException e) {
            throw new InvalidTokenException(ACCESS_TOKEN_EXPIRED);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(ACCESS_TOKEN_INVALID);
        }
    }

    public Claims parseRefreshToken(String refreshToken) {
        try {
            return Jwts.parser()
                    .verifyWith(refreshKey)
                    .build()
                    .parseSignedClaims(refreshToken)
                    .getPayload();
        } catch (JwtException e) {
            throw new InvalidTokenException(REFRESH_TOKEN_EXPIRED);
        } catch (IllegalArgumentException e) {
            throw new InvalidTokenException(REFRESH_TOKEN_INVALID);
        }
    }
}
