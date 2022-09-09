package model;

import middleware.Message;
import middleware.User;

import java.sql.*;
import java.util.ArrayList;
import java.util.Date;

public class DataSource {

    private Connection conn;
    private PreparedStatement loginStatement;
    private PreparedStatement registerStatement;

    private PreparedStatement setLoggedStatusStatement;
    private PreparedStatement newMsgStatement;
    private PreparedStatement newReciverStatement;
    private PreparedStatement checkRecivers;
    private PreparedStatement sendedMsgsStatement;
    private PreparedStatement recivedMsgsStatement;
    private PreparedStatement reciversOfMsgStatement;
    private PreparedStatement arrangedMsgsStatement;
    private PreparedStatement deleteMsgStatement;
    private PreparedStatement deleteReciversOfMsg;

    private PreparedStatement updateMessageStatement;

    public static final String DATABASE_TYPE = "jdbc:sqlite:";
    public static final String DATABASE_PATH = "./mail.db";
    public static final String DATABASE_FULL_PATH = DATABASE_TYPE+DATABASE_PATH;

    public static final String TABLE_USERS = "users";
    public static final String USERS_COLUMN_ID = "id";
    public static final String USERS_COLUMN_USERNAME = "username";
    public static final String USERS_COLUMN_PASSWORD = "password";
    public static final String USERS_COLUMN_ISLOGGED = "isLogged";


    public static final String TABLE_ATTACHMENTS = "attachments";
    public static final String ATTACHMENTS_COLUMN_ID = "id";
    public static final String ATTACHMENTS_COLUMN_FILE = "file";
    public static final String ATTACHMENTS_COLUMN_MSG_ID = "id_message";

    public static final String TABLE_MSGS = "messages";
    public static final String MSGS_COLUMN_ID = "id";
    public static final String MSGS_COLUMN_TITLE = "title";
    public static final String MSGS_COLUMN_CONTENT = "content";
    public static final String MSGS_COLUMN_DATE_TIME = "date_and_time";
    public static final String MSGS_COLUMN_SENDER_ID = "id_sender";
    public static final String MSGS_COLUMN_IS_FINISHED = "is_finished";

    public static final String TABLE_MSGS_RECIVERS = "msgsRecivers";
    public static final String MSGS_RECIVERS_COLUMN_ID = "id";
    public static final String MSGS_RECIVERS_COLUMN_MSG_ID = "id_message";
    public static final String MSGS_RECIVERS_COLUMN_RECIVER_ID = "id_reciver";
    public static final String MSGS_RECIVERS_COLUMN_IS_READ = "is_read";




    public static final String QUERY_CHECK_USER = "SELECT " + USERS_COLUMN_ID + " FROM " + TABLE_USERS + " WHERE ";
    public static final String END_OF_CHECK_RECIVERS = TABLE_USERS + "." + USERS_COLUMN_USERNAME + "=? OR ";
    public static final String QUERY_LOGIN =  "SELECT * FROM " + TABLE_USERS + " WHERE " + TABLE_USERS +
                                                "." + USERS_COLUMN_USERNAME + "=?";
    public static final String QUERY_REGISTER = "INSERT INTO " + TABLE_USERS + "(" + USERS_COLUMN_USERNAME + ", " +
                                                USERS_COLUMN_PASSWORD + ") VALUES (?, ?)";

    public static final String QUERY_SET_LOGGED = "UPDATE " + TABLE_USERS +
            " SET " + USERS_COLUMN_ISLOGGED + "=? " +
            " WHERE " + USERS_COLUMN_USERNAME + "=?";

    public static final String QUERY_NEW_MESSAGE = "INSERT INTO " + TABLE_MSGS + "(" +
         MSGS_COLUMN_TITLE + ", " + MSGS_COLUMN_CONTENT + ", "  + MSGS_COLUMN_DATE_TIME + ", " +
        MSGS_COLUMN_SENDER_ID + ", " + MSGS_COLUMN_IS_FINISHED + ") VALUES (?, ?, ?, ?, ?)";

    public static final String QUERY_NEW_RECIVER = "INSERT INTO " + TABLE_MSGS_RECIVERS + " (" +
         MSGS_RECIVERS_COLUMN_MSG_ID + "," + MSGS_RECIVERS_COLUMN_RECIVER_ID +
            ") VALUES (?, ?)";

    public static final String QUERY_GET_SENDED_MSGS = "SELECT " + MSGS_COLUMN_ID + ", " + MSGS_COLUMN_TITLE + ", " +
            MSGS_COLUMN_CONTENT + ", " + MSGS_COLUMN_DATE_TIME + " FROM " + TABLE_MSGS + " WHERE " + MSGS_COLUMN_SENDER_ID
            + "=? AND " + MSGS_COLUMN_IS_FINISHED + "=? AND " + MSGS_COLUMN_DATE_TIME + "<?";

    public static final String QUERY_GET_ARRANGED_MSGS = "SELECT " + MSGS_COLUMN_ID + ", " + MSGS_COLUMN_TITLE + ", " +
            MSGS_COLUMN_CONTENT + ", " + MSGS_COLUMN_DATE_TIME + " FROM " + TABLE_MSGS + " WHERE " + MSGS_COLUMN_SENDER_ID
            + "=? AND " + MSGS_COLUMN_IS_FINISHED + "=? AND " + MSGS_COLUMN_DATE_TIME + ">?";

    public static final String QUERY_GET_RECIVED_MSGS = "SELECT " + MSGS_COLUMN_TITLE + ", " +
            MSGS_COLUMN_CONTENT + ", " + MSGS_COLUMN_DATE_TIME + ", " + TABLE_MSGS_RECIVERS + "." +
            MSGS_RECIVERS_COLUMN_IS_READ + ", " + TABLE_USERS + "." + USERS_COLUMN_USERNAME + ", " +
            TABLE_MSGS + "." + MSGS_COLUMN_ID +
            " FROM " + TABLE_MSGS + " INNER JOIN  " +
            TABLE_MSGS_RECIVERS + " ON " + TABLE_MSGS_RECIVERS +"." + MSGS_RECIVERS_COLUMN_MSG_ID +
            "=" +  TABLE_MSGS + "." + MSGS_COLUMN_ID + " INNER JOIN " + TABLE_USERS + " ON " +
            TABLE_USERS + "." + USERS_COLUMN_ID + "=" + TABLE_MSGS + "." + MSGS_COLUMN_SENDER_ID +
            " WHERE "  + TABLE_MSGS_RECIVERS + "." + MSGS_RECIVERS_COLUMN_RECIVER_ID + "=? AND " +
            TABLE_MSGS + "." + MSGS_COLUMN_IS_FINISHED + "=1 AND " + TABLE_MSGS + "." +
            MSGS_COLUMN_DATE_TIME + "<?";

    public static final String QUERY_GET_RECIVERS_OF_MESSAGE = "SELECT " + USERS_COLUMN_USERNAME + " FROM " +
            TABLE_USERS + " INNER JOIN " + TABLE_MSGS_RECIVERS + " ON " + TABLE_USERS + "." + USERS_COLUMN_ID +
            "=" + TABLE_MSGS_RECIVERS + "." + MSGS_RECIVERS_COLUMN_RECIVER_ID + " WHERE " + TABLE_MSGS_RECIVERS +
            "." + MSGS_RECIVERS_COLUMN_MSG_ID  + "=?";



    public static final String QUERY_DELETE_MESSAGE = "DELETE FROM " + TABLE_MSGS + " WHERE " + MSGS_COLUMN_ID + "=?";
    public static final String QUERY_DELETE_RECIVERS_OF_MESSAGE = "DELETE FROM " + TABLE_MSGS_RECIVERS +
             " WHERE " + MSGS_RECIVERS_COLUMN_MSG_ID + "=?";

    public static final String QUERY_UPDATE_MESSAGE = "UPDATE " +  TABLE_MSGS + " SET " + MSGS_COLUMN_TITLE + "=? , " +
             MSGS_COLUMN_CONTENT + "=? , " + MSGS_COLUMN_DATE_TIME + "=? , " + MSGS_COLUMN_IS_FINISHED + "=? WHERE " +
            MSGS_COLUMN_ID + "=?";

    public void openConnection(){
        System.out.println(QUERY_GET_RECIVED_MSGS);
        try {
            this.conn = DriverManager.getConnection(DATABASE_FULL_PATH);
            loginStatement = conn.prepareStatement(QUERY_LOGIN);
            registerStatement = conn.prepareStatement(QUERY_REGISTER);
            setLoggedStatusStatement = conn.prepareStatement(QUERY_SET_LOGGED);
            newMsgStatement = conn.prepareStatement(QUERY_NEW_MESSAGE);
            newReciverStatement = conn.prepareStatement(QUERY_NEW_RECIVER);
            sendedMsgsStatement = conn.prepareStatement(QUERY_GET_SENDED_MSGS);
            recivedMsgsStatement = conn.prepareStatement(QUERY_GET_RECIVED_MSGS);
            reciversOfMsgStatement = conn.prepareStatement(QUERY_GET_RECIVERS_OF_MESSAGE);
            arrangedMsgsStatement = conn.prepareStatement(QUERY_GET_ARRANGED_MSGS);
            deleteMsgStatement = conn.prepareStatement(QUERY_DELETE_MESSAGE);
            deleteReciversOfMsg = conn.prepareStatement(QUERY_DELETE_RECIVERS_OF_MESSAGE);
            System.out.println(QUERY_UPDATE_MESSAGE);
            updateMessageStatement = conn.prepareStatement(QUERY_UPDATE_MESSAGE);

        } catch (SQLException e) {
            System.out.println("Error connecting to the base" + e.getMessage());
        }
    }

    public User loginUser(String username, String password){
        try {
            loginStatement.setString(1, username);
            ResultSet result = loginStatement.executeQuery();
            User newUser = new User();
            if(result == null){
                return  null;
            }
            while(result.next()){
                newUser.setId(result.getInt(1));
                newUser.setUsername(result.getString(2));
                newUser.setPassword(result.getString(3));
            }
            System.out.println(username.equals(newUser.getUsername()) && password.equals(newUser.getPassword()));
            if (username.equals(newUser.getUsername()) && password.equals(newUser.getPassword())){
                System.out.println("Izvrseno");
                setLoggedStatusStatement.setInt(1, 1);
                setLoggedStatusStatement.setString(2, newUser.getUsername());
                setLoggedStatusStatement.executeUpdate();
                return newUser;
            }
        } catch (SQLException e) {
            System.out.println("Error executing login query" + e.getMessage());
        }
        return null;
    }

    public void disconectUser(String username){
        try {
            setLoggedStatusStatement.setInt(1, 0);
            setLoggedStatusStatement.setString(2, username);
            setLoggedStatusStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error disconnecting user " + e.getMessage());
        }

    }


    private ArrayList<Integer> checkExistingRecivers(ArrayList<String> reciverUsernames) {
        ArrayList<Integer> validReciversIds = new ArrayList<Integer>(reciverUsernames.size());
        String queryExistingRecivers = QUERY_CHECK_USER;
        for(int i = 0; i < reciverUsernames.size(); i++){
            queryExistingRecivers += END_OF_CHECK_RECIVERS;
        }
        queryExistingRecivers += "0";
        System.out.println(queryExistingRecivers);
        try {
            this.checkRecivers = conn.prepareStatement(queryExistingRecivers);
            for(int i = 0; i < reciverUsernames.size(); i++){
                this.checkRecivers.setString(i + 1, reciverUsernames.get(i));
            }
            ResultSet results = this.checkRecivers.executeQuery();
            while (results.next()){
                validReciversIds.add(results.getInt(1));
            }
        } catch (SQLException e) {
            System.out.println("Error quering to check recivers" + e.getMessage());
        }
        return validReciversIds;
    }



    public int updateMessage(int msgId, String title, String content, ArrayList<String> reciverUsernames, int isFinished, int dateAndTime){
        ArrayList<Integer> validRecivers = checkExistingRecivers(reciverUsernames);
        if(validRecivers.isEmpty()){
//            return error code of no valid reciver (1)
            return 1;
        }
        try {
            this.updateMessageStatement.setString(1, title);
            this.updateMessageStatement.setString(2, content);
//        datum i proracun odlozenog vremena
            this.updateMessageStatement.setInt(3, dateAndTime);
            this.updateMessageStatement.setInt(4, isFinished);
            this.updateMessageStatement.setInt(5, msgId);
            System.out.println(newMsgStatement.toString());
            this.updateMessageStatement.executeUpdate();
            ResultSet result = newMsgStatement.getGeneratedKeys();
            this.deleteReciversOfMsg.setInt(1, msgId);
            this.deleteReciversOfMsg.executeUpdate();

            for(Integer reciverId : validRecivers){
                this.newReciverStatement = conn.prepareStatement(QUERY_NEW_RECIVER);
                this.newReciverStatement.setInt(1, msgId);
                this.newReciverStatement.setInt(2, reciverId);
                this.newReciverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error updating message" + e.getMessage());
//            return error query (3)
            return 2;
        }
        return 0;
    }
    public int newMessage(int senderId, String title, String content, ArrayList<String> reciverUsernames, int isFinished, int dateAndTime){
        ArrayList<Integer> validRecivers = checkExistingRecivers(reciverUsernames);
        if(validRecivers.isEmpty()){
//            return error code of no valid reciver (1)
            return 1;
        }
        try {
            this.newMsgStatement.setString(1, title);
            this.newMsgStatement.setString(2, content);
//        datum i proracun odlozenog vremena
            this.newMsgStatement.setInt(3, dateAndTime);
            this.newMsgStatement.setInt(4, senderId);
            this.newMsgStatement.setInt(5, isFinished);
            System.out.println(newMsgStatement.toString());
            this.newMsgStatement.executeUpdate();
            ResultSet result = newMsgStatement.getGeneratedKeys();
            Integer msgId = null;
            if(result.next()){
                msgId = result.getInt(1);
            }else{
                System.out.println("Error getting id of message");
//                return error code for getting id of msg (2)
                return 2;
            }
            for(Integer reciverId : validRecivers){
                this.newReciverStatement = conn.prepareStatement(QUERY_NEW_RECIVER);
                this.newReciverStatement.setInt(1, msgId);
                this.newReciverStatement.setInt(2, reciverId);
                this.newReciverStatement.executeUpdate();
            }
        } catch (SQLException e) {
            System.out.println("Error sending message" + e.getMessage());
//            return error query (3)
            return 3;
        }
        return 0;
    }

    private ArrayList<String> getReciversOfMessage(Integer msgId){
        ArrayList<String> reciversUsernames = new ArrayList<String>(5);
        try {
            reciversOfMsgStatement.setInt(1, msgId);
            ResultSet result = reciversOfMsgStatement.executeQuery();
            while (result.next()){
                reciversUsernames.add(result.getString(1));
            }
        } catch (SQLException e) {
            System.out.println("Error querying for recivers of message" + e.getMessage());
        }
        return reciversUsernames;
    }
    private ArrayList<Message> getSendedMsgsPrivate(User user, Integer isFinished, PreparedStatement statement){
        Integer userId = user.getId();
        ArrayList<Message> messages = new ArrayList<Message>(10);
        try {
            statement.setInt(1, userId);
            statement.setInt(2, isFinished);
            statement.setInt(3, (int)(new Date().getTime() / 1000));

            ResultSet result = statement.executeQuery();
            while(result.next()){
                Message msg = new Message();
                msg.id = result.getInt(1);
                msg.title = result.getString(2);
                msg.content = result.getString(3);
                msg.date_and_time = result.getInt(4);
                msg.isFinished = isFinished;
//                System.out.println("Ima");
                msg.reciverUsernames = getReciversOfMessage(result.getInt(1));
                msg.senderUsername = user.getUsername();
                messages.add(msg);
            }
        } catch (SQLException e) {
            System.out.println("Error querying for sended msgs " + e.getMessage());
        }
        return messages;
    }

    public ArrayList<Message> getSendedMsgs(User user){
        return getSendedMsgsPrivate(user, 1, sendedMsgsStatement);
    }

    public ArrayList<Message> getUnfinishedMsgs(User user){
        return getSendedMsgsPrivate(user, 0, sendedMsgsStatement);
    }

    public ArrayList<Message> getArrangedMsgs(User user){
        return getSendedMsgsPrivate(user, 1, arrangedMsgsStatement);
    }

    public ArrayList<Message> getRecivedMsgs(User user){
        ArrayList<Message> messages = new ArrayList<Message>(10);
        Integer userId = user.getId();

        try {
            this.recivedMsgsStatement.setInt(1, userId);
            this.recivedMsgsStatement.setInt(2, (int)(new Date().getTime() / 1000));
            ResultSet result = this.recivedMsgsStatement.executeQuery();

            while(result.next()){
                Message msg = new Message();
                msg.title = result.getString(1);
                msg.content = result.getString(2);
                msg.date_and_time = result.getInt(3);
                msg.isFinished = 1;
                msg.reciverUsernames = null;
                msg.senderUsername = result.getString(5);
                msg.id = result.getInt(6);
                messages.add(msg);
            }

        } catch (SQLException e) {
            System.out.println("Error qurying for recived msgs" + e.getMessage());
        }
        return messages;
    }

    public Integer deleteMsg(Integer msgId){
        try {
            deleteReciversOfMsg.setInt(1, msgId);
            deleteMsgStatement.setInt(1, msgId);
            deleteReciversOfMsg.executeUpdate();
            deleteMsgStatement.executeUpdate();
        } catch (SQLException e) {
            System.out.println("Error delete msg from db" + e.getMessage());
            return 1;
        }
        return 0;
    }

    public String registerUser(String username, String pass1, String pass2){
        String msg = "Error";

        if(!pass1.equals(pass2)){
            msg = "Repeated password isn't same!!!";
            return msg;
        }
        try {
            loginStatement.setString(1, username);
            ResultSet result = loginStatement.executeQuery();
            User newUser = new User();
            if(result.next()){
                msg = "Username already exists, try another one!";
                return  msg;
            }
            registerStatement.setString(1, username);
            registerStatement.setString(2, pass1);
            System.out.println(registerStatement.toString());
            registerStatement.executeUpdate();
            msg = "Succesfully registered!";
        } catch (SQLException e) {
            System.out.println("Register query error " + e.getMessage());
        }
        return msg;
    }





}
