package com.intelsoft.exceptions;

public class NapServiceException extends RuntimeException {
    public NapServiceException(String message) {
        super(message);
    }

    public NapServiceException(String message, Throwable cause) {
        super(message, cause);
    }
}