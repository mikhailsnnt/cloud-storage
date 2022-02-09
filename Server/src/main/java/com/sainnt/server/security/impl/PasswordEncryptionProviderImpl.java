package com.sainnt.server.security.impl;

import com.sainnt.server.security.PasswordEncryptionProvider;
import org.springframework.stereotype.Component;

import java.io.FileInputStream;
import java.io.IOException;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Objects;

@Component
public class PasswordEncryptionProviderImpl implements PasswordEncryptionProvider {
    private final byte[] salts;
    private final MessageDigest messageDigest;

    public PasswordEncryptionProviderImpl() {
        try {
            messageDigest = MessageDigest.getInstance("SHA-256");
            salts = new FileInputStream(Objects.requireNonNull(getClass().getResource("/slt")).getFile()).readAllBytes();
        } catch (NoSuchAlgorithmException | IOException e) {
            e.printStackTrace();
            throw new RuntimeException(e);
        }
    }

    @Override
    public byte[] getEncryptedPassword(byte[] password) {
        messageDigest.update(salts);
        return messageDigest.digest(password);
    }
}
