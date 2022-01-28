package com.sainnt.views.treeview;

import com.sainnt.files.FileRepresentation;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.scene.control.TreeItem;

import java.util.stream.Collectors;

public class FileTreeItem extends TreeItem<FileRepresentation> {
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

}
