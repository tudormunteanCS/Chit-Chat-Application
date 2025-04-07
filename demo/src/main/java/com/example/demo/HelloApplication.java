package com.example.demo;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {
    @Override
    public void start(Stage stage) throws IOException {
        FXMLLoader fxmlLoaderLoginSignup = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene sceneLogin = new Scene(fxmlLoaderLoginSignup.load(),1100,615); // 600, 500
        LoginController loginController = fxmlLoaderLoginSignup.getController();
        loginController.setStage(stage);
        stage.setTitle("Login");
        stage.setScene(sceneLogin);
        stage.show();

    }

    public static void main(String[] args) {
        launch();
    }
}