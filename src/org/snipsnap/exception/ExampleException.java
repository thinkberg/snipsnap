package com.neotis.exception;

/**
 * Example exception.
 *
 * @author Stephan J. Schmidt
 * @version $Id$
 **/
public class ExampleException extends ChainedException {
    public ExampleException(String message, Throwable cause) {
        super(message, cause);
    }

    public ExampleException(String message) {
        super(message);
    }
}
