package com.example.mailclient;

import javafx.event.ActionEvent;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Scene;
import javafx.scene.control.Label;
import javafx.scene.control.PasswordField;
import javafx.scene.control.TextField;
import javafx.stage.Stage;
import middleware.MailMsg;
import middleware.Register;
import middleware.RegisterRespond;

import java.io.IOException;
import java.net.URL;
import java.util.ResourceBundle;

public class RegisterController implements Initializable {
    public TextField username;
    public PasswordField password1;
    public PasswordField password2;
    public Label RegistrationText;
    private ServerConnection serverConnection;
    @Override



    public void initialize(URL url, ResourceBundle resourceBundle) {
        this.serverConnection = ServerConnection.getServerConnection();
        this.RegistrationText.setText("Enter username and password of your new account you wish to register");
    }


    public void onRegisterButtonClick(ActionEvent actionEvent) {
        if(password1.getText().compareTo(password2.getText()) != 0){
            RegistrationText.setText("Repeated password isn't same... Try again");
            password1.setText("");
            password2.setText("");
            return;
        }
        if(username.getText().compareTo("") == 0 || password1.getText().compareTo("") == 0){
            RegistrationText.setText("Username or password empty... Try again");
            username.setText("");
            password1.setText("");
            password2.setText("");
            return;
        }
        Register registerRequest = new Register(username.getText(), password1.getText());
        MailMsg mailMsg = new MailMsg("register", null, registerRequest);
        this.serverConnection.sendRequest(mailMsg);
        mailMsg = this.serverConnection.readRespond(true);
        if(mailMsg.type.compareTo("register") == 0){
            RegisterRespond registerRespond = (RegisterRespond) mailMsg.data;
            if(registerRespond.status == 1){
                RegistrationText.setText("Username already exists... Try again");
                password1.setText("");
                password2.setText("");
                username.setText("");
                return;
            }
            if (registerRespond.status == 0){
                RegistrationText.setText("Successfully registered");
                HelloApplication.msg = "Successfully registered, log in with your new account";
                goToLoginPage();
                return;
            }
        }
    }


    private void goToLoginPage(){
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("login-view.fxml"));
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
    public void onLoginButtonClick(ActionEvent actionEvent) {
        goToLoginPage();
    }
}
