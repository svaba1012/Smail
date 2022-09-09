package middleware;

import java.io.Serializable;

public class Register implements Serializable {
    public String username;
    public String password;

    public Register(String username, String password) {
        this.username = username;
        this.password = password;
    }
}
