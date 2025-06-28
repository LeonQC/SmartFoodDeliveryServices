package com.chris.exception;

/**
 * Google OAuth token verification failed
 */
public class GoogleOAuthVerifyException extends CustomizedBaseException {
    public GoogleOAuthVerifyException(String message) {
        super(message);
    }
}
