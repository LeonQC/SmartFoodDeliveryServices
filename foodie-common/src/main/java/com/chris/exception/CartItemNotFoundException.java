package com.chris.exception;

public class CartItemNotFoundException extends CustomizedBaseException {
    public CartItemNotFoundException(String message) {
        super(message);
    }
}
