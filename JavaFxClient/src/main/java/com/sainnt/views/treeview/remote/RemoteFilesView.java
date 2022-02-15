package com.sainnt.views.treeview.remote;

import com.sainnt.files.FileRepresentation;
import com.sainnt.files.RemoteFileRepresentation;
import com.sainnt.net.CloudClient;
import com.sainnt.views.treeview.FilesView;
import javafx.scene.control.TreeItem;

public class RemoteFilesView extends FilesView {
    public RemoteFilesView() {
        setShowRoot(false);
        RemoteFileRepresentation rootItem = new RemoteFileRepresentation(1, null, "", true);
        initiateRoot(new RemoteFileTreeItem(rootItem));
        CloudClient.getClient().addRemoteFileRepresentation(rootItem);
    }

    @Override
    protected void processExpand(TreeItem.TreeModificationEvent<FileRepresentation> event) {

    }

    @Override
    protected void processCollapse(TreeItem.TreeModificationEvent<FileRepresentation> event) {

    }
}
