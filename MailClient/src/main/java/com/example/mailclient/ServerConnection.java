package com.example.mailclient;

import middleware.MailMsg;
import middleware.User;

import java.io.*;
import java.net.Socket;

public class ServerConnection {

    private static ServerConnection serverConnection = null;

    private ServerConnection() {
    }

    public Socket socket;
    private BufferedReader respondReader;
    private PrintWriter requestWritter;

    private ObjectInputStream objectReader;
    private  ObjectOutputStream objectWritter;

    private User user;

    public static ServerConnection getServerConnection(){
        if(serverConnection == null){
            serverConnection = new ServerConnection();
        }
        return serverConnection;
    }

    public void openConnection(){
        try {
            this.socket = new Socket("localhost", 5001);
            respondReader = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            requestWritter = new PrintWriter(socket.getOutputStream(), true);

            objectWritter = new ObjectOutputStream(this.socket.getOutputStream());
            objectReader = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error connecting to server " + e.getMessage());
        }
    }


    public void sendRequest(String request){
        requestWritter.println(request);
    }

    public void sendRequest(MailMsg request){
        try {
            objectWritter.writeObject(request);
        } catch (IOException e) {
            System.out.println("Error sending object" + e.getMessage());
        }
    }

    public MailMsg readRespond(boolean value){
        MailMsg obj = null;
        try {
            obj = (MailMsg) objectReader.readObject();
        } catch (IOException e) {
            System.out.println("Error reading object" + e.getMessage());
        } catch (ClassNotFoundException e1) {
            System.out.println("Error reading object" + e1.getMessage());
        }
        return obj;
    }

    public String readRespond(){
        try {
            String respond = respondReader.readLine();
            return respond;
        } catch (IOException e) {
            System.out.println("Error reading respond " + e.getMessage());
            return null;
        }
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }
}
