package com.sainnt.server.service;

import com.sainnt.server.dto.LoginResult;
import com.sainnt.server.dto.RegistrationResult;

public interface AuthenticationService {
    LoginResult authenticate(String username, byte[] password);

    void userDisconnected(Long userId);

    RegistrationResult registerUser(String username, String email, byte[] password);
}
