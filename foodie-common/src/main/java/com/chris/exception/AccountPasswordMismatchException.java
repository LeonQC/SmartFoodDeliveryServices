package com.chris.exception;

/**
 * Username or password is incorrect
 */
public class AccountPasswordMismatchException extends CustomizedBaseException {
    public AccountPasswordMismatchException(String message) {
        super(message);
    }
}
