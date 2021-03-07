package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Base64;

public class AuthHandler implements HttpHandler {
    private static final String REQ_AUTH_HEADER = "WWW-Authenticate";
    private static final String RES_AUTH_HEADER = "Authorization";
    private static final String BASIC_REALM = "Basic realm=\"GET\"";

    private static final int OK_STATUS = 200;
    private static final int FORBIDDEN = 401;

    private static final String MOCK_LOGIN = "admin";
    private static final String MOCK_PASSWORD = "password";

    @Override
    public void handle(HttpExchange exchange) throws
                                              IOException {
        String response = "Auth";
        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.getResponseHeaders().set(REQ_AUTH_HEADER, BASIC_REALM);

        int status = getStatus(exchange);

        exchange.sendResponseHeaders(status, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private int getStatus(HttpExchange exchange) {
        String authenticationResponse = getAuthorization(exchange);
        if (authenticationResponse == null) {
            return FORBIDDEN;
        }


        String[] split = new String(Base64.getDecoder()
                                          .decode(authenticationResponse)).split(":");

        if (split.length == 2) {
            String login = split[0];
            String password = split[1];
            return login.equals(MOCK_LOGIN) && password.equals(MOCK_PASSWORD) ? OK_STATUS : FORBIDDEN;
        }


        return FORBIDDEN;
    }

    private String getAuthorization(HttpExchange exchange) {
        String authenticationResponse = exchange.getRequestHeaders().getFirst(RES_AUTH_HEADER);

        if (authenticationResponse != null) {
            authenticationResponse = authenticationResponse.replace("Basic ", "");
        }

        return authenticationResponse;
    }
}
