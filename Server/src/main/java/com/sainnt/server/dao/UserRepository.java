package com.sainnt.server.dao;

import com.sainnt.server.entity.User;

import java.util.Optional;

public interface UserRepository {
    void registerUser(User user) throws DaoException;

    Optional<User> getUserById(long id) throws DaoException;

    Optional<User> getUserByUsername(String username) throws DaoException;

    boolean usernameExists(String username) throws DaoException;

    boolean emailExists(String email) throws DaoException;
}
