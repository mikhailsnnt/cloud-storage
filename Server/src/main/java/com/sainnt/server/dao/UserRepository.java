package com.sainnt.server.dao;

import com.sainnt.server.dto.RegistrationResult;
import com.sainnt.server.entity.User;
import com.sainnt.server.entity.UserCredentials;

public interface UserRepository {
    User saveUser(User user);
    User getUserById(long id);
    User getUserByUsername(String username);
    User getUserAndFetchCredentials(String username);
    boolean usernameExists(String username);
    boolean emailExists(String email);
}
