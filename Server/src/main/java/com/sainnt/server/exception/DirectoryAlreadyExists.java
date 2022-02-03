package com.sainnt.server.exception;

public class DirectoryAlreadyExists extends ClientAvailableException {
    public DirectoryAlreadyExists(String path) {
        super(path);
    }

}
