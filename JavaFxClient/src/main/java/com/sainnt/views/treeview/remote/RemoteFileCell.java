package com.sainnt.views.treeview.remote;

import com.sainnt.dto.RemoteFileDto;
import com.sainnt.files.RemoteFileRepresentation;
import com.sainnt.views.treeview.FileCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.DataFormat;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;

public class RemoteFileCell extends FileCell {

    @Override
    protected void initializeEventHandlers() {
        setOnDragDetected(event -> {
            if (getItem() != null) {
                Dragboard db = startDragAndDrop(TransferMode.MOVE);
                setBackground(Background.fill(Paint.valueOf("#26829e")));
                ClipboardContent content = new ClipboardContent();
                content.put(RemoteFileDto.dataFormat, ((RemoteFileRepresentation)getItem()).getDto());
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
}
