package com.chris.utils;

import com.chris.exception.InvalidTokenException;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.JwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import lombok.Getter;

import javax.crypto.SecretKey;
import java.util.Date;
import java.util.Map;

import static com.chris.constant.MessageConstant.*;

@Getter
public class JwtTokenUtil {

    private final SecretKey accessKey;
    private final SecretKey refreshKey;
    private final long accessExpirationSeconds;
    private final long refreshExpirationSeconds;

    public JwtTokenUtil(String base64Secret,
                        long accessExpirationSeconds,
                        long refreshExpirationSeconds) {
        byte[] keyBytes = Decoders.BASE64.decode(base64Secret);
        this.accessKey = Keys.hmacShaKeyFor(keyBytes);
        this.refreshKey = Keys.hmacShaKeyFor(keyBytes);
        this.accessExpirationSeconds = accessExpirationSeconds;
        this.refreshExpirationSeconds = refreshExpirationSeconds;
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
