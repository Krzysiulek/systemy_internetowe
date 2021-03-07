package server.handlers;

import com.cedarsoftware.util.io.JsonWriter;
import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.HashMap;
import java.util.Map;

public class EchoHandler implements HttpHandler {

    @Override
    public void handle(HttpExchange exchange) throws
                                              IOException {
        Headers requestHeaders = exchange.getRequestHeaders();
        String response = JsonWriter.objectToJson(requestHeaders, getJsonWriterSettings());

        exchange.getResponseHeaders()
                .add("Content-Type", "application/json");

        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private Map<String, Object> getJsonWriterSettings() {
        Map<String, Object> args = new HashMap<>();
        args.put(JsonWriter.PRETTY_PRINT, true);

        return args;
    }
}

