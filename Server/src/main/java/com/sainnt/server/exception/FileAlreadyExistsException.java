package com.sainnt.server.exception;

import lombok.Getter;

@Getter
public class FileAlreadyExistsException extends ClientAvailableException {
    public FileAlreadyExistsException(String path) {
        super(path);
    }

}
