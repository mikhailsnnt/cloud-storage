package com.sainnt.server.dao;

import com.sainnt.server.entity.Directory;

public interface DirectoryRepository {

    Directory loadRootDirectory() throws DaoException;

    void saveDirectory(Directory dir) throws DaoException;

    void deleteDirectory(Directory dir) throws DaoException;

    void updateDirectory(Directory dir) throws DaoException;
}
