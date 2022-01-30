package com.sainnt.server.service.impl;

import com.sainnt.server.dao.UserRepository;
import com.sainnt.server.entity.User;
import com.sainnt.server.exception.UserAlreadyLoggedInException;
import com.sainnt.server.security.PasswordEncryptionProvider;
import com.sainnt.server.service.AuthenticationService;

import java.util.HashSet;
import java.util.Set;

public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncryptionProvider encryptor;
    private final Set<Long> connectedUsers = new HashSet<>();


    public AuthenticationServiceImpl(UserRepository repository, PasswordEncryptionProvider encryptor) {
        this.repository = repository;
        this.encryptor = encryptor;
    }

    @Override
    public User authenticate(String login, byte[] password) throws UserAlreadyLoggedInException{
        User user = repository.getUserByCredentials(login, encryptor.getEncryptedPassword(password));
        if(user == null)
            return null;
        if(connectedUsers.contains(user.getId()))
            throw  new UserAlreadyLoggedInException();
        connectedUsers.add(user.getId());
        return user;
    }

    @Override
    public void userDisconnected(Long userId) {
        connectedUsers.remove(userId);
    }

}
