package com.example.mailclient;

import javafx.event.ActionEvent;
import javafx.stage.Stage;

public class SaveUnfinishedMsg {

    public static Stage parentStage;
    public static Stage stage;


    private void close(){
        parentStage.close();
        stage.close();
    }
    public void onClickBtnCancel(ActionEvent actionEvent) {
        close();
    }

    public void onClickBtnSaveMsg(ActionEvent actionEvent) {
        NewMsgController.currentController.sendMessage(0);
        UserPageController.currentController.unfinishedHasUpdate = true;
        close();
    }
}
