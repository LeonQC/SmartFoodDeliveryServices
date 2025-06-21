package com.chris.exception;

/**
 * Google OAuth token is invalid
 */
public class InvalidGoogleOAuthException extends CustomizedBaseException {
    public InvalidGoogleOAuthException(String message) {
        super(message);
    }
}
