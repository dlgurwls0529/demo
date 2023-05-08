package com.dong.demo.v1.exception;

public class DataAccessException extends RuntimeException {
    public DataAccessException() {
        super("total sql data access exception");
    }

    public DataAccessException(Throwable cause) {
        super(cause);
    }
}
