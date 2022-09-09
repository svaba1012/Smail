package com.example.mailclient;

import javafx.application.Platform;
import javafx.scene.control.ProgressIndicator;
import middleware.MailMsg;

import java.io.File;

public class AttachmentSenderThread implements Runnable{

    public File file;

    public ProgressIndicator progressIndicator;


    public AttachmentSenderThread(File file, ProgressIndicator progressIndicator) {
        this.file = file;
        this.progressIndicator = progressIndicator;
    }

    @Override
    public void run() {
        MailMsg mailMsg = new MailMsg("sendAttachment", null, file);
        Double prog = 0.2;
        while(prog < 1.0) {
            try {
                Thread.sleep(1000);
                Double finalProg = prog;
                Platform.runLater(new Runnable() {
                    @Override
                    public void run() {
                        progressIndicator.setProgress(finalProg + 0.1);
                    }
                });
                prog += 0.1;
            } catch (InterruptedException e) {
                System.out.println("Error sleeping " + e.getMessage());
            }
        }
//        not implemented yet
//        ServerConnection.getServerConnection().sendRequest(mailMsg);
//        ServerConnection.getServerConnection().readRespond(true);
    }
}
