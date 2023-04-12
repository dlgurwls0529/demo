package com.dong.demo.v1.exception;

public class NoMatchParentRowException extends RuntimeException {

    public NoMatchParentRowException() {
        super("referential integrity violation : there is no matching parent row.");
    }
}
