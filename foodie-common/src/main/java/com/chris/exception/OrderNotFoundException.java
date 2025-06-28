package com.chris.exception;

public class OrderNotFoundException extends CustomizedBaseException {
    public OrderNotFoundException(String message) {
        super(message);
    }
}
