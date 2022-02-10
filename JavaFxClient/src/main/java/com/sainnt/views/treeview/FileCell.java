package com.sainnt.views.treeview;

import com.sainnt.exception.FileAlreadyExistsException;
import com.sainnt.exception.FileRenamingFailedException;
import com.sainnt.files.FileRepresentation;
import javafx.scene.control.Alert;
import javafx.scene.control.TextField;
import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.KeyCode;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;

import java.util.List;

public class FileCell extends TreeCell<FileRepresentation> {
    private TextField textField;
    private static final Background activeBackground = Background.fill(Paint.valueOf("#33bd4a"));

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

    public FileCell() {
        setOnDragDetected(event -> {

            if (getItem() != null) {
                Dragboard db = startDragAndDrop(TransferMode.COPY);
                setBackground(Background.fill(Paint.valueOf("#26829e")));
                ClipboardContent content = new ClipboardContent();
                content.putFiles(List.of(getItem().getFile()));
                db.setContent(content);
                setTextFill(Paint.valueOf("#000000"));
            }
            event.consume();
        });
        setOnDragDropped(dragEvent -> {
            Dragboard db = dragEvent.getDragboard();
            boolean success = false;
            if (db.hasFiles()) {
                db.getFiles().forEach(f -> getItem().copyFileToDirectory(f));
                success = true;
            }
            dragEvent.setDropCompleted(success);
            dragEvent.consume();
        });
        setOnDragOver(dragEvent -> {
            if (isAbleToDrop(getItem(), dragEvent.getDragboard())) {
                dragEvent.acceptTransferModes(TransferMode.COPY_OR_MOVE);
                setBackground(activeBackground);
            }
            dragEvent.consume();
        });
        setOnDragExited(dragEvent -> {
            if (getBackground() == activeBackground) {
                setBackground(null);
                updateItem(getItem(), false);
            }
            dragEvent.consume();
        });
        setOnDragDone(dragEvent -> {
            setBackground(null);
            updateItem(getItem(), false);
            dragEvent.consume();
        });
    }

    private boolean isAbleToDrop(FileRepresentation item, Dragboard dragboard) {
        if (item == null || item.isFile() || !dragboard.hasFiles())
            return false;
        // Recursive copying check:
        return (!item.getPath().startsWith(dragboard.getFiles().get(0).toString() + ""));
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
                }
                catch (FileRenamingFailedException e){
                    displayError("File renaming failed",e.getMessage());
                    return;
                }
                commitEdit(getItem());
                updateItem(getItem(), false);
            } else if (keyEvent.getCode() == KeyCode.ESCAPE) {
                cancelEdit();
            }
        });
    }

    private void displayError(String header, String content) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setHeaderText(header);
        alert.setContentText(content);
        alert.showAndWait();
    }

}
