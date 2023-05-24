package com.dong.demo.v1.exception;

public class VerifyFailedException extends RuntimeException {
    public VerifyFailedException() {
        super("private key verification is failed.");
    }
}
