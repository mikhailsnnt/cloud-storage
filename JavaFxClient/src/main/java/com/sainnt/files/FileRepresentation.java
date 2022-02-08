package com.sainnt.files;


import javafx.collections.ObservableList;

import java.io.File;
import java.util.List;
public interface FileRepresentation {
     String getPath();
     String getName();
    boolean isDirectory();
    ObservableList<FileRepresentation> getChildren();
    File getFile();
    void copyFileToDirectory(File file);
    void loadContent();
}
