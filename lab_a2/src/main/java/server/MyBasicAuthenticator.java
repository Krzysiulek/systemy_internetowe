package server;

import com.sun.net.httpserver.BasicAuthenticator;

public class MyBasicAuthenticator extends BasicAuthenticator {
    private static final String REALM_NAME = "AUTH2";
    private static final String LOGIN = "admin2";
    private static final String PASSWORD = "password2";

    MyBasicAuthenticator() {
        super(REALM_NAME);
    }

    @Override
    public boolean checkCredentials(String username, String password) {
        return username.equals(LOGIN) && password.equals(PASSWORD);
    }
}
