package com.chris.exception;

public class ExistedCartItemException extends CustomizedBaseException {
    public ExistedCartItemException(String message) {
        super(message);
    }
}
