package com.dong.demo.v1.exception;

public class NoSuchSubscribeDemandException extends RuntimeException {
    public NoSuchSubscribeDemandException() {
        super("there is no subscribe demand to allow.");
    }
}
