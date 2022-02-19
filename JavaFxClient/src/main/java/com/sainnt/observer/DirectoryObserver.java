package com.sainnt.observer;

import com.sainnt.files.FileRepresentation;
import com.sainnt.files.LocalFileRepresentation;

public interface DirectoryObserver {
    void fileAdded(FileRepresentation file);

    void fileRemoved(FileRepresentation file);

    void fileModified(FileRepresentation file);

    FileRepresentation getDirectory();
}
