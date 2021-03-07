package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class Auth2Handler implements HttpHandler {
    @Override
    public void handle(HttpExchange exchange) throws
                                              IOException {
        String response = "Auth 2";
        exchange.getResponseHeaders()
                .set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


}
