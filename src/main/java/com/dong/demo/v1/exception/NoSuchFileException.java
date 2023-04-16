package com.dong.demo.v1.exception;

public class NoSuchFileException extends RuntimeException {
    public NoSuchFileException() {
        super("file does not exist. you can not modify that file.");
    }
}
