package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import server.ResourcesUtils;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;

public class RootHandler implements HttpHandler {
    private static final String INDEX_FILE = "index.html";


    @Override
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
        Path file = ResourcesUtils.getFile(INDEX_FILE);
        return String.join("", Files.readAllLines(file));
    }
}