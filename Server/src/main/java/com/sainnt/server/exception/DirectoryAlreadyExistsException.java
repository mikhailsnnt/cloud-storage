package com.sainnt.server.exception;

public class DirectoryAlreadyExistsException extends ClientAvailableException {
    public DirectoryAlreadyExistsException(String path) {
        super(path);
    }

}
