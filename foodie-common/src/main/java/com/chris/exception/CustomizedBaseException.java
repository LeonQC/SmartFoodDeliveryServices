package com.chris.exception;

public class CustomizedBaseException extends RuntimeException {

  public CustomizedBaseException() {}

  public CustomizedBaseException(String message) {
    super(message);
  }
}
