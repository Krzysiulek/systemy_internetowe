package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import files.FileComparator;

import java.io.IOException;
import java.io.OutputStream;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static server.Constants.*;

public class FileHandler implements HttpHandler {
    private final String basePath;
    private Path currentPath;

    FileHandler(String basePath) {
        this.basePath = basePath;
    }

    @Override
    public void handle(HttpExchange exchange) throws
                                              IOException {
        String path = exchange.getRequestURI()
                              .getPath();
        currentPath = Paths.get(basePath + path);
        System.out.println(currentPath);

        // TODO: 3/7/21 działa, ale tylko przez telnet
        if (!currentPath.toFile().getCanonicalPath().startsWith(basePath)) {
            manageTraversalFound(exchange);
        }

        if (currentPath.toFile().isFile()) {
            manageFile(exchange);
        } else if (currentPath.toFile().isDirectory()){
            manageDirectory(exchange);
        } else {
            manageNofFound(exchange);
        }
    }

    private void manageFile(HttpExchange exchange) throws
                                                   IOException {
        exchange.getResponseHeaders()
                .set(CONTENT_TYPE, TEXT_HTML);
        exchange.getResponseHeaders().add(CONTENT_DISPOSITION, "attachment; filename=" + currentPath.toFile().getName());
        OutputStream os = exchange.getResponseBody();
        exchange.sendResponseHeaders(200,
                                     currentPath.toFile()
                                                .length());
        os.write(Files.readAllBytes(currentPath.toAbsolutePath()));
        os.close();
    }

    private void manageDirectory(HttpExchange exchange) throws
                                                        IOException {
        String response = getFilesAndDirectories();
        exchange.getResponseHeaders()
                .set(CONTENT_TYPE, TEXT_HTML);
        exchange.sendResponseHeaders(200, response.getBytes().length);
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }

    private void manageNofFound(HttpExchange exchange) throws
                                                       IOException {
        manageError(exchange, 404);
    }

    private void manageTraversalFound(HttpExchange exchange) throws
                                                             IOException {
        manageError(exchange, 403);
    }

    private void manageError(HttpExchange exchange, int status) throws
                                                    IOException {
        exchange.getResponseHeaders()
                .set(CONTENT_TYPE, TEXT_HTML);
        exchange.sendResponseHeaders(status, 0);
        OutputStream os = exchange.getResponseBody();
        os.close();
    }

    private String getFilesAndDirectories() throws
                                            IOException {
        Stream<Path> pathStream = Files.list(currentPath);

        return pathStream
                .sorted(FileComparator::compareFiles)
                .map(this::getHTML)
                .collect(Collectors.joining());
    }

    private String getHTML(Path path) {
        boolean directory = path.toFile().isDirectory();
        boolean currentLocation = currentPath.equals(path);
        String fileName = path.getFileName().toString();

        if (currentLocation) {
            return "";
        }
        String pathWithRegexEscapes = basePath.replace("\\", "\\\\");
        String serverPath = path.toString()
                       .replaceFirst(pathWithRegexEscapes, "");

        return new StringBuilder()
                .append(directory ? "<b>" : "")
                .append("<a href=\"")
                .append(serverPath)
                .append("\">")
                .append(fileName)
                .append("</a>")
                .append(directory ? "</b>" : "")
                .append("<br>").toString();

    }
}
