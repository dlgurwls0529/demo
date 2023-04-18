package com.dong.demo.v1.exception;

public class NoSuchFileException extends RuntimeException {
    public NoSuchFileException() {
        super("such file does not exist.");
    }
}
