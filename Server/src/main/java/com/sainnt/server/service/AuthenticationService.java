package com.sainnt.server.service;

import com.sainnt.server.entity.User;
import io.netty.buffer.ByteBuf;

public interface AuthenticationService {
    User authenticate(String login, byte[] password);
    void userDisconnected(Long userId);
}
