package com.sainnt.server.exception;

public abstract class ClientAvailableException extends RuntimeException {
    public ClientAvailableException() {
    }

    public ClientAvailableException(String message) {
        super(message);
    }
}
