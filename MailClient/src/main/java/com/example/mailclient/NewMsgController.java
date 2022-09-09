package com.example.mailclient;

import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.scene.Node;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.FileChooser;
import javafx.stage.Modality;
import javafx.stage.Stage;
import middleware.MailMsg;
import middleware.Message;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class NewMsgController implements Initializable {


    public static NewMsgController currentController = null;
    public VBox attachmentBox;


    public static void passDate(NewMsgController controller, Integer timeInSecs){
        controller.dateInSecs = timeInSecs;
        controller.setDate();
    }

    public static Stage stage;

    public Label lblSendingTime;

    public Integer dateInSecs;
    public TextField textNewReciver;
    public HBox reciversBox;
    public TextField textMsgTitle;
    public TextArea textMsgContent;

//    private Stage stage;
//
//    public void setStage(Stage stage) {
//        this.stage = stage;
//    }

    public void setDate(){
        int secs = (int)(new Date().getTime() / 1000);
        if(this.dateInSecs == null || this.dateInSecs < secs) {
            this.dateInSecs = secs;
        }
        lblSendingTime.setText("Message will be sent: " + new Date((long)this.dateInSecs * 1000L).toString());
    }

    private void addReciver(String reciverUsername){
        TextField textReciverUsername = new TextField();
        textReciverUsername.setText(reciverUsername);
        Button btnDeleteReciver = new Button();
        btnDeleteReciver.setText("X");
        btnDeleteReciver.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                Button clickedBtn = (Button)actionEvent.getTarget();
                HBox reciverBox = (HBox) clickedBtn.getParent();
                reciversBox.getChildren().remove(reciverBox);
            }
        });
        HBox reciverBox = new HBox();
        reciversBox.getChildren().add(reciverBox);
        reciverBox.getChildren().add(textReciverUsername);
        reciverBox.getChildren().add(btnDeleteReciver);
        textNewReciver.setText("");
    }
    public void onClickBtnAddReciver(ActionEvent actionEvent) {
        System.out.println(actionEvent.getEventType());
        String reciverUsername = textNewReciver.getText();
        if(reciverUsername.compareTo("") == 0){
            return;
        }
        addReciver(reciverUsername);


    }

    public void sendMessage(Integer isFinished){
        ArrayList<String> reciversUsernames = new ArrayList<String>(5);
        for(Node reciverNode : this.reciversBox.getChildren()){
            HBox reciverBox = (HBox) reciverNode;
            TextField textReciver = (TextField) reciverBox.getChildren().get(0);
            reciversUsernames.add(textReciver.getText());
        }

        if(reciversUsernames.isEmpty()){
            if(isFinished == 1){
                System.out.println("Add recivers");
                return;
            }
            reciversUsernames.add(ServerConnection.getServerConnection().getUser().getUsername());
        }
        Message msg = new Message();
        msg.reciverUsernames = reciversUsernames;
        msg.isFinished = isFinished;
        msg.content = textMsgContent.getText();
        msg.title = textMsgTitle.getText();
//        menjaj kasnije
        setDate();
        msg.date_and_time = this.dateInSecs;
        msg.isRead = null;
        msg.senderUsername = ServerConnection.getServerConnection().getUser().getUsername();
        if(UserPageController.currentMessage == null){
            MailMsg request = new MailMsg("sendMessage", null, msg);
            ServerConnection.getServerConnection().sendRequest(request);
            MailMsg respond = ServerConnection.getServerConnection().readRespond(true);
            if(respond.type.compareTo("sendMessage") != 0){
//            error
                return;
            }
            Integer status = (Integer) respond.data;
            if(status == 0){
                System.out.println("Poruka uspesno dostavljena");
                if (isFinished == 1){
                    UserPageController.currentController.sentHasUpdate = true;
                    UserPageController.currentController.arrangedHasUpdate = true;
                }
            }
        }else{
            msg.id = UserPageController.currentMessage.id;
            MailMsg request = new MailMsg("updateMessage", null, msg);
            ServerConnection.getServerConnection().sendRequest(request);
            MailMsg respond = ServerConnection.getServerConnection().readRespond(true);
            if(respond.type.compareTo("updateMessage") != 0){
//            error
                return;
            }
            Integer status = (Integer) respond.data;
            if(status == 0){
                System.out.println("Poruka uspesno promenjena");
                if (isFinished == 1){
                    UserPageController.currentController.sentHasUpdate = true;
                    UserPageController.currentController.arrangedHasUpdate = true;
                }
            }
            UserPageController.currentController.closeMsg(UserPageController.currentController.getSelectedTabNum());

        }

    }

    public void onClickBtnSendMsg(ActionEvent actionEvent) {
        sendMessage(1);
        NewMsgController.stage.close();
    }

    public void onClickBtnArrangeSending(ActionEvent actionEvent) {
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("datePick-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 500, 400);
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
        Stage stage = new Stage();
        stage.initModality(Modality.APPLICATION_MODAL);
//        Stage stage = HelloApplication.primaryStage;
        stage.setScene(scene);
        stage.show();
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        NewMsgController.currentController = this;
        if(UserPageController.currentMessage != null){
            textMsgTitle.setText(UserPageController.currentMessage.title);
            textMsgContent.setText(UserPageController.currentMessage.content);
            for(String reciver : UserPageController.currentMessage.reciverUsernames){
                addReciver(reciver);
            }
            dateInSecs = UserPageController.currentMessage.date_and_time;
        }

        setDate();
        NewMsgController.stage.setOnCloseRequest(event ->{
            event.consume();
            Stage stage = new Stage();
            SaveUnfinishedMsg.stage = stage;
            SaveUnfinishedMsg.parentStage = NewMsgController.stage;
            FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("saveUnfinishedMsg-view.fxml"));
            Scene scene = null;
            try {
                scene = new Scene(fxmlLoader.load(), 400, 300);
            } catch (IOException e) {
                throw new RuntimeException(e);
            }

            stage.initModality(Modality.APPLICATION_MODAL);
//        Stage stage = HelloApplication.primaryStage;
            stage.setScene(scene);
            stage.show();
        });
    }


    private void addAttachmentBox(File file){
        HBox attachBox = new HBox();
        Label lblFileName = new Label(file.getName());
        Label lblFileSize = new Label(file.length()/1024 + "kB");
        ProgressIndicator progress = new ProgressIndicator();
        progress.setProgress(0.2);
        Button btnCancleAttachment = new Button();
        FontIcon closeIcon = new FontIcon("fa-close");
        btnCancleAttachment.setGraphic(closeIcon);
        btnCancleAttachment.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {

                attachBox.getChildren().clear();
//                clear on Server side
            }
        });
        attachBox.getChildren().add(lblFileName);
        attachBox.getChildren().add(lblFileSize);
        attachBox.getChildren().add(progress);
        attachBox.getChildren().add(btnCancleAttachment);
        attachmentBox.getChildren().add(attachBox);
        Thread attachmentThread = new Thread(new AttachmentSenderThread(file, progress));
        attachmentThread.start();
    }
    public void onClickBtnAddAtachment(ActionEvent actionEvent) {
        FileChooser fileChooser = new FileChooser();
        File file = fileChooser.showOpenDialog(NewMsgController.stage);
        if(file == null){
            return;
        }
        if (file.canExecute()){
            System.out.println("Executable files can't be sent!");
            return;
        }

        addAttachmentBox(file);
    }
}
