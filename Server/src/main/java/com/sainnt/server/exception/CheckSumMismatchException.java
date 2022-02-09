package com.sainnt.server.exception;

public class CheckSumMismatchException extends ClientAvailableException {

    public CheckSumMismatchException(String path) {
        super(path);
    }


}
