package com.sainnt.files;

import com.sainnt.dto.RemoteFileDto;
import com.sainnt.net.CloudClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public class RemoteFileRepresentation implements FileRepresentation {
    private final long id;
    private RemoteFileRepresentation parent;
    private  String name;
    private boolean firstTimeLoad = true;
    private final boolean isDirectory;
    private final ObservableList<FileRepresentation> children;

    public RemoteFileRepresentation(long id, RemoteFileRepresentation parent, String name, boolean isDirectory) {
        this.id = id;
        this.parent = parent;
        this.name = name;
        this.isDirectory = isDirectory;
        children = FXCollections.observableArrayList();
    }

    @Override
    public String getPath() {
        if (parent == null)
            return name;
        return parent.getPath() + "/" + name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isFile() {
        return !isDirectory;
    }

    @Override
    public ObservableList<FileRepresentation> getChildren() {
        return children;
    }


    @Override
    public void copyFileToDirectory(File file) {
        CloudClient.getClient().uploadFile(getId(), file);
    }

    @Override
    public void loadContent() {
        if (firstTimeLoad && isDirectory) {
            CloudClient.getClient().requestChildrenFiles(getId());
            firstTimeLoad = false;
        }
    }

    @Override
    public void rename(String name, Runnable onComplete) {
        if(isDirectory)
            CloudClient.getClient().renameDirectoryRequest(getId(),name, onComplete);
        else
            CloudClient.getClient().renameFileRequest(getId(), name, onComplete);
    }

    public void setName(String name) {
        this.name = name;
    }

    public long getId() {
        return id;
    }

    public void setParent(RemoteFileRepresentation parent) {
        this.parent = parent;
    }

    public RemoteFileRepresentation getParent() {
        return parent;
    }

    public RemoteFileDto getDto() {
        return new RemoteFileDto(name, id);
    }
}
