package com.sainnt.server.exception;

import lombok.Getter;

@Getter
public class InvalidFileNameException extends ClientAvailableException{

    public InvalidFileNameException(String filename) {
        super(filename);
    }

}
