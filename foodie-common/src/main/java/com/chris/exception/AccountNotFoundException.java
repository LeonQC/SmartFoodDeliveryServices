package com.chris.exception;

/**
 * Account not found exception
 */
public class AccountNotFoundException extends CustomizedBaseException {
    public AccountNotFoundException(String message) {
        super(message);
    }
}
