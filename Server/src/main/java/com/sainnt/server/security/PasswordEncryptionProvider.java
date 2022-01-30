package com.sainnt.server.security;

import io.netty.buffer.ByteBuf;

public interface PasswordEncryptionProvider {
    byte[] getEncryptedPassword(byte[] password);
}
