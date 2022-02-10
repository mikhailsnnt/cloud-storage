package com.sainnt.views.treeview;

import com.sainnt.files.FileRepresentation;
import javafx.scene.control.TreeItem;
import javafx.scene.control.TreeView;

public abstract class FilesView extends TreeView<FileRepresentation> {
    public FilesView() {
        setCellFactory(new FileCellFactory());
        getStylesheets().add("tree-view-style.css");
    }

    public void initiateRoot(TreeItem<FileRepresentation> item) {
        setRoot(item);
        item.addEventHandler(TreeItem.branchExpandedEvent(), event -> ((FileRepresentation) event.getTreeItem().getValue()).loadContent());
        item.getValue().loadContent();
        getRoot().addEventHandler(TreeItem.branchExpandedEvent(), this::processExpand);
        getRoot().addEventHandler(TreeItem.branchCollapsedEvent(), this::processCollapse);
        item.setExpanded(true);
    }

    protected abstract void processExpand(FileTreeItem.TreeModificationEvent<FileRepresentation> event);

    protected abstract void processCollapse(FileTreeItem.TreeModificationEvent<FileRepresentation> event);
}
