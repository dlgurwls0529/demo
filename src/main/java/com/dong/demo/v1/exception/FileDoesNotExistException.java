package com.dong.demo.v1.exception;

public class FileDoesNotExistException extends RuntimeException {
    public FileDoesNotExistException() {
        super("file does not exist. you can not modify that file.");
    }
}
