package com.sainnt.views.treeview.local;

import com.sainnt.files.FileRepresentation;
import com.sainnt.views.treeview.FileTreeItem;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import lombok.extern.slf4j.Slf4j;

@Slf4j
public class LocalFileTreeItem extends FileTreeItem {

    public LocalFileTreeItem(FileRepresentation fileRepresentation) {
        super(fileRepresentation);
    }

    @Override
    public FileTreeItem getTreeItemForFile(FileRepresentation file) {
        return new LocalFileTreeItem(file);
    }

    @Override
    public ContextMenu getMenu(Runnable startEdit) {
        if (getValue().isFile()) {
            MenuItem renameItem = new MenuItem("Rename");
            renameItem.setOnAction(actionEvent -> startEdit.run());
            return new ContextMenu(renameItem);
        } else {
            return new ContextMenu();
        }
    }
}
