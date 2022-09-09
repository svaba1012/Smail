package com.example.mailclient;

import javafx.event.ActionEvent;
import javafx.scene.control.Button;
import javafx.stage.Stage;
import middleware.MailMsg;

public class DeleteMsgWarningController {
    public Button btnCancelDelete;
    public Button btnDeleteMsg;

    public static Stage stage;

    public static Integer msgId;
    
    public void onClickBtnCancelDelete(ActionEvent actionEvent) {
        stage.close();
    }

    public void onClickBtnDeleteMsg(ActionEvent actionEvent) {
        ServerConnection serverConnection = ServerConnection.getServerConnection();
        MailMsg mailMsg = new MailMsg("deleteMessage", null, msgId);
        serverConnection.sendRequest(mailMsg);
        serverConnection.readRespond(true);
        UserPageController.currentController.unfinishedHasUpdate = true;
        UserPageController.currentController.arrangedHasUpdate = true;
        Integer currentTabNum = UserPageController.currentController.getSelectedTabNum();
        UserPageController.currentController.closeMsg(currentTabNum);
        stage.close();
    }
}
