package com.neotis.exception;

public class ExampleException extends ChainedException {
    public ExampleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExampleException(String message) {
        super(message);
    }
}
