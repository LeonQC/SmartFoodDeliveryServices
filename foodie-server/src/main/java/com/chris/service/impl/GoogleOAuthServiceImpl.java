package com.chris.service.impl;

import com.chris.dto.OAuth2UserInfo;
import com.chris.service.GoogleOAuthService;
import com.chris.utils.GoogleOAuthUtil;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;


@Service
public class GoogleOAuthServiceImpl implements GoogleOAuthService {
    @Autowired
    private GoogleOAuthUtil googleOAuthUtil;

    @Override
    public OAuth2UserInfo validateToken(String idTokenString) {
        // 1. 工具包只返回原生 payload
        GoogleIdToken.Payload payload = googleOAuthUtil.verifyAndGetPayload(idTokenString);

        // 2. 在这里才把 payload 转成业务 DTO
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

