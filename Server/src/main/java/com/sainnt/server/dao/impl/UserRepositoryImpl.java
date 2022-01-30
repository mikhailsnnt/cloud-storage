package com.sainnt.server.dao.impl;

import com.sainnt.server.dao.UserRepository;
import com.sainnt.server.entity.User;
import com.sainnt.server.entity.UserCredentials;
import com.sainnt.server.util.HibernateUtil;
import org.hibernate.Session;

import java.util.Arrays;

public class UserRepositoryImpl implements UserRepository {
    private final Session session;

    public UserRepositoryImpl() {
        session = HibernateUtil.getSessionFactory().getCurrentSession();
    }

    @Override
    public User getUserById(long id) {
        session.beginTransaction();
        User user = session.get(User.class, id);
        session.getTransaction().commit();
        return user;
    }

    @Override
    public User getUserByCredentials(String username, byte[] password) {
        session.beginTransaction();
        UserCredentials credentials = session.get(UserCredentials.class,username);
        session.getTransaction().commit();
        if(credentials == null|| !Arrays.equals(credentials.getEncPassword(), password))
            return null;
        return credentials.getUser();

    }
}
