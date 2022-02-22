package com.sainnt;

import com.sainnt.controller.LoginPageController;
import com.sainnt.net.CloudClient;
import com.sainnt.views.treeview.local.LocalFilesView;
import com.sainnt.views.treeview.remote.RemoteFilesView;
import javafx.application.Application;
import javafx.application.Platform;
import javafx.fxml.FXMLLoader;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
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
    private Stage stage;

    @Override
    public void start(Stage stage) {
        this.stage = stage;
        CloudClient.getClient();
        this.stage.setOnCloseRequest(windowEvent -> closeApp());
        initializeLoginScene();

    }

    public void initializeLoginScene() {
        FXMLLoader fxmlLoader = new FXMLLoader(getClass().getResource("login_scene.fxml"));
        try {
            Scene scene = new Scene(fxmlLoader.load());
            ((LoginPageController) fxmlLoader.getController()).setOnLoggedIn(this::initializeMainScene);
            stage.setTitle("Log in or sign up");
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }

    public void initializeMainScene() {
        //Initialising file views
        LocalFilesView localFilesView = new LocalFilesView();
        RemoteFilesView remoteFilesView = new RemoteFilesView();
        CloudClient.getClient().setOnRequestStarted(remoteFilesView::showProgressIndicator);
        CloudClient.getClient().setOnRequestCompleted(remoteFilesView::hideProgressIndicator);
        AnchorPane localSide = getColoredPaneWithView(localFilesView, "#FFFAF0");
        AnchorPane remoteSide = getColoredPaneWithView(remoteFilesView, "#E0FFFF");
        SplitPane splitPane = new SplitPane(localSide, remoteSide);
        splitPane.setStyle("-fx-focus-color:transparent");
        splitPane.setDividerPosition(1, 0.5);
        VBox.setVgrow(splitPane, Priority.ALWAYS);

        //Initialising menu bar
        Menu mainMenu = new Menu("Cloud-storage");
        MenuItem preferencesItem = new MenuItem("Preferences");
        MenuItem quitItem = new MenuItem("Quit");
        quitItem.setOnAction(t -> closeApp());
        mainMenu.getItems().addAll(preferencesItem, new SeparatorMenuItem(), quitItem);
        Menu editMenu = new Menu("Edit");
        MenuItem undoEditItem = new MenuItem("Undo");
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
        stage.setTitle("Cloud-storage");
        stage.setScene(scene);
        stage.show();


    }

    private AnchorPane getColoredPaneWithView(Node node, String color) {
        AnchorPane pane = new AnchorPane(node);
        pane.setBackground(Background.fill(Paint.valueOf(color)));
        node.setOpacity(0.6);
        AnchorPane.setBottomAnchor(node, 0.0);
        AnchorPane.setTopAnchor(node, 0.0);
        AnchorPane.setLeftAnchor(node, 0.0);
        AnchorPane.setRightAnchor(node, 0.0);
        return pane;
    }

    private void closeApp() {
        CloudClient.getClient().closeConnection();
        Platform.exit();
    }

    public static void main(String[] args) {
        launch();
    }

}