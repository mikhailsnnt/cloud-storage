package com.sainnt.controller;

import com.sainnt.dto.SignInResult;
import com.sainnt.dto.SignUpResult;
import com.sainnt.net.CloudClient;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.control.*;
import javafx.scene.layout.StackPane;
import javafx.scene.layout.VBox;

import java.net.URL;
import java.util.ResourceBundle;

public class LoginPageController implements Initializable {
    public TabPane tabPane;
    public StackPane rootPane;
    public Button signInButton;
    public Button signUpButton;
    private Runnable onLoggedIn;
    private VBox progressIndicatorBox;
    public TextField signInLoginTextField;
    public PasswordField signInPasswordField;
    public TextField signUpLoginTextField;
    public TextField signUpEmailTextField;
    public PasswordField signUpPasswordField;

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        CloudClient.getClient().initLoginHandler(this::handleSignIn, this::handleSignUp);
        signInLoginTextField.setOnAction(event -> signInPasswordField.requestFocus());
        signInPasswordField.setOnAction(event -> signInButton.fire());

        signUpLoginTextField.setOnAction(event -> signUpEmailTextField.requestFocus());
        signUpEmailTextField.setOnAction(event -> signUpPasswordField.requestFocus());
        signUpPasswordField.setOnAction(event -> signUpButton.fire());
    }

    public void setOnLoggedIn(Runnable action) {
        onLoggedIn = action;
    }


    public void signIn() {
        showProgressIndicator();
        CloudClient.getClient().authenticate(signInLoginTextField.getText(), signInPasswordField.getText());
        signInPasswordField.clear();
    }

    public void signUp() {
        showProgressIndicator();
        CloudClient.getClient().register(signUpLoginTextField.getText(), signUpEmailTextField.getText(), signUpPasswordField.getText());
        signUpPasswordField.clear();
    }

    public void handleSignIn(SignInResult result) {
        hideProgressIndicator();
        if (result == SignInResult.success) {
            onLoggedIn.run();
        } else if (result == SignInResult.bad_credentials) {
            showInformation("Authentication failed", "Bad credentials");
        } else if (result == SignInResult.user_already_logged_in) {
            showInformation("Authentication failed", "User is already logged in");
        }
    }

    public void handleSignUp(SignUpResult result) {
        hideProgressIndicator();
        if (result == SignUpResult.success)
            showInformation("Signed up successfully, please log in", "");
        else if (result == SignUpResult.email_invalid)
            showInformation("Registration failed", "Email is invalid");
        else if (result == SignUpResult.password_invalid)
            showInformation("Registration failed", "Password is not strong enough");


    }

    private void showProgressIndicator() {
        if (progressIndicatorBox == null) {
            progressIndicatorBox = new VBox();
            ProgressIndicator indicator = new ProgressIndicator();
            progressIndicatorBox.setAlignment(Pos.CENTER);
            progressIndicatorBox.getChildren().add(indicator);
        }
        tabPane.setDisable(true);
        rootPane.getChildren().add(progressIndicatorBox);
    }

    public void hideProgressIndicator() {
        tabPane.setDisable(false);
        rootPane.getChildren().remove(progressIndicatorBox);
    }


    private void showInformation(String header, String info) {
        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setHeaderText(header);
        alert.setContentText(info);
        alert.showAndWait();
    }
}
