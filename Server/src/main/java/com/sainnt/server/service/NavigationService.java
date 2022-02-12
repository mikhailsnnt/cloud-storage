package com.sainnt.server.service;

import com.sainnt.server.dto.DirectoryWithAccessInfo;
import com.sainnt.server.entity.Directory;
import com.sainnt.server.entity.File;
import com.sainnt.server.entity.User;

import java.util.Set;

public interface NavigationService {
    Directory findDirectoryByPath(String path, User user);

    DirectoryWithAccessInfo findDirectoryWithAccessInfoByPath(String path, User user);

    DirectoryWithAccessInfo findDirectoryWithAccessInfoByPathN(String path, User user);

    Directory createDirectory(String path, User user);

    void deleteDirectory(String path, User user);

    void renameDirectory(String path, User user, String newName);

    Set<Directory> getSubDirectories(String path, User user);

    Set<File> getFiles(String path, User user);

    File getFileByPath(String path, User user);

    File createFile(String path, User user);

    void deleteFile(String path, User user);

    void renameFile(String path, User user, String newName);

    void updateFileEntity(File file);

}
