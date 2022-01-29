package com.sainnt.views.treeview;

import com.sainnt.files.FileRepresentation;
import javafx.scene.control.TreeCell;
import javafx.scene.control.TreeView;
import javafx.util.Callback;



public class FileCellFactory implements Callback<TreeView<FileRepresentation>, TreeCell<FileRepresentation>> {

    @Override
    public TreeCell<FileRepresentation> call(TreeView<FileRepresentation> fileTreeView) {
        return new FileCell();
    }

}
