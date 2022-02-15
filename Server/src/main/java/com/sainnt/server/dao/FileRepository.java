package com.sainnt.server.dao;

import com.sainnt.server.entity.File;

import java.util.Optional;

public interface FileRepository {
    Optional<File> getFile(long id) throws DaoException;

    void saveFile(File file) throws DaoException;

    void deleteFile(File file) throws DaoException;

    void updateFile(File file) throws DaoException;
}
