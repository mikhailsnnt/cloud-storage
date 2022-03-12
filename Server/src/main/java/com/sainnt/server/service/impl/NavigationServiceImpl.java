package com.sainnt.server.service.impl;

import com.sainnt.server.dao.DaoException;
import com.sainnt.server.dao.DirectoryRepository;
import com.sainnt.server.dao.FileRepository;
import com.sainnt.server.dto.DirectoryWithAccessInfo;
import com.sainnt.server.entity.Directory;
import com.sainnt.server.entity.File;
import com.sainnt.server.entity.User;
import com.sainnt.server.exception.*;
import com.sainnt.server.service.NavigationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Set;

@Service
@Slf4j
public class NavigationServiceImpl implements NavigationService {
    private final DirectoryRepository dirRepository;
    private final FileRepository fileRepository;

    @Autowired
    public NavigationServiceImpl(DirectoryRepository dirRepository, FileRepository fileRepository) {
        this.dirRepository = dirRepository;
        this.fileRepository = fileRepository;
    }

    @Override
    public Directory accessDirectoryByPath(String path, User user) {
        DirectoryWithAccessInfo dir = findDirectoryWithAccessInfoByPath(path, user);
        if (!dir.isUserAuthorized())
            throw new AccessDeniedException(user.getUsername(), path);
        return dir.getDirectory();
    }

    @Override
    public Directory accessDirectoryById(long id, User user) {
        DirectoryWithAccessInfo dir = findDirectoryWithAccessInfoById(id, user);
        if (!dir.isUserAuthorized())
            throw new AccessDeniedException(user.getUsername(), "Directory id " + id);
        return dir.getDirectory();
    }

    @Override
    public DirectoryWithAccessInfo findDirectoryWithAccessInfoByPath(String path, User user) {
        Directory curDir;
        try {
            curDir = dirRepository.loadRootDirectory();
        } catch (DaoException e) {
            log.error("Loading root directory  exception", e);
            throw new InternalServerError();
        }
        boolean userAuthorized = curDir.getOwner().contains(user);
        if (path.isEmpty()) {
            return new DirectoryWithAccessInfo(curDir, userAuthorized);
        }
        for (String dirName : path.split("/")) {
            curDir = curDir
                    .getSubDirs()
                    .stream()
                    .filter(d -> d.getName().equals(dirName))
                    .findAny()
                    .orElseThrow(() -> new DirectoryNotFoundException(path));
            if (!userAuthorized)
                userAuthorized = curDir.getOwner().contains(user);
        }
        return new DirectoryWithAccessInfo(curDir, userAuthorized);
    }

    @Override
    public DirectoryWithAccessInfo findDirectoryWithAccessInfoById(long id, User user) {
        Directory dir;
        try {
            dir = dirRepository.loadById(id).orElseThrow(() -> new DirectoryNotFoundException("Directory id: " + id));
        } catch (DaoException e) {
            log.error("Loading directory  by id exception", e);
            throw new InternalServerError();
        }
        return new DirectoryWithAccessInfo(dir, hasRightsOnDirectory(dir, user));
    }

    @Override
    public Directory createDirectory(long parentId, String dirName, User user) {
        if (invalidDirectoryName(dirName))
            throw new InvalidFileNameException(dirName);
        Directory dir = accessDirectoryById(parentId, user);
        if (fileOrDirExists(dir, dirName))
            throw new DirectoryAlreadyExistsException(dirName);
        Directory newDir = new Directory();
        newDir.setName(dirName);
        newDir.setOwner(Set.of(user));
        newDir.setParent(dir);
        try {
            dirRepository.saveDirectory(newDir);
        } catch (DaoException e) {
            log.error("Saving dir entity exception", e);
            throw new InternalServerError();
        }
        dir.getSubDirs().add(newDir);
        updateDirEntity(dir);
        return newDir;
    }

    @Override
    public void deleteDirectory(long id, User user) {
        Directory dir = accessDirectoryById(id, user);
        deleteDirectory(dir);
    }

    @Override
    public void renameDirectory(long id, User user, String newName) {
        if (invalidDirectoryName(newName))
            throw new InvalidFileNameException(newName);
        Directory dir = accessDirectoryById(id, user);
        if (fileOrDirExists(dir.getParent(), newName))
            throw new DirectoryAlreadyExistsException(newName);
        dir.setName(newName);
        updateDirEntity(dir);
    }

    @Override
    public Set<Directory> getSubDirectories(String path, User user) {
        return accessDirectoryByPath(path, user).getSubDirs();
    }

    @Override
    public Set<File> getFiles(String path, User user) {
        return accessDirectoryByPath(path, user).getFiles();
    }

    @Override
    public File accessFileByPath(String path, User user) {
        String parentDirPath = getParentDirectory(path);
        String filename = getFilename(path);
        if (invalidFilename(filename))
            throw new InvalidFileNameException(filename);
        Directory dir = accessDirectoryByPath(parentDirPath, user);
        return dir
                .getFiles()
                .stream()
                .filter(t -> t.getName().equals(filename))
                .findAny()
                .orElseThrow(() -> new FileNotFoundException(path));
    }

    @Override
    public File accessFileById(long id, User user) {
        File file;
        try {
            file = fileRepository.getFile(id).orElseThrow(() -> new FileNotFoundException("File id: " + id));
        } catch (DaoException e) {
            log.error("Error loading file by id {}", id, e);
            throw new InternalServerError();
        }
        if (!file.getOwner().equals(user) && !hasRightsOnDirectory(file.getParentDirectory(), user))
            throw new AccessDeniedException(user.getUsername(), "File id: " + id);
        return file;
    }

    @Override
    public File createFile(long parentId, String fileName, User user) {
        if (invalidFilename(fileName))
            throw new InvalidFileNameException(fileName);
        Directory dir = accessDirectoryById(parentId, user);
        if (fileOrDirExists(dir, fileName))
            throw new FileAlreadyExistsException(fileName);
        File file = new File();
        file.setName(fileName);
        file.setOwner(user);
        file.setParentDirectory(dir);
        Set<File> files = dir.getFiles();
        files.add(file);
        try {
            fileRepository.saveFile(file);
        } catch (DaoException e) {
            log.error("Error creating file entity", e);
            throw new InternalServerError();
        }
        updateDirEntity(dir);
        return file;
    }


    @Override
    public void deleteFile(long id, User user) {
        File file = accessFileById(id, user);
        deleteFile(file);
    }

    @Override
    public void renameFile(long id, User user, String newName) {
        if (invalidFilename(newName))
            throw new InvalidFileNameException(newName);
        File file = accessFileById(id,user);
        if (fileOrDirExists(file.getParentDirectory(), newName))
            throw new FileAlreadyExistsException(newName);
        file.setName(newName);
        updateFileEntity(file);
    }



    private void updateDirEntity(Directory dir) {
        try {
            dirRepository.updateDirectory(dir);
        } catch (DaoException exception) {
            log.error("Updating dir entity exception", exception);
            throw new InternalServerError();
        }
    }

    private void deleteDirectory(Directory dir) {
        try {
            dir.getSubDirs().forEach(this::deleteDirectory);
            dir.getFiles().forEach(this::deleteFile);
            dirRepository.deleteDirectory(dir);
        } catch (DaoException exception) {
            log.error("Deleting dir entity exception", exception);
            throw new InternalServerError();
        }
    }
    private void deleteFile(File file){
        try {
            fileRepository.deleteFile(file);
            Files.deleteIfExists(Path.of("files/" + file.getId()));
        } catch (DaoException exception) {
            log.error("Deleting file[{}] entity exception", file.getId(), exception);
            throw new InternalServerError();
        } catch (IOException exception) {
            log.info("Deleting file[{}]  exception", file.getId(), exception);
            throw new InternalServerError();
        }
    }

    public void updateFileEntity(File file) {
        try {
            fileRepository.updateFile(file);
        } catch (DaoException exception) {
            log.error("Updating file entity exception", exception);
            throw new InternalServerError();
        }
    }


    private boolean fileOrDirExists(Directory dir, String filename) {
        return (dir.getFiles() != null &&
                dir.getFiles().stream().anyMatch(t -> t.getName().equals(filename))) ||
                (dir.getSubDirs() != null &&
                        dir.getSubDirs().stream().anyMatch(t -> t.getName().equals(filename)));
    }

    private String getParentDirectory(String path) {
        int endIndex = path.lastIndexOf('/');
        if (endIndex == -1)
            return "";
        return path.substring(0, endIndex);
    }

    private boolean invalidDirectoryName(String dirName) {
        return dirName.isBlank();
    }

    private boolean invalidFilename(String filename) {
        return filename.isBlank();
    }

    private String getFilename(String path) {
        return path.substring(path.lastIndexOf('/') + 1);
    }

    private boolean hasRightsOnDirectory(Directory directory, User user) {
        boolean isAuthenticated = false;
        while (!isAuthenticated && directory != null) {
            isAuthenticated = directory.getOwner().contains(user);
            directory = directory.getParent();
        }
        return isAuthenticated;
    }
}
