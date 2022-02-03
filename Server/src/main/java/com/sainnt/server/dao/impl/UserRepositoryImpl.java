package com.sainnt.server.dao.impl;

import com.sainnt.server.dao.DaoException;
import com.sainnt.server.dao.UserRepository;
import com.sainnt.server.entity.Directory;
import com.sainnt.server.entity.User;
import com.sainnt.server.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.Optional;
import java.util.Set;
@Repository
public class UserRepositoryImpl implements UserRepository {
    @Override
    public void registerUser(User user) throws DaoException {
        try{
            Session session = HibernateUtil.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            session.save(user);
            Directory rootDir = session.get(Directory.class,1);
            Directory userDir = new Directory();
            userDir.setName(user.getUsername());
            userDir.setOwner(Set.of(user));
            userDir.setParent(rootDir);
            rootDir.getSubDirs().add(userDir);
            session.save(userDir);
            session.update(rootDir);
            transaction.commit();
        }catch (Exception exception)
        {
            throw new DaoException(exception);
        }
    }

    @Override
    public Optional<User> getUserById(long id) throws DaoException {
        try {
            Session session = HibernateUtil.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            Optional<User> user = Optional.ofNullable( session.get(User.class, id));
            transaction.commit();
            return user;
        }catch (Exception exception)
        {
            throw new DaoException(exception);
        }
    }

    @Override
    public Optional<User> getUserByUsername(String username) throws DaoException {
        try{Session session = HibernateUtil.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            Query<User> query = session.createQuery(" from User where username = :usernameParam ", User.class);
            query.setParameter("usernameParam",username);
            Optional<User> result = Optional.ofNullable(query.uniqueResult());
            transaction.commit();
            return result;
        }catch (Exception exception){
            throw new DaoException(exception);
        }
    }

    @Override
    public boolean usernameExists(String username) throws DaoException {
        try{
            Session session = HibernateUtil.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            Query<User> query = session.createQuery(" from User where username = :usernameParam ", User.class);
            query.setParameter("usernameParam",username);
            boolean found = query.uniqueResult() != null;
            transaction.commit();
            return found;
        }
        catch (Exception exception)
        {
            throw new DaoException(exception);
        }
    }

    @Override
    public boolean emailExists(String email) throws DaoException {
        try{
            Session session = HibernateUtil.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            Query<User> query = session.createQuery(" from User where email = :emailParam ", User.class);
            query.setParameter("emailParam",email);
            boolean found = query.uniqueResult() != null;
            transaction.commit();
            return found;
        }
        catch (Exception exception)
        {
            throw new DaoException(exception);
        }
    }
}
