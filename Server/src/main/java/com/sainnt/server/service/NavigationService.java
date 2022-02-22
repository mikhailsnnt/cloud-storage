package com.sainnt.server.service;

import com.sainnt.server.dto.DirectoryWithAccessInfo;
import com.sainnt.server.entity.Directory;
import com.sainnt.server.entity.File;
import com.sainnt.server.entity.User;

import java.util.Set;

public interface NavigationService {
    Directory accessDirectoryByPath(String path, User user);


    Directory accessDirectoryById(long id, User user);

    DirectoryWithAccessInfo findDirectoryWithAccessInfoByPath(String path, User user);

    DirectoryWithAccessInfo findDirectoryWithAccessInfoById(long id, User user);

    Directory createDirectory(long parentId, String path, User user);

    void deleteDirectory(long id, User user);

    void renameDirectory(long id, User user, String newName);

    Set<Directory> getSubDirectories(String path, User user);

    Set<File> getFiles(String path, User user);

    File accessFileByPath(String path, User user);

    File accessFileById(long id, User user);

    File createFile(long parentId, String path, User user);

    void deleteFile(long id, User user);

    void renameFile(long id, User user, String newName);

    void updateFileEntity(File file);
}
