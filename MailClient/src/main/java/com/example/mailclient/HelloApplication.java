package com.example.mailclient;

import javafx.application.Application;
import javafx.fxml.FXMLLoader;
import javafx.scene.Scene;
import javafx.stage.Stage;

import java.io.IOException;

public class HelloApplication extends Application {

    public static Stage primaryStage;

    public static String msg;
    @Override
    public void start(Stage stage) throws IOException {
//        ServerConnection.getServerConnection().openConnection();
        primaryStage = stage;
        msg = "Welcome to SMail, best mail ever.\n Login to get started.";
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
        Scene scene = new Scene(fxmlLoader.load(), 800, 600);
        stage.setTitle("Mail");
        stage.setScene(scene);
        stage.setMaximized(true);
        stage.show();
    }


    public static void main(String[] args) {
        launch();
    }
}