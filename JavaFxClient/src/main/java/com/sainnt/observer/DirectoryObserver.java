package com.sainnt.observer;

import com.sainnt.files.FileRepresentation;

public interface DirectoryObserver {
    void fileAdded(FileRepresentation file);

    void fileRemoved(FileRepresentation file);

    void fileModified(FileRepresentation file);

    FileRepresentation getDirectory();
}
