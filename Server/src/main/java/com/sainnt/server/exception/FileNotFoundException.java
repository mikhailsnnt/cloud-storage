package com.sainnt.server.exception;

import lombok.Getter;

public class FileNotFoundException extends ClientAvailableException{

    public FileNotFoundException(String path) {
        super(path);
    }

}
