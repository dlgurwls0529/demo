package com.dong.demo.v1.exception;

public class VerifyInvalidInputException extends RuntimeException {

    private final String message;

    public VerifyInvalidInputException(Throwable cause) {
        super(cause);
        message = "key or sign ... format is invalid";
    }

    @Override
    public String getMessage() {
        return message;
    }
}
