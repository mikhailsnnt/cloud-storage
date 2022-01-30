package com.sainnt.server.security.impl;

import com.sainnt.server.security.PasswordEncryptionProvider;
import io.netty.buffer.ByteBuf;

import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;

public class PasswordEncryptionProviderImpl implements PasswordEncryptionProvider {
    private MessageDigest messageDigest;
    {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
        } catch (NoSuchAlgorithmException e) {
            e.printStackTrace();
        }
    }

    @Override
    public byte[] getEncryptedPassword(byte[] password) {
        return  messageDigest.digest(password);
    }
}
