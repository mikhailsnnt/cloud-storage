package com.sainnt.views.treeview;

import com.sainnt.dto.RemoteFileDto;
import com.sainnt.exception.FileAlreadyExistsException;
import com.sainnt.exception.FileRenamingFailedException;
import com.sainnt.files.FileRepresentation;
import com.sainnt.files.RemoteFileRepresentation;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;

public abstract class FileCell extends TreeCell<FileRepresentation> {
    private TextField textField;
    protected static final Background activeBackground = Background.fill(Paint.valueOf("#33bd4a"));

    public FileCell() {
        initializeEventHandlers();
    }

    protected abstract void initializeEventHandlers();

    @Override
    protected void updateItem(FileRepresentation file, boolean empty) {
        super.updateItem(file, empty);
        if (empty) {
            setGraphic(null);
            setText(null);
            return;
        }
        if (isEditing()) {
            setText(null);
            setGraphic(textField);
        } else {
            setText(file.getName());
            setContextMenu(((FileTreeItem) getTreeItem()).getMenu(this::startEdit));
            setGraphic(null);
        }
    }

    @Override
    public void startEdit() {
        super.startEdit();
        if (textField == null)
            initTextField();
        setText(null);
        textField.setText(getItem().getName());
        setGraphic(textField);
        textField.requestFocus();
        textField.selectRange(0, Math.max(0, getItem().getName().indexOf('.')));
    }


    @Override
    public void cancelEdit() {
        super.cancelEdit();
        setText(getItem().getName());
        setGraphic(null);
    }

    private void initTextField() {
        textField = new TextField();
        textField.setOnKeyReleased(keyEvent -> {
            if (keyEvent.getCode() == KeyCode.ENTER) {
                try {
                    getItem().setName(textField.getText());
                } catch (FileAlreadyExistsException e) {
                    displayError("Could not rename, file already exists", e.getMessage());
                    return;
                } catch (FileRenamingFailedException e) {
                    displayError("File renaming failed", e.getMessage());
                    return;
                }
                commitEdit(getItem());
                updateItem(getItem(), false);
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }

    protected void displayError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

    protected boolean isAbleToDrop(FileRepresentation item, Dragboard dragboard) {
        if (item == null || item.isFile())
            return false;
        // Recursive copying check:
        if(dragboard.hasFiles())
            return (!item.getPath().startsWith(dragboard.getFiles().get(0).toString() + ""));
        return dragboard.hasContent(RemoteFileDto.dataFormat);
    }
}
