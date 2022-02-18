package com.sainnt.files;

import com.sainnt.net.CloudClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class RemoteFileRepresentation implements FileRepresentation {
    private final long id;
    private RemoteFileRepresentation parent;
    private final String name;
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
    public File getFile() {
        return null;
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
    public void setName(String name) {
        CloudClient.getClient().renameFileRequest(getId(), name);
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
}
