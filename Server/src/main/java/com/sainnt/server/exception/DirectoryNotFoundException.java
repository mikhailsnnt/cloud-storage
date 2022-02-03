package com.sainnt.server.exception;

public class DirectoryNotFoundException extends ClientAvailableException{
    public DirectoryNotFoundException(String path) {
        super(path);
    }

}
