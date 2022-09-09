package com.example.mailclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import middleware.Login;
import middleware.MailMsg;
import middleware.User;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class LoginController implements Initializable {
    @FXML
    private Label welcomeText;
    @FXML
    private TextField username;
    @FXML
    private PasswordField password;


    private ServerConnection serverConnection;

    public LoginController() {
    }


    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.serverConnection = ServerConnection.getServerConnection();
        this.serverConnection.openConnection();
        this.welcomeText.setText(HelloApplication.msg);
    }

    @FXML
    protected void onLoginButtonClick() {

        Login login = new Login();


        login.username = username.getText();
        login.password = password.getText();
        MailMsg mailMsg = new MailMsg("login", null, login);
        this.serverConnection.sendRequest(mailMsg);
        mailMsg = this.serverConnection.readRespond(true);
        if(mailMsg.type.compareTo("login") == 0){
            User user =(User) mailMsg.data;

            if(user != null){
                welcomeText.setText("Uspesno ulogovan");
                ServerConnection.getServerConnection().setUser(user);
                userLoggedIn();
            }else{
                welcomeText.setText("Netacna sifra");
            }
        }
    }

    public void userLoggedIn(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("userPage-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 800, 600);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = HelloApplication.primaryStage;
        stage.setScene(scene);
        stage.show();
    }

    public void onRegisterButtonClick(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("register-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 800, 600);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = HelloApplication.primaryStage;
        stage.setScene(scene);
        stage.show();
    }
}