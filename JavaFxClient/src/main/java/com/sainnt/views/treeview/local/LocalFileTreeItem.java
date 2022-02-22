package com.sainnt.views.treeview.local;

import com.sainnt.files.FileRepresentation;
import com.sainnt.files.LocalFileRepresentation;
import com.sainnt.views.treeview.FileTreeItem;
import javafx.scene.control.Alert;
import javafx.scene.control.ContextMenu;
import javafx.scene.control.MenuItem;
import javafx.scene.control.TextInputDialog;
import lombok.extern.slf4j.Slf4j;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Optional;

@Slf4j
public class LocalFileTreeItem extends FileTreeItem {

    public LocalFileTreeItem(FileRepresentation fileRepresentation) {
        super(fileRepresentation);
    }

    @Override
    public FileTreeItem getTreeItemForFile(FileRepresentation file) {
        return new LocalFileTreeItem(file);
    }

    @SuppressWarnings("ResultOfMethodCallIgnored")
    @Override
    public ContextMenu getMenu(Runnable startEdit) {
        MenuItem renameItem = new MenuItem("Rename");
        renameItem.setOnAction(actionEvent -> startEdit.run());
        MenuItem deleteItem = new MenuItem("Delete");
        if (getValue().isFile()) {
            deleteItem.setOnAction(actionEvent -> ((LocalFileRepresentation)getValue()).getFile().delete() );
            return new ContextMenu(renameItem,deleteItem);
        } else {
            deleteItem.setOnAction(actionEvent -> deleteDirectory( ((LocalFileRepresentation)getValue()).getFile()));
            MenuItem createItem = new MenuItem("Create directory");
            createItem.setOnAction(actionEvent -> {
                TextInputDialog dialog = new TextInputDialog();
                dialog.setTitle("Create directory");
                dialog.setHeaderText("Enter directory name:");
                dialog.setContentText("Name:");
                Optional<String> result = dialog.showAndWait();
                if (result.isPresent() && !createDirectory(result.get()))
                    createItem.fire();
            });
            return new ContextMenu(renameItem, createItem,deleteItem);
        }

    }
    private boolean createDirectory(String name) {
        Path newDirPath = ((LocalFileRepresentation) getValue()).getFile().toPath().resolve(name);
        if (Files.exists(newDirPath)) {
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.setHeaderText("Could not create directory");
            alert.setContentText("Directory with name \"" + name + "\" already exists");
            alert.showAndWait();
            return false;
        }
        try {
            Files.createDirectory(newDirPath);
        } catch (IOException e) {
            log.error("Error creating directory",e);
            return false;
        }
        return true;
    }
    private boolean deleteDirectory(File dir){
        File[] subFiles = dir.listFiles();
        if(subFiles!=null)
        {
            for (File f : subFiles) {
                if (!deleteDirectory(f))
                    return false;
            }
        }
        return dir.delete();
    }
}
