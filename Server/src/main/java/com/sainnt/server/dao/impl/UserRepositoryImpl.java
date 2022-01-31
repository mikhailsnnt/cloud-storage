package com.sainnt.server.dao.impl;

import com.sainnt.server.dao.UserRepository;
import com.sainnt.server.entity.User;
import com.sainnt.server.util.HibernateUtil;
import org.hibernate.Hibernate;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;


public class UserRepositoryImpl implements UserRepository {



    @Override
    public User saveUser(User user) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        try{
            session.save(user);
        }
        catch (Exception exception)
        {
            exception.printStackTrace();
            transaction.rollback();
            return null;
        }
        transaction.commit();
        return user;
    }

    @Override
    public User getUserById(long id) {
        Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        User user = session.get(User.class, id);
        transaction.commit();
        return user;
    }

    @Override
    public User getUserByUsername(String username) {
        Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        User user = loadUserByUsername(session,username);
        transaction.commit();
        return user;
    }

    @Override
    public User getUserAndFetchCredentials(String username) {
        Session session =  HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        User user = loadUserByUsername(session,username);
        Hibernate.initialize(user.getCredentials());
        transaction.commit();
        return user;
    }

    @Override
    public boolean usernameExists(String username) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Query<User> query = session.createQuery("from User where username =: usernameParam", User.class);
        query.setMaxResults(1);
        query.setParameter("usernameParam",username);
        boolean objectFound = query.uniqueResult() != null;
        transaction.commit();
        return objectFound;
    }


    @Override
    public boolean emailExists(String email) {
        Session session = HibernateUtil.getSessionFactory().getCurrentSession();
        Transaction transaction = session.beginTransaction();
        Query<User> query = session.createQuery("from User where email=: emailParam", User.class);
        query.setMaxResults(1);
        query.setParameter("emailParam",email);
        boolean objectFound = query.uniqueResult() != null;
        transaction.commit();
        return objectFound;
    }
    private User loadUserByUsername(Session session, String username){
        Query<User> query = session.createQuery("from User where username =: usernameParam",User.class);
        query.setMaxResults(1);
        query.setParameter("usernameParam",username);
        return query.uniqueResult();
    }

}
