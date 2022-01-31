package com.sainnt.server.service.impl;

import com.sainnt.server.dao.UserRepository;
import com.sainnt.server.dto.RegistrationResult;
import com.sainnt.server.entity.User;
import com.sainnt.server.entity.UserCredentials;
import com.sainnt.server.exception.UserAlreadyLoggedInException;
import com.sainnt.server.security.PasswordEncryptionProvider;
import com.sainnt.server.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Set;
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncryptionProvider encryptor;
    private final Set<Long> connectedUsers = new HashSet<>();
    private final EmailValidator emailValidator = EmailValidator.getInstance();


    public AuthenticationServiceImpl(UserRepository repository, PasswordEncryptionProvider encryptor) {
        this.repository = repository;
        this.encryptor = encryptor;
    }

    @Override
    public User authenticate(String username, byte[] password) throws UserAlreadyLoggedInException{
        User user = repository.getUserAndFetchCredentials(username);
        if(user==null)
            return null;
        if(connectedUsers.contains(user.getId()))
            throw  new UserAlreadyLoggedInException();
        if(!Arrays.equals( encryptor.getEncryptedPassword(password), user.getCredentials().getEncPassword() ))
            return null;
        connectedUsers.add(user.getId());
        return user;
    }

    @Override
    public void userDisconnected(Long userId) {
        connectedUsers.remove(userId);
    }

    @Override
    public RegistrationResult registerUser(String username, String email, byte[] password) {
        if(!validateEmail(email))
            return RegistrationResult.email_invalid;
        if(!validatePassword(password))
            return RegistrationResult.password_invalid;
        if(repository.emailExists(email))
            return RegistrationResult.email_exists;
        if(repository.usernameExists(username))
            return RegistrationResult.username_occupied;
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        UserCredentials credentials = new UserCredentials();
        credentials.setEncPassword(encryptor.getEncryptedPassword(password));
        user.setCredentials(credentials);

        if(repository.saveUser(user) == null)
            return RegistrationResult.registration_failed;
        log.info("User registered: {}",user);
        return RegistrationResult.success;
    }

    private boolean validateEmail(String email){
        return emailValidator.isValid(email);
    }
    private boolean validatePassword(byte[] password){
        return password.length > 5;
    }
}
