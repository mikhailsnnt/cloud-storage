package com.sainnt.server.security;

public interface PasswordEncryptionProvider {
    byte[] getEncryptedPassword(byte[] password);
}
