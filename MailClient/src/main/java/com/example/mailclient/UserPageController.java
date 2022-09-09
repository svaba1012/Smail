package com.example.mailclient;

import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.Event;
import javafx.event.EventHandler;
import javafx.fxml.FXMLLoader;
import javafx.fxml.Initializable;
import javafx.geometry.Pos;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.scene.layout.VBox;
import javafx.stage.Modality;
import javafx.stage.Stage;
import middleware.MailMsg;
import middleware.Message;
import org.kordamp.ikonli.javafx.FontIcon;

import java.io.IOException;
import java.net.URL;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.ResourceBundle;

public class UserPageController implements Initializable {

    public static UserPageController currentController;
    public Button btnNewMsg;
    public ServerConnection serverConnection;
    public VBox inboxBox;
    public VBox sentBox;
    public VBox unfinishedBox;

    public Boolean inboxHasUpdate = true;
    public Boolean sentHasUpdate = true;
    public Boolean unfinishedHasUpdate = true;
    public Boolean arrangedHasUpdate = true;

    public Boolean messageView = false;


    public VBox arrangedBox;
    public Tab arrangedTab;
    public Tab unfinishedTab;
    public Tab sentTab;
    public Tab inboxTab;
    public TabPane tabPaneMenu;

    public Integer currentMsgId;

    public static Message currentMessage;

    private void setTabsGraphic(Tab tab, String text, String iconStr){
        HBox box1 = new HBox();

        Label lbl1 = new Label(text);
        FontIcon icon1 = new FontIcon(iconStr);
        box1.setMinWidth(100);
        lbl1.setMinWidth(70);
        box1.getChildren().add(lbl1);
        box1.getChildren().add(icon1);
        box1.setSpacing(10);
        tab.setGraphic(box1);
    }

    @Override
    public void initialize(URL url, ResourceBundle resourceBundle) {
        UserPageController.currentController = this;
        serverConnection = ServerConnection.getServerConnection();
        setTabsGraphic(inboxTab, "Inbox", "bi-envelope-open-fill");
        setTabsGraphic(sentTab, "Sent", "fa-send");
        setTabsGraphic(unfinishedTab, "Unfinished", "bi-pencil-fill");
        setTabsGraphic(arrangedTab, "Arranged", "bi-clock-fill");
        tabPaneMenu.setRotateGraphic(false);
    }


    private void openMessageWindow(){
        Stage stage = new Stage();
        NewMsgController.stage = stage;
        FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("newMsg-view.fxml"));
        Scene scene = null;
        try {
            scene = new Scene(fxmlLoader.load(), 800, 600);
            stage.initModality(Modality.APPLICATION_MODAL);
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            throw new RuntimeException(e);
        }
    }
    public void onNewMsgBtnClick(ActionEvent actionEvent) {
        openMessageWindow();
    }


    private ArrayList<Message> getMessages(String requestType){
        serverConnection = ServerConnection.getServerConnection();
        MailMsg request = new MailMsg(requestType, null, null);
        serverConnection.sendRequest(request);
        MailMsg respond = serverConnection.readRespond(true);
        if(respond.type.compareTo(requestType) != 0){
            System.out.println("Error getting Inbox messages " + requestType + " " + respond.type);
        }
        return (ArrayList<Message>) respond.data;
    }

    private static final int MILIS_IN_DAY = 86400000;

    private String setDate(Integer intDate){
        Long longDate = (long)intDate * 1000L;
        Date msgDateTime = new Date(longDate);
        String format = "dd/MM/yyyy HH:mm:ss";
        SimpleDateFormat formater = new SimpleDateFormat(format);
        String formatedDate = formater.format(msgDateTime);
//        if (new Date().getTime() - longDate <= MILIS_IN_DAY &&  new Date().getTime() - longDate > 0) {
//            return formatedDate.substring(11, 18);
//        }else{
//            return formatedDate.substring(0, 10);
//        }
        return formatedDate;
    }


    public void closeMsg(Integer tabNum){
        messageView = false;
        inboxTab.setDisable(false);
        sentTab.setDisable(false);
        unfinishedTab.setDisable(false);
        arrangedTab.setDisable(false);
        currentMsgId = null;
        currentMessage = null;
        selectedTab(tabNum);
    }
    private void displayMsg(VBox box, Message msg, Integer tabNum){
        messageView = true;
        inboxTab.setDisable(true);
        sentTab.setDisable(true);
        unfinishedTab.setDisable(true);
        arrangedTab.setDisable(true);
        switch (tabNum){
            case 0: inboxTab.setDisable(false);
                    break;
            case 1: sentTab.setDisable(false);
                    break;
            case 2: unfinishedTab.setDisable(false);
                    break;
            case 3: arrangedTab.setDisable(false);
                    break;
        }
        HBox optionBox = new HBox();
        box.getChildren().add(optionBox);
        box.setSpacing(20);
        FontIcon closeIcon = new FontIcon("fa-arrow-left");
        closeIcon.setIconSize(32);
        Button btnExitMsg = new Button();
        btnExitMsg.setGraphic(closeIcon);
        btnExitMsg.setOnAction(new EventHandler<ActionEvent>() {
            @Override
            public void handle(ActionEvent actionEvent) {
                closeMsg(tabNum);
                box.setSpacing(3);
            }
        });
        optionBox.getChildren().add(btnExitMsg);
        optionBox.setSpacing(15);
        if(tabNum == 2){
            Button btnFinishMsg = new Button();
            FontIcon icon = new FontIcon("fa-pencil");
            icon.setIconSize(32);
            btnFinishMsg.setGraphic(icon);
            btnFinishMsg.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
//                    prozor za novu poruku koji apdejtuje staru poruku
                    openMessageWindow();
                }
            });
            optionBox.getChildren().add(btnFinishMsg);
        }
        if (tabNum == 3){
            Button btnModifyMsg = new Button();
            FontIcon icon = new FontIcon("fa-pencil");
            icon.setIconSize(32);
            btnModifyMsg.setGraphic(icon);
            btnModifyMsg.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
//                    prozor za novu poruku koji apdejtuje staru poruku
                    openMessageWindow();
                }
            });

            optionBox.getChildren().add(btnModifyMsg);

        }
        if(tabNum >= 2){
            Button btnCancelMsg = new Button();
            FontIcon icon = new FontIcon("fa-trash");
            icon.setIconSize(32);
            btnCancelMsg.setGraphic(icon);
            btnCancelMsg.setOnAction(new EventHandler<ActionEvent>() {
                @Override
                public void handle(ActionEvent actionEvent) {
//                    prozor za otkazivanje slanja i brisanje iz baze podataka
                    Stage stage = new Stage();
                    DeleteMsgWarningController.stage = stage;
                    DeleteMsgWarningController.msgId = currentMsgId;
                    FXMLLoader fxmlLoader = new FXMLLoader(HelloApplication.class.getResource("deleteMsgWarning-view.fxml"));
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
                }
            });
            optionBox.getChildren().add(btnCancelMsg);
        }
        HBox boxSenderReciver = new HBox();
        Label lbl1 = new Label();
        Label lbl2 = new Label();
        if(tabNum == 0){
            lbl1.setText("Sender: ");
            lbl2.setText(msg.senderUsername);
        }else{
            lbl1.setText("Recivers: ");
            String allRecivers = new String();
            for (String reciver: msg.reciverUsernames) {
                allRecivers += reciver;
                allRecivers += ", ";
            }
            lbl2.setText(allRecivers);
        }
        boxSenderReciver.getChildren().add(lbl1);
        boxSenderReciver.getChildren().add(lbl2);
        box.getChildren().add(boxSenderReciver);
        HBox box1 = new HBox();
        HBox box2 = new HBox();
        Label lbl3 = new Label("Title: ");
        Label lbl4 = new Label("Content: ");
        Label lblTitle = new Label(msg.title);
        Label lblContent = new Label(msg.content);
        box1.getChildren().add(lbl3);
        box1.getChildren().add(lblTitle);
        box2.getChildren().add(lbl4);
        box2.getChildren().add(lblContent);
        box.getChildren().add(box1);
        box.getChildren().add(box2);
        lbl1.setMinWidth(100);
        lbl3.setMinWidth(100);
        lbl4.setMinWidth(100);
    }

    private void fillMessageBox(VBox box, ArrayList<Message> messages,  Integer tabNum){

        for(int i = 0; i < messages.size(); i++){
            Message msg = messages.get(i);
            HBox messageBox = new HBox();
            messageBox.setMinHeight(30);
            messageBox.setSpacing(10);
            messageBox.setAlignment(Pos.CENTER_LEFT);
            messageBox.getStyleClass().add("msgBox");


            messageBox.setOnMouseClicked(new EventHandler<javafx.scene.input.MouseEvent>() {
                @Override
                public void handle(javafx.scene.input.MouseEvent mouseEvent) {
                    Label lbl = (Label)messageBox.getChildren().get(4);
                    Label dateLbl = (Label)messageBox.getChildren().get(5);

                    currentMsgId = Integer.parseInt(lbl.getText());
                    currentMessage = new Message();
                    currentMessage.id = currentMsgId;
                    currentMessage.title = ((Label)messageBox.getChildren().get(1)).getText();
                    currentMessage.content = ((Label)messageBox.getChildren().get(2)).getText();
                    currentMessage.date_and_time =Integer.parseInt(dateLbl.getText());
                    currentMessage.isFinished = 0;
                    if(tabNum != 0){
                        ComboBox<Label> reciversComboBox = (ComboBox<Label>) messageBox.getChildren().get(0);
                        ObservableList<Label> reciversLabels = reciversComboBox.getItems();
                        currentMessage.reciverUsernames = new ArrayList<String>();
                        for(Label lbl1 : reciversLabels){
                            currentMessage.reciverUsernames.add(lbl1.getText());
                        }
                    }


                    box.getChildren().clear();
                    displayMsg(box, msg, tabNum);
                }
            });
            box.getChildren().add(messageBox);
            Label title = new Label(msg.title);

            title.setMaxWidth(300);
            title.setMinWidth(300);
            String contentStr = msg.content;

            if(msg.content.length() > 80){
                contentStr = msg.content.substring(0,79) + "...";
            }
            Label content = new Label(contentStr);
            content.setMaxWidth(500);
            content.setMinWidth(500);
            Label date = new Label(setDate(msg.date_and_time));

            if(tabNum == 0){
                Label senderUsername = new Label(msg.senderUsername);
                messageBox.getChildren().add(senderUsername);
                senderUsername.setMinWidth(100);
                senderUsername.setMaxWidth(100);
            }else{
                ComboBox<Label> reciversUsernames = new ComboBox<Label>();
                for(String reciver : msg.reciverUsernames){
                    reciversUsernames.getItems().add(new Label(reciver));

                }
//                auto select first build later also disable selection
                reciversUsernames.getSelectionModel().select(1);
                messageBox.getChildren().add(reciversUsernames);

            }
            Label lblId = new Label(msg.id+"");
            lblId.setVisible(false);
            Label lblIntDateAndTime = new Label(msg.date_and_time + "");
            lblIntDateAndTime.setVisible(false);

            messageBox.getChildren().add(title);
            messageBox.getChildren().add(content);
            messageBox.getChildren().add(date);
            messageBox.getChildren().add(lblId);
            messageBox.getChildren().add(lblIntDateAndTime);
        }
    }

    public Integer getSelectedTabNum(){
        if(inboxTab.isSelected()){
            return 0;
        } else if (sentTab.isSelected()) {
            return 1;
        } else if (unfinishedTab.isSelected()) {
            return 2;
        } else if (arrangedTab.isSelected()) {
            return 3;
        }else{
            return null;
        }
    }


    private void selectedTab(Integer tabNum){
        VBox box = null;
        String msgReq = null;
        messageView = false;

        switch (tabNum){
            case 0: box = inboxBox;
                    msgReq = "getRecivedMessages";

                    break;
            case 1: box = sentBox;
                    msgReq = "getSendedMessages";
                    break;
            case 2: box = unfinishedBox;
                    msgReq = "getUnfinshedMessages";
                    break;
            case 3: box = arrangedBox;
                    msgReq = "getArrangedMessages";
                    break;
        }
        ArrayList<Message> messages = getMessages(msgReq);
        if (messages == null || messages.isEmpty()){
            return;
        }
        box.getChildren().clear();
        fillMessageBox(box, messages, tabNum);

    }

    public void onSelectedInboxTab(Event event) {
        if(!inboxHasUpdate && !messageView){
            return;
        }
        Tab inboxTab = (Tab) event.getSource();

        if(!inboxTab.isSelected()){
            return;
        }
        selectedTab(0);
        inboxHasUpdate = false;
    }



    public void onSelectedSentTab(Event event) {
        if(!sentHasUpdate && !messageView){
            return;
        }

        Tab sentTab = (Tab) event.getSource();
        if(!sentTab.isSelected()){
            return;
        }

        selectedTab(1);
        sentHasUpdate = false;
    }

    public void onSelectedUnfinishedTab(Event event) {
        if(!unfinishedHasUpdate && !messageView){
            return;
        }
        Tab unfinishedTab = (Tab) event.getSource();
        if(!unfinishedTab.isSelected()){
            return;
        }
        selectedTab(2);
        unfinishedHasUpdate = false;
    }

    public void onSelectedArrangedTab(Event event) {
        if(!arrangedHasUpdate && !messageView){
            return;
        }

        Tab sentTab = (Tab) event.getSource();
        if(!sentTab.isSelected()){
            return;
        }

        selectedTab(3);
        arrangedHasUpdate = false;
    }
}
