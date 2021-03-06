package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Paths;

class RootHandler implements HttpHandler {
    private static final String INDEX_FILE = "lab_a1/src/server/index.html";


    public void handle(HttpExchange exchange) throws
                                              IOException {
        String response = getFileContent();
        exchange.getResponseHeaders()
                .set("Content-Type", "text/html");
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private String getFileContent() throws
                                    IOException {
        return String.join("", Files.readAllLines(Paths.get(RootHandler.INDEX_FILE)));
    }
}