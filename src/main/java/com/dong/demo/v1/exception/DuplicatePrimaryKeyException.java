package com.dong.demo.v1.exception;

public class DuplicatePrimaryKeyException extends RuntimeException {

    public DuplicatePrimaryKeyException() {
        super("entity integrity violation : primary key duplicate.");
    }
}
