package com.sainnt.server.exception;

import lombok.Getter;

@Getter
public class AccessDeniedException extends ClientAvailableException {

    public AccessDeniedException(String username, String resource) {
        super(String.format("%s denied access to %s", username, resource));
    }

}
