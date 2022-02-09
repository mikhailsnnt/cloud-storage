package com.sainnt.server.exception;

public class FileNotFoundException extends ClientAvailableException {

    public FileNotFoundException(String path) {
        super(path);
    }

}
