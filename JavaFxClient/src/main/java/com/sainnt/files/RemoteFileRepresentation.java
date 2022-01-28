package com.sainnt.files;

import java.io.File;
import java.util.List;

public class RemoteFileRepresentation implements FileRepresentation{
    private final String path;
    private final String name;
    private final boolean isDirectory;

    public RemoteFileRepresentation(String path, String name, boolean isDirectory) {
        this.path = path;
        this.name = name;
        this.isDirectory = isDirectory;
    }

    @Override
    public String getPath() {
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
    public List<FileRepresentation> getChildren() {
        return List.of();
    }

    @Override
    public File getFile() {
        return null ;
    }


    @Override
    public void copyFileToDirectory(File file) {

    }
}
