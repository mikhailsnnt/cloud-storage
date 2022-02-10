package com.sainnt.views.treeview.remote;

import com.sainnt.files.FileRepresentation;
import com.sainnt.net.CloudClient;
import com.sainnt.views.treeview.FileTreeItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;

public class RemoteFileTreeItem extends FileTreeItem {
    public RemoteFileTreeItem(FileRepresentation fileRepresentation) {
        super(fileRepresentation);
    }

    @Override
    public FileTreeItem getTreeItemForFile(FileRepresentation file) {
        return new RemoteFileTreeItem(file);
    }

    @Override
    public ContextMenu getMenu(Runnable startEdit) {
        if (getValue().isFile()) {
//            MenuItem deleteItem = new MenuItem("Delete");
//            deleteItem.setOnAction(actionEvent -> CloudClient.getClient().);
            MenuItem renameItem = new MenuItem("Rename");
            renameItem.setOnAction(actionEvent -> startEdit.run());
            return new ContextMenu(renameItem);
        } else {
            MenuItem refreshItem = new MenuItem("Refresh");
            refreshItem.setOnAction(actionEvent -> CloudClient.getClient().requestChildrenFiles(getValue().getPath(), getValue().getChildren()));
            return new ContextMenu(refreshItem);
        }
    }
}
