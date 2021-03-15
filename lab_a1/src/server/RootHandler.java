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
        byte[] fileContent = getFileContent();
        exchange.sendResponseHeaders(200, fileContent.length);
        OutputStream os = exchange.getResponseBody();
        os.write(fileContent);
        os.close();
    }

    private byte[] getFileContent() throws
                                    IOException {
        return Files.readAllBytes(Paths.get(INDEX_FILE));
    }
}