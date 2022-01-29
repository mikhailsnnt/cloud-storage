package com.sainnt.views.treeview;

import com.sainnt.files.FileRepresentation;
import com.sainnt.observer.DirectoryObserver;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.stream.Collectors;

public class FileTreeItem extends TreeItem<FileRepresentation> implements DirectoryObserver {
    private boolean firstTimeLoad = true;
    public FileTreeItem() {
    }

    public FileTreeItem(FileRepresentation fileRepresentation) {
        super(fileRepresentation);
    }

    @Override
    public boolean isLeaf() {
        return !getValue().isDirectory();
    }

    @Override
    public ObservableList<TreeItem<FileRepresentation>> getChildren() {
        if(firstTimeLoad){
            super.getChildren().setAll(
                    getValue()
                         .getChildren()
                            .stream()
                            .map(FileTreeItem::new)
                             .collect(Collectors.toCollection(FXCollections::observableArrayList)));
            firstTimeLoad = false;
        }
        return super.getChildren();
    }


    @Override
    public void fileAdded(FileRepresentation file) {
        getChildren().add(new FileTreeItem(file));
    }

    @Override
    public void fileRemoved(FileRepresentation file) {
        System.out.println(file.getFile().getAbsolutePath());
        TreeItem<FileRepresentation> treeItem = getChildren().stream().filter(t -> t.getValue().getFile().getAbsolutePath().equals(file.getFile().getAbsolutePath())).findFirst().orElseThrow();
        System.out.println("Removing "+ file.getName());
        getChildren().remove(treeItem);
    }

    @Override
    public void fileModified(FileRepresentation file) {
        System.out.println("TreeItem::File modified");
    }

    @Override
    public FileRepresentation getDirectory() {
        return getValue();
    }
}
