package com.chris.service.impl;

import com.chris.dto.OAuth2UserInfo;
import com.chris.exception.GoogleOAuthVerifyException;
import com.chris.exception.InvalidGoogleOAuthException;
import com.chris.service.GoogleOAuthService;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.chris.constant.MessageConstant.OAUTH_TOKEN_INVALID;
import static com.chris.constant.MessageConstant.TOKEN_VERIFY_FAILED;

@Service
public class GoogleOAuthServiceImpl implements GoogleOAuthService {
    @Autowired
    private GoogleIdTokenVerifier verifier;

    @Override
    public OAuth2UserInfo validateToken(String idTokenString) {
        // 2. 用之前构造好的 verifier 去验签和解析 JWT
        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException e) {
            // 3. 在验签过程中，如果出现任何网络或解析异常，就抛出自定义的 Token 认证失败异常
            throw new GoogleOAuthVerifyException(TOKEN_VERIFY_FAILED);
        }
        if (idToken == null) {
            // 4. 如果返回的 idToken 为 null，说明这个 JWT 根本不是发给你应用（aud 不对）或已过期／格式不对, 抛出自定义的无效 Token 异常
            throw new InvalidGoogleOAuthException(OAUTH_TOKEN_INVALID);
        }
        return toUserInfo(idToken.getPayload());
    }

    private OAuth2UserInfo toUserInfo(GoogleIdToken.Payload payload) {
        OAuth2UserInfo u = new OAuth2UserInfo();
        u.setSub(payload.getSubject());
        u.setEmail(payload.getEmail());
        u.setEmailVerified(Boolean.TRUE.equals(payload.getEmailVerified()));
        u.setName((String) payload.get("name"));
        u.setPicture((String) payload.get("picture"));
        u.setLocale((String) payload.get("locale"));
        return u;
    }
}

