package com.sainnt.views.treeview.remote;

import com.sainnt.files.FileRepresentation;
import com.sainnt.files.RemoteFileRepresentation;
import com.sainnt.net.CloudClient;
import com.sainnt.views.treeview.FilesView;
import javafx.application.Platform;
import javafx.geometry.Pos;
import javafx.scene.control.ProgressIndicator;
import javafx.scene.control.TreeItem;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

public class RemoteFilesView extends StackPane {
    private final FilesView filesView;
    private VBox progressIndicatorBox;

    public RemoteFilesView() {
        filesView = new FilesView() {
            @Override
            protected void processExpand(TreeItem.TreeModificationEvent<FileRepresentation> event) {

            }

            @Override
            protected void processCollapse(TreeItem.TreeModificationEvent<FileRepresentation> event) {

            }
        };
    }

    public void showProgressIndicator(){
        Platform.runLater(()->{
            if (progressIndicatorBox == null) {
                progressIndicatorBox = new VBox();
                ProgressIndicator indicator = new ProgressIndicator();
                progressIndicatorBox.setAlignment(Pos.CENTER);
                progressIndicatorBox.getChildren().add(indicator);
            }
            filesView.setDisable(true);
            getChildren().add(progressIndicatorBox);
        });
    }
    public void hideProgressIndicator() {
        Platform.runLater(()->{
            filesView.setDisable(false);
            getChildren().remove(progressIndicatorBox);
        });
    }

    public void load(){
        filesView.setCellFactory(fileRepresentationTreeView -> new RemoteFileCell());
        filesView.setShowRoot(false);
        RemoteFileRepresentation rootItem = new RemoteFileRepresentation(1, null, "", true);
        filesView.initiateRoot(new RemoteFileTreeItem(rootItem));
        CloudClient.getClient().addRemoteFileRepresentation(rootItem);
        getChildren().add(filesView);
    }


}
