package com.sainnt.views;

import com.sainnt.files.FileRepresentation;
import com.sainnt.files.LocalFileRepresentation;
import com.sainnt.views.treeview.FilesView;
import javafx.scene.control.TreeItem;

import java.nio.file.Path;
import java.nio.file.Paths;

public class LocalFilesView extends FilesView {
    public LocalFilesView() {
        setHomeDirPath();
        setShowRoot(false);
    }
    private void setHomeDirPath(){
        Path path = Paths.get(System.getProperty("user.home"));
        LocalFileRepresentation rootDir = new LocalFileRepresentation(path.getRoot());
        setRoot(rootDir);
        //Expanding path to home directory
        getRoot().setExpanded(true);
        int nameCount = path.getNameCount();
        TreeItem<FileRepresentation> current =  getRoot();
        for (int i = 0; i < nameCount; i++) {
            int finalI = i;
            current = current
                    .getChildren()
                    .stream()
                    .filter(t->t.getValue().getName().equals(path.getName(finalI).toString()))
                    .findFirst()
                    .orElseThrow(()->new RuntimeException("Path error"));
            current.setExpanded(true);
        }
        getSelectionModel().select(current);
    }
}
