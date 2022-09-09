package middleware;

import java.io.Serializable;

public class MailMsg implements Serializable {
    public String type;
    public String dataJson;

    public Object data;
    public MailMsg(String type, String dataJson, Object data) {
        this.type = type;
        this.dataJson = dataJson;
        this.data = data;
    }
}
