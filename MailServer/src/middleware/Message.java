package middleware;

import java.io.Serializable;
import java.util.ArrayList;

public class Message implements Serializable {
    public Integer id;
    public String title;
    public String content;
    public Integer date_and_time;
    public Integer isRead;
    public String senderUsername;
    public ArrayList<String> reciverUsernames;
    public Integer isFinished;
}
