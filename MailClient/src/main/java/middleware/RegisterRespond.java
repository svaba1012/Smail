package middleware;

import java.io.Serializable;

public class RegisterRespond implements Serializable {
    public String username;
    public String password;
//    status 0 all ok, status 1 username already in use
    public Integer status;
}
