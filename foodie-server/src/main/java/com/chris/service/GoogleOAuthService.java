package com.chris.service;

import com.chris.dto.OAuth2UserInfo;

public interface GoogleOAuthService {
    /**
     * Verifies the Google ID Token received from the frontend; if it is valid and not expired, returns the current user's information.
     * @param idTokenString The ID Token (JWT string) passed from the frontend.
     * @return GoogleUserInfo containing fields such as email, name, picture, etc.; returns null or throws an exception if verification fails.
     */
    OAuth2UserInfo validateToken(String idTokenString);
}