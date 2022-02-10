package com.sainnt.exception;

public class FileRenamingFailedException extends RuntimeException {
    public FileRenamingFailedException(String message) {
        super(message);
    }
}
