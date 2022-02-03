package com.sainnt.server.dao;

import com.sainnt.server.entity.File;

public interface FileRepository {
    void saveFile(File file) throws DaoException;
    void deleteFile(File file) throws DaoException;
    void updateFile(File file) throws DaoException;
}
