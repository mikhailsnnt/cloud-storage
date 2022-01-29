package com.sainnt.files;


import java.io.File;
import java.util.List;
public interface FileRepresentation {
     String getPath();
     String getName();
    boolean isDirectory();
    List<FileRepresentation> getChildren();
    File getFile();
    void copyFileToDirectory(File file);
}
