package com.sainnt.server.service.impl;

import com.sainnt.server.dao.DaoException;
import com.sainnt.server.dao.UserRepository;
import com.sainnt.server.dto.LoginResult;
import com.sainnt.server.dto.RegistrationResult;
import com.sainnt.server.entity.User;
import com.sainnt.server.entity.UserCredentials;
import com.sainnt.server.exception.InternalServerError;
import com.sainnt.server.security.PasswordEncryptionProvider;
import com.sainnt.server.service.AuthenticationService;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.validator.routines.EmailValidator;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;
@Service
@Slf4j
public class AuthenticationServiceImpl implements AuthenticationService {
    private final UserRepository repository;
    private final PasswordEncryptionProvider encryptor;
    private final Set<Long> connectedUsers = new HashSet<>();
    private final EmailValidator emailValidator = EmailValidator.getInstance();



    @Autowired
    public AuthenticationServiceImpl(UserRepository repository, PasswordEncryptionProvider encryptor) {
        this.repository = repository;
        this.encryptor = encryptor;
    }

    @Override
    public LoginResult authenticate(String username, byte[] password) {
        Optional<User> optionalUser;
        try {
            optionalUser = repository.getUserByUsername(username);
        } catch (DaoException e) {
            log.error("Error fetching user",e);
            throw new InternalServerError();
        }
        if(optionalUser.isEmpty())
            return new LoginResult(null, LoginResult.Result.bad_credentials);
        User user = optionalUser.get();
        if(connectedUsers.contains(user.getId()))
            return new LoginResult(null, LoginResult.Result.user_already_logged_in);
        if(!Arrays.equals( encryptor.getEncryptedPassword(password), user.getCredentials().getEncPassword() ))
            return new LoginResult(null, LoginResult.Result.bad_credentials);
        connectedUsers.add(user.getId());
        return  new LoginResult( user, LoginResult.Result.success);
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
        try {
            if (repository.emailExists(email))
                return RegistrationResult.email_exists;
            if (repository.usernameExists(username))
                return RegistrationResult.username_occupied;
        }
        catch (DaoException e){
            log.error("Error checking username|email occupation",e);
            throw new InternalServerError();
        }
        User user = new User();
        user.setUsername(username);
        user.setEmail(email);
        UserCredentials credentials = new UserCredentials();
        credentials.setEncPassword(encryptor.getEncryptedPassword(password));
        user.setCredentials(credentials);
        try{
            repository.registerUser(user);
        }
        catch (DaoException e){
            log.error("Error saving user entity during registration: ",e);
            throw new InternalServerError();
        }
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
