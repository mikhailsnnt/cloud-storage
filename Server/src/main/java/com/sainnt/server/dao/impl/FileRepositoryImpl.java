package com.sainnt.server.dao.impl;

import com.sainnt.server.dao.DaoException;
import com.sainnt.server.dao.FileRepository;
import com.sainnt.server.entity.Directory;
import com.sainnt.server.entity.File;
import com.sainnt.server.util.HibernateUtil;
import org.hibernate.Session;
import org.hibernate.Transaction;
import org.springframework.stereotype.Repository;

@Repository
public class FileRepositoryImpl implements FileRepository {
    @Override
    public void saveFile(File file) throws DaoException {
        try {
            Session session = HibernateUtil.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            session.save(file);
            transaction.commit();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }

    @Override
    public void deleteFile(File file) throws DaoException {
        Transaction transaction = null;
        try {
            Session session = HibernateUtil.getCurrentSession();
            transaction = session.beginTransaction();
            Directory parentDirectory = file.getParentDirectory();
            parentDirectory.getFiles().remove(file);
            session.delete(file);
            session.update(parentDirectory);
            transaction.commit();
        } catch (Exception e) {
            if(transaction != null)
                transaction.rollback();
            throw new DaoException(e);
        }
    }


    @Override
    public void updateFile(File file) throws DaoException {
        try {
            Session session = HibernateUtil.getCurrentSession();
            Transaction transaction = session.beginTransaction();
            session.update(file);
            transaction.commit();
        } catch (Exception e) {
            throw new DaoException(e);
        }
    }
}
