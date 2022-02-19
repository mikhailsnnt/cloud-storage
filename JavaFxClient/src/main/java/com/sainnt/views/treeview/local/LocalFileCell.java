package com.sainnt.views.treeview.local;

import com.sainnt.dto.RemoteFileDto;
import com.sainnt.files.LocalFileRepresentation;
import com.sainnt.net.CloudClient;
import com.sainnt.views.treeview.FileCell;
import javafx.scene.input.ClipboardContent;
import javafx.scene.input.Dragboard;
import javafx.scene.input.TransferMode;
import javafx.scene.layout.Background;
import javafx.scene.paint.Paint;

import java.io.File;
import java.io.IOException;
import java.util.List;

public class LocalFileCell extends FileCell {
    @Override
    protected void initializeEventHandlers() {

        setOnDragDetected(event -> {
            if (getItem() != null) {
                Dragboard db = startDragAndDrop(TransferMode.COPY);
                setBackground(Background.fill(Paint.valueOf("#26829e")));
                ClipboardContent content = new ClipboardContent();
                LocalFileRepresentation localFile = (LocalFileRepresentation) getItem();
                content.putFiles(List.of(localFile.getFile()));
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
            RemoteFileDto remoteFile = (RemoteFileDto) db.getContent(RemoteFileDto.dataFormat);
            LocalFileRepresentation localFile = (LocalFileRepresentation) getItem();
            File file = localFile.getFile().toPath().resolve(remoteFile.getName()).toFile();
            if (file.exists()) {
                displayError("File upload exception", "File already exists " + file.getAbsolutePath());
                dragEvent.setDropCompleted(success);
                dragEvent.consume();
                return;
            }
            try {
                if (!file.createNewFile())
                    displayError("File upload failed", "File creation failed");
            } catch (IOException e) {
                displayError("File upload failed", "File creation IO Exception");
                e.printStackTrace();
                dragEvent.setDropCompleted(success);
                dragEvent.consume();
                return;
            }
            CloudClient.getClient().downloadFile(remoteFile.getId(),file);
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
