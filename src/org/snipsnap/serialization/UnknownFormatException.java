package org.snipsnap.serialization;

/** This exception is thrown when no serializer is available for a requested output format */
public class UnknownFormatException extends Exception {

    /** Constructs an Exception without a message. */
    public UnknownFormatException() {
        super();
    }

    /**
     * Constructs an Exception with a detailed message.
     * @param message The message associated with the exception.
     */
    public UnknownFormatException(String message) {
        super(message);
    }
}
