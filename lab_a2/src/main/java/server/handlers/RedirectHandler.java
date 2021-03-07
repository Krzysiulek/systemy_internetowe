package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;

public class RedirectHandler implements HttpHandler {
    private static final int DEFAULT_STATUS = 500;


    public void handle(HttpExchange exchange) throws
                                              IOException {
        int status = getStatus(exchange);
        String response = "Redirect status: " + status;
        System.out.println(response);

        exchange.getResponseHeaders().set("Content-Type", "text/html");
        exchange.getResponseHeaders().set("Location", exchange.getRequestURI().getPath());

        exchange.sendResponseHeaders(status, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private int getStatus(HttpExchange exchange) {
        String status = exchange.getRequestURI().getPath()
                .replace("/redirect/", "");

        return status.matches("^\\d+$") ? Integer.valueOf(status) : DEFAULT_STATUS;
    }
}
