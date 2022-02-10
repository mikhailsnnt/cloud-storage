package com.sainnt.views.treeview;

import com.sainnt.files.FileRepresentation;
import com.sainnt.observer.DirectoryObserver;
import com.sainnt.views.CustomListBinder;
import javafx.collections.ObservableList;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.TreeItem;

public abstract class FileTreeItem extends TreeItem<FileRepresentation> implements DirectoryObserver {
    private boolean firstTimeLoad = true;

    public FileTreeItem(FileRepresentation fileRepresentation) {
        super(fileRepresentation);
        CustomListBinder.bindLists(super.getChildren(), fileRepresentation.getChildren(), this::getTreeItemForFile);
    }

    @Override
    public boolean isLeaf() {
        return getValue().isFile();
    }

    @Override
    public ObservableList<TreeItem<FileRepresentation>> getChildren() {
        if (firstTimeLoad) {
            getValue().getChildren();
            firstTimeLoad = false;
        }
        return super.getChildren();
    }

    public abstract FileTreeItem getTreeItemForFile(FileRepresentation file);


    @Override
    public void fileAdded(FileRepresentation file) {
        getChildren().add(getTreeItemForFile(file));
    }

    @Override
    public void fileRemoved(FileRepresentation file) {
        getChildren().removeIf(t -> t.getValue().getName().equals(file.getName()));
    }

    @Override
    public void fileModified(FileRepresentation file) {
        System.out.println("TreeItem::File modified");
    }

    @Override
    public FileRepresentation getDirectory() {
        FileRepresentation value = getValue();
        if (value.isFile())
            throw new RuntimeException("Directory expected but file found:" + value.getPath());
        return value;
    }

    public abstract ContextMenu getMenu(Runnable startEdit);
}
