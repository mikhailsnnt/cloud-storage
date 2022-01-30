package com.sainnt.server.dao;

import com.sainnt.server.entity.User;
import com.sainnt.server.entity.UserCredentials;

public interface UserRepository {
    User getUserById(long id);
    User getUserByCredentials(String username, byte[] password);
}
