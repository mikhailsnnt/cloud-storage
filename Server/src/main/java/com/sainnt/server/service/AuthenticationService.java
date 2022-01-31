package com.sainnt.server.service;

import com.sainnt.server.dto.RegistrationResult;
import com.sainnt.server.entity.User;

public interface AuthenticationService {
    User authenticate(String username, byte[] password);
    void userDisconnected(Long userId);
    RegistrationResult registerUser(String username, String email, byte[] password);
}
