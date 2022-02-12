package com.sainnt.server.dao.impl;

import com.sainnt.server.dao.DaoException;
import com.sainnt.server.dao.DirectoryRepository;
import com.sainnt.server.entity.Directory;
import com.sainnt.server.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.hibernate.query.Query;
import org.springframework.stereotype.Repository;

import java.util.List;

@Repository
public class DirectoryRepositoryImpl implements DirectoryRepository {
    @Override
    public Directory loadRootDirectory() throws DaoException {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getCurrentSession();
            transaction = session.beginTransaction();
            Directory rootDir = session.get(Directory.class, 1);
            transaction.commit();
            return rootDir;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            throw new DaoException(e);
        }
    }

    @Override
    public List<Directory> getDirectories() throws DaoException {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getCurrentSession();
            transaction = session.beginTransaction();
            Query<Directory> query = session.createQuery("from Directory order by id", Directory.class);
            List<Directory> resultList = query.getResultList();
            transaction.commit();
            return resultList;
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            throw new DaoException(e);
        }
    }

    @Override
    public void saveDirectory(Directory dir) throws DaoException {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getCurrentSession();
            transaction = session.beginTransaction();
            session.save(dir);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            throw new DaoException(e);
        }
    }

    @Override
    public void deleteDirectory(Directory dir) throws DaoException {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getCurrentSession();
            transaction = session.beginTransaction();
            Directory parent = dir.getParent();
            parent.getSubDirs().remove(dir);
            session.update(parent);
            session.delete(dir);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            throw new DaoException(e);
        }
    }

    @Override
    public void updateDirectory(Directory dir) throws DaoException {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getCurrentSession();
            transaction = session.beginTransaction();
            session.update(dir);
            transaction.commit();
        } catch (Exception e) {
            if (transaction != null)
                transaction.rollback();
            throw new DaoException(e);
        }
    }
}
