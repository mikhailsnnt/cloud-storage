package com.sainnt;

import com.sainnt.files.RemoteFileRepresentation;
import com.sainnt.views.LocalFilesView;
import com.sainnt.views.treeview.FilesView;
import javafx.application.Application;
import javafx.event.EventHandler;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.input.DragEvent;
import javafx.scene.input.Dragboard;
import javafx.scene.layout.AnchorPane;
import javafx.scene.layout.Background;
import javafx.scene.layout.Priority;
import javafx.scene.layout.VBox;
import javafx.scene.paint.Paint;
import javafx.stage.Stage;

import java.io.IOException;

/**
 * JavaFX App
 */
public class App extends Application {


    private LocalFilesView localFilesView;
    private FilesView remoteFilesView;
    @Override
    public void start(Stage stage)  {
        localFilesView = new LocalFilesView();
        remoteFilesView  = new FilesView();
        remoteFilesView.setRoot(new RemoteFileRepresentation("/","",true));
        initializeMainScene(stage);
    }

    public void initializeMainScene(Stage stage){
        //Initialising file views
        AnchorPane localSide = getColoredPaneWithView(this.localFilesView, "#FFFAF0");
        AnchorPane remoteSide = getColoredPaneWithView(this.remoteFilesView, "#E0FFFF");
        SplitPane splitPane = new SplitPane(localSide,remoteSide);
        splitPane.setStyle("-fx-focus-color:transparent");
        splitPane.setDividerPosition(1,0.5);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        //Initialising menu bar
        Menu mainMenu = new Menu("Cloud-storage");
        MenuItem preferencesItem = new MenuItem("Preferences");
        MenuItem quitItem = new MenuItem("Quit");
        mainMenu.getItems().addAll(preferencesItem, new SeparatorMenuItem(), quitItem);
        Menu editMenu = new Menu("Edit");
        MenuItem  undoEditItem = new MenuItem("Undo");
        MenuItem redoEditItem = new MenuItem("Redo");

        editMenu.getItems().addAll(
                undoEditItem,
                redoEditItem);
        Menu helpMenu = new Menu("Help");
        MenuItem aboutHelpItem = new MenuItem("About Cloud-storage");
        helpMenu.getItems().addAll(aboutHelpItem);
        MenuBar menuBar = new MenuBar(mainMenu, editMenu, helpMenu);

        VBox viewBox = new VBox(menuBar, splitPane);
        Scene scene = new Scene(viewBox, 900, 600);
        stage.setScene(scene);
        stage.show();
    }

    private AnchorPane getColoredPaneWithView(Node node, String color) {
        AnchorPane pane = new AnchorPane(node);
        pane.setBackground(Background.fill(Paint.valueOf(color)));
        node.setOpacity(0.6);
        AnchorPane.setBottomAnchor(node,0.0);
        AnchorPane.setTopAnchor(node,0.0);
        AnchorPane.setLeftAnchor(node,0.0);
        AnchorPane.setRightAnchor(node,0.0);
        return pane;
    }



    public static void main(String[] args) {
        launch();
    }

}