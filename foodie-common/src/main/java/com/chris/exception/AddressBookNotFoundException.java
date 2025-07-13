package com.chris.exception;

public class AddressBookNotFoundException extends CustomizedBaseException {
    public AddressBookNotFoundException(String message) {
        super(message);
    }
}
