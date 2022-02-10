package com.sainnt.views.treeview.remote;

import com.sainnt.files.FileRepresentation;
import com.sainnt.net.CloudClient;
import com.sainnt.views.treeview.FileTreeItem;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;

import java.util.Optional;
import java.util.function.Consumer;

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
            MenuItem renameItem = new MenuItem("Rename");
            renameItem.setOnAction(actionEvent -> startEdit.run());
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(actionEvent -> CloudClient.getClient().deleteFileRequest(getValue().getPath()));
            return new ContextMenu(renameItem,deleteItem);
        } else {
            MenuItem refreshItem = new MenuItem("Refresh");
            refreshItem.setOnAction(actionEvent -> CloudClient.getClient().requestChildrenFiles(getValue().getPath(), getValue().getChildren()));
            MenuItem createItem  = new MenuItem("Create directory");
            createItem.setOnAction(actionEvent -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Create directory");
                dialog.setHeaderText("Enter directory name:");
                dialog.setContentText("Name:");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent() && !createRemoteDirectory(result.get()))
                    createItem.fire();
            });
            MenuItem renameItem = new MenuItem("Rename");
            renameItem.setOnAction(actionEvent -> startEdit.run());
            MenuItem deleteItem = new MenuItem("Delete");
            deleteItem.setOnAction(actionEvent -> CloudClient.getClient().deleteDirectoryRequest(getValue().getPath()));
            return new ContextMenu(refreshItem,createItem,renameItem,deleteItem);
        }
    }
    private boolean createRemoteDirectory(String name){
        if (getValue().getChildren().stream().anyMatch(t->t.getName().equals(name)))
        {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText( "Could not create directory");
            alert.setContentText("Directory with name \""+name+"\" already exists");
            alert.showAndWait();
            return false;
        }
        CloudClient.getClient().createRemoteDirectory(getValue().getPath()+"/"+name);
        return true;
    }
}
