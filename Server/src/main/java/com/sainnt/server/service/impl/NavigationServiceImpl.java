package com.sainnt.server.service.impl;

import com.sainnt.server.dao.DaoException;
import com.sainnt.server.dao.DirectoryRepository;
import com.sainnt.server.dao.FileRepository;
import com.sainnt.server.entity.Directory;
import com.sainnt.server.entity.File;
import com.sainnt.server.entity.User;
import com.sainnt.server.exception.*;
import com.sainnt.server.service.NavigationService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

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
    public Directory findDirectoryByPath(String path, User user) {
        Directory curDir;
        try{
            curDir = dirRepository.loadRootDirectory();
        }catch (DaoException exception){
            log.error("Loading dir entity exception",exception);
            throw  new InternalServerError();
        }
        boolean userAuthorized = curDir.getOwner().contains(user);
        for (String dirName : path.split("/"))
        {
            curDir = curDir
                    .getSubDirs()
                    .stream()
                    .filter(d->d.getName().equals(dirName))
                    .findAny()
                    .orElseThrow(()->new DirectoryNotFoundException(path));
            if(!userAuthorized)
                userAuthorized = curDir.getOwner().contains(user);
        }
        if(!userAuthorized)
            throw  new AccessDeniedException(user.getUsername(),path);
        return curDir;
    }

    @Override
    public Directory createDirectory(String path, User user) {
        String parentDirectory = getParentDirectory(path);
        if(parentDirectory == null) //Bad format
            throw new AccessDeniedException(user.getUsername(),"/");
        String dirName = getFilename(path);
        if(invalidDirectoryName(dirName))
            throw new InvalidFileNameException(dirName);
        Directory dir = findDirectoryByPath(parentDirectory, user);
        if(fileOrDirExists(dir,dirName))
            throw new DirectoryAlreadyExists(path);
        Directory newDir = new Directory();
        newDir.setName(dirName);
        newDir.setOwner(Set.of(user));
        newDir.setParent(dir);
        try{
            dirRepository.saveDirectory(newDir);
        }catch (DaoException e){
            log.error("Saving dir entity exception",e);
            throw  new InternalServerError();
        }
        dir.getSubDirs().add(newDir);
        updateDirEntity(dir);
        return newDir;
    }

    @Override
    public void deleteDirectory(String path, User user) {
        Directory dir = findDirectoryByPath(path, user);
        deleteDirectory(dir);
    }

    @Override
    public void renameDirectory(String path, User user, String newName) {
        if(invalidDirectoryName(newName))
            throw  new InvalidFileNameException(newName);
        String parentPath = getParentDirectory(path);
        Directory parendDir = findDirectoryByPath(parentPath, user);
        if(fileOrDirExists(parendDir,newName))
            throw new FileAlreadyExistsException(path+"/"+newName);
        String oldName = getFilename(path);
        Directory dir = parendDir.getSubDirs()
                .stream()
                .filter(t->t.getName().equals(oldName))
                .findFirst()
                        .orElseThrow(()->new DirectoryNotFoundException(path));
        dir.setName(newName);
        updateDirEntity(dir);
    }

    @Override
    public Set<Directory> getSubDirectories(String path, User user) {
        return findDirectoryByPath(path,user).getSubDirs();
    }

    @Override
    public Set<File> getFiles(String path, User user) {
        return findDirectoryByPath(path,user).getFiles();
    }

    @Override
    public File getFileByPath(String path, User user) {
        String parentDirPath = getParentDirectory(path);
        String filename = getFilename(path);
        if(invalidFilename(filename))
            throw  new InvalidFileNameException(filename);
        Directory dir = findDirectoryByPath(parentDirPath,user);
        return dir
                .getFiles()
                .stream()
                .filter(t->t.getName().equals(filename))
                .findAny()
                .orElseThrow(()->new FileNotFoundException(path));
    }

    @Override
    public File createFile(String path, User user) {
        String parentDirectory = getParentDirectory(path);
        if(parentDirectory == null) //Bad format
            throw new AccessDeniedException(user.getUsername(),"/");
        String fileName = getFilename(path);
        if(invalidFilename(fileName))
            throw new InvalidFileNameException(fileName);
        Directory dir = findDirectoryByPath(parentDirectory, user);
        if(fileOrDirExists(dir, fileName))
            throw new FileAlreadyExistsException(path);
        File file = new File();
        file.setName(fileName);
        file.setOwner(user);
        file.setParentDirectory(dir);
        dir.getFiles().add(file);
        try {
            fileRepository.saveFile(file);
        }catch (DaoException e){
            log.error("Error creating file entity",e);
            throw new InternalServerError();
        }
        updateDirEntity(dir);
        return file;
    }


    @Override
    public void deleteFile(String path, User user) {
        try {
            fileRepository.deleteFile(getFileByPath(path, user));
        }
        catch (DaoException exception){
            log.error("Deleting file entity exception",exception);
            throw  new InternalServerError();
        }
    }

    @Override
    public void renameFile(String path, User user, String newName) {
        if(invalidFilename(newName))
            throw  new InvalidFileNameException(newName);
        String parentPath = getParentDirectory(path);
        Directory parendDir = findDirectoryByPath(parentPath, user);
        if (fileOrDirExists(parendDir, newName))
            throw  new FileAlreadyExistsException(path+"/"+newName);
        String oldName = getFilename(path);
        File file = parendDir.getFiles()
                .stream()
                .filter(t->t.getName().equals(oldName))
                .findFirst()
                .orElseThrow(()->new FileNotFoundException(path));
        file.setName(newName);
        updateFileEntity(file);
    }


    private void saveDirEntity(Directory dir){

    }
    private void updateDirEntity(Directory dir){
        try{
            dirRepository.updateDirectory(dir);
        }catch (DaoException exception){
            log.error("Updating dir entity exception",exception);
            throw  new InternalServerError();
        }
    }
    private void deleteDirectory(Directory dir){
        try {
            dirRepository.deleteDirectory(dir);
        }
        catch (DaoException exception){
            log.error("Deleting dir entity exception",exception);
            throw new InternalServerError();
        }
    }
    public void updateFileEntity(File file){
        try{
            fileRepository.updateFile(file);
        } catch (DaoException exception) {
            log.error("Updating file entity exception",exception);
            throw new InternalServerError();
        }
    }


    private boolean fileOrDirExists(Directory dir , String filename){
        return (dir.getFiles()!=null &&
                dir.getFiles().stream().anyMatch(t -> t.getName().equals(filename)) )||
                (dir.getSubDirs()!=null &&
                dir.getSubDirs().stream().anyMatch(t -> t.getName().equals(filename)));
    }
    private String getParentDirectory(String path){
        int endIndex = path.lastIndexOf('/');
        if (endIndex == -1)
            return null;
        return path.substring(0, endIndex);
    }
    private boolean invalidDirectoryName(String dirName){
        return dirName.isBlank();
    }
    private boolean invalidFilename(String filename)
    {
        return filename.isBlank();
    }
    private String getFilename(String path){
        return path.substring( path.lastIndexOf('/')+1);
    }
}
