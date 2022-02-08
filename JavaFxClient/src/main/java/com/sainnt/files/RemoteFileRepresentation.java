package com.sainnt.files;

import com.sainnt.net.CloudClient;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;

import java.io.File;

public class RemoteFileRepresentation implements FileRepresentation{
    private final String path;
    private final String name;
    private  boolean firstTimeLoad = true;
    private final boolean isDirectory;
    private final ObservableList<FileRepresentation> children;

    public RemoteFileRepresentation(String path, String name, boolean isDirectory) {
        this.path = path;
        this.name = name;
        this.isDirectory = isDirectory;
        children = FXCollections.observableArrayList();
    }

    @Override
    public String getPath() {
        if(path.isBlank())
            return name;
        return path+"/"+name;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public boolean isDirectory() {
        return isDirectory;
    }

    @Override
    public ObservableList<FileRepresentation> getChildren() {
        return children;
    }

    @Override
    public File getFile() {
        return null ;
    }


    @Override
    public void copyFileToDirectory(File file) {

    }

    @Override
    public void loadContent(){
        if(firstTimeLoad && isDirectory) {
            CloudClient.getClient().requestChildrenFiles(getPath(), children);
            firstTimeLoad = false;
        }
    }
}
