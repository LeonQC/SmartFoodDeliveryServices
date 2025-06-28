package com.chris.exception;

/**
 * User not found exception
 */
public class UserNotFoundException extends CustomizedBaseException {
  public UserNotFoundException(String message) {
    super(message);
  }
}
