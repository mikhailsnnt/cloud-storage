package com.sainnt.views.treeview;

import com.sainnt.files.FileRepresentation;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public class FilesView extends TreeView<FileRepresentation> {
    public FilesView() {
        setCellFactory(new FileCellFactory());
        getStylesheets().add("tree-view-style.css");

    }

    public void setRoot(FileRepresentation fileRepresentation) {
        FileTreeItem treeItem = new FileTreeItem(fileRepresentation);
        setRoot(treeItem);
        getRoot().addEventHandler(TreeItem.branchExpandedEvent(), event -> ((FileRepresentation) event.getTreeItem().getValue()).loadContent());
        fileRepresentation.loadContent();
        treeItem.setExpanded(true);
    }
}
