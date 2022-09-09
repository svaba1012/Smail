import com.google.gson.Gson;
import middleware.*;
import model.DataSource;

import java.io.*;
import java.net.Socket;
import java.util.ArrayList;

public class MailServiceThread implements Runnable{
    public Socket socket;
    public BufferedReader requestReader;
    public PrintWriter respondWriter;

    public ObjectInputStream objectReader;
    public ObjectOutputStream objectWritter;

    public DataSource dataSource;

    public Gson gson;

    private User user;

    public MailServiceThread(Socket socket) {
        this.socket = socket;
        try {
            requestReader = new BufferedReader(new InputStreamReader(this.socket.getInputStream()));
            respondWriter = new PrintWriter(this.socket.getOutputStream(), true);
            objectWritter = new ObjectOutputStream(this.socket.getOutputStream());
            objectReader = new ObjectInputStream(this.socket.getInputStream());
        } catch (IOException e) {
            System.out.println("Error opening reader or writer" + e.getMessage());
        }
        this.dataSource = new DataSource();
        this.dataSource.openConnection();
    }


    private MailMsg loginRequestProcess(MailMsg req){
        Login login = (Login) req.data;
        this.user = dataSource.loginUser(login.username, login.password);
        MailMsg mailMsg = new MailMsg("login", null, user);
        return mailMsg;
    }

//    private String loginRequestProcess(String json){
//        Login login = this.gson.fromJson(json, Login.class);
//        User user = dataSource.loginUser(login.username, login.password);
//        MailMsg mailMsg = new MailMsg("login", gson.toJson(user), null);
//        return gson.toJson(mailMsg);
//    }

//    private String registerRequestProcess(String json){
//        return null;
//    }

    private MailMsg registerRequestProcess(MailMsg req){
        Register registerRequest = (Register) req.data;
        String msg = dataSource.registerUser(registerRequest.username, registerRequest.password, registerRequest.password);
        RegisterRespond registerRespond = new RegisterRespond();
        if(msg.compareTo("Username already exists, try another one!") == 0){
            registerRespond.status = 1;
            registerRespond.password = null;
            registerRespond.username = null;
        } else if (msg.compareTo("Succesfully registered!") == 0) {
            registerRespond.status = 0;
            registerRespond.username = registerRequest.username;
            registerRespond.password = registerRequest.password;
        }
        MailMsg mailMsg = new MailMsg("register", null, registerRespond);
        return mailMsg;
    }

    private MailMsg getRecivedMessagesRequestProcess(MailMsg req){

        ArrayList<Message> messages = this.dataSource.getRecivedMsgs(this.user);
        MailMsg respond = new MailMsg(req.type, null,messages);
        return respond;
    }
    private MailMsg getUnfinishedMessagesRequestProcess(MailMsg req){

        ArrayList<Message> messages = this.dataSource.getUnfinishedMsgs(this.user);
        MailMsg respond = new MailMsg(req.type, null,messages);
        return respond;
    }
    private MailMsg getSendedMessagesRequestProcess(MailMsg req) {

        ArrayList<Message> messages = this.dataSource.getSendedMsgs(this.user);
        MailMsg respond = new MailMsg(req.type, null,messages);
        return respond;
    }

    private MailMsg sendMessageRequestProcess(MailMsg req) {
        Message message = (Message) req.data;
        Integer status = this.dataSource.newMessage(this.user.getId(), message.title,
            message.content, message.reciverUsernames, message.isFinished, message.date_and_time);
        MailMsg respond = new MailMsg(req.type, null, status);
        return respond;
    }

    private MailMsg getArrangedMessagesRequestProcess(MailMsg req) {
        ArrayList<Message> messages = this.dataSource.getArrangedMsgs(this.user);
        MailMsg respond = new MailMsg(req.type, null,messages);
        return respond;
    }

    private  MailMsg deleteMessageRequestProcess(MailMsg req){
        Integer msgId = (Integer) req.data;
        Integer status = this.dataSource.deleteMsg(msgId);
        MailMsg respond = new MailMsg(req.type, null, status);
        return respond;
    }

    private MailMsg updateMessageRequestProcess(MailMsg req){
        Message message = (Message) req.data;
        Integer status = this.dataSource.updateMessage(message.id, message.title, message.content,
                message.reciverUsernames, message.isFinished, message.date_and_time);
        MailMsg respond = new MailMsg(req.type, null, status);
        return respond;
    }

    private MailMsg sendAttachmentRequestProcess(MailMsg req){
//        not implemented yet
        return null;
    }

    private void requestProcess(MailMsg req){
        MailMsg respond = null;
//        System.out.println(req.type);
        switch (req.type){
            case "login":                   respond = loginRequestProcess(req);
                                            break;
            case "register":                respond = registerRequestProcess(req);
                                            break; 
            case "sendMessage":             respond = sendMessageRequestProcess(req);
                                            break;
            case "getSendedMessages":       respond = getSendedMessagesRequestProcess(req);
                                            break;
            case "getRecivedMessages":      respond = getRecivedMessagesRequestProcess(req);
                                            break;
            case "getUnfinshedMessages":    respond = getUnfinishedMessagesRequestProcess(req);
                                            break;
            case "getArrangedMessages":     respond = getArrangedMessagesRequestProcess(req);
                                            break;
            case "deleteMessage":           respond = deleteMessageRequestProcess(req);
                                            break;
            case "updateMessage":           respond = updateMessageRequestProcess(req);
                                            break;
            case "sendAttachment":          //implement later
                                            respond = sendAttachmentRequestProcess(req);
                                            break;
        }
        try {
            objectWritter.writeObject(respond);
        } catch (IOException e) {
            System.out.println("Error sending object" + e.getMessage());
        }
    }




    @Override
    public void run() {
        this.gson = new Gson();
        System.out.println("User connected");
        while(true){
            try {
                MailMsg emailMsg = (MailMsg) objectReader.readObject();
                requestProcess(emailMsg);
//                String jsonEmailMsg = requestReader.readLine();
//                middleware.MailMsg mailMsg = gson.fromJson(jsonEmailMsg, middleware.MailMsg.class);
//                requestProcess(mailMsg);
            } catch (IOException e) {
                System.out.println("Error reading request from client " + e.getMessage());
                dataSource.disconectUser(this.user.getUsername());
                try {
                    this.socket.close();
                } catch (IOException ex) {
                    throw new RuntimeException(ex);
                }
                return;
            } catch (ClassNotFoundException e1) {
                System.out.println("Error reading request from client, class not found " + e1.getMessage());
            }
        }
    }
}
