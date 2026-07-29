package com.koboolean.springbatchlecture.config.exception;

public class RetryableException extends RuntimeException {

    public RetryableException() {
        super();
    }

    public RetryableException(String message) {
        super(message);
    }
}
