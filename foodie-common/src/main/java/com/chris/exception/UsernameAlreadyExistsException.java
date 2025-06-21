package com.chris.exception;

public class UsernameAlreadyExistsException extends CustomizedBaseException {
    public UsernameAlreadyExistsException(String message) {
        super(message);
    }
}
