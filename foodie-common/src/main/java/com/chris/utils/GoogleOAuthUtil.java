package com.chris.utils;

import com.chris.exception.GoogleOAuthVerifyException;
import com.chris.exception.InvalidGoogleOAuthException;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdToken;
import com.google.api.client.googleapis.auth.oauth2.GoogleIdTokenVerifier;

import java.io.IOException;
import java.security.GeneralSecurityException;

import static com.chris.constant.MessageConstant.OAUTH_TOKEN_INVALID;
import static com.chris.constant.MessageConstant.TOKEN_VERIFY_FAILED;

public class GoogleOAuthUtil {
    private final GoogleIdTokenVerifier verifier;

    public GoogleOAuthUtil(GoogleIdTokenVerifier verifier) {
        this.verifier = verifier;
    }

    /**
     * 验证 Google ID Token 并返回原生 Payload
     * @throws InvalidGoogleOAuthException, GoogleOAuthVerifyException
     */
    public GoogleIdToken.Payload verifyAndGetPayload(String idTokenString) {
        GoogleIdToken idToken;
        try {
            idToken = verifier.verify(idTokenString);
        } catch (GeneralSecurityException | IOException e) {
            throw new GoogleOAuthVerifyException(TOKEN_VERIFY_FAILED);
        }
        if (idToken == null) {
            throw new InvalidGoogleOAuthException(OAUTH_TOKEN_INVALID);
        }
        return idToken.getPayload();
    }
}
