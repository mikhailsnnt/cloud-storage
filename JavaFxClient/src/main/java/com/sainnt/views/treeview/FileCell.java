package com.sainnt.views.treeview;

import com.sainnt.files.FileRepresentation;
import javafx.scene.control.TreeCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;

import java.util.List;

public class FileCell extends TreeCell<FileRepresentation> {
    private static final Background activeBackground = Background.fill(Paint.valueOf("#33bd4a"));

    @Override
    protected void updateItem(FileRepresentation file, boolean b) {
        super.updateItem(file, b);
        if (file == null) {
            setGraphic(null);
            setText(null);
            return;
        }
        setText(file.getName());
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
}
