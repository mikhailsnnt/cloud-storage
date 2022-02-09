package com.sainnt.files;


import javafx.collections.ObservableList;

import java.io.File;

public interface FileRepresentation {
    String getPath();

    String getName();

    boolean isFile();

    ObservableList<FileRepresentation> getChildren();

    File getFile();

    void copyFileToDirectory(File file);

    void loadContent();
}
