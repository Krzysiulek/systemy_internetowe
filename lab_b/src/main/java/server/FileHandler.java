package server;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import files.FileComparator;
import lombok.SneakyThrows;

import java.io.File;
import java.io.IOException;
import java.io.OutputStream;
import java.net.URLDecoder;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.stream.Collectors;
import java.util.stream.Stream;

import static server.Constants.*;

public class FileHandler implements HttpHandler {
    private final Path basePath;
    private Path currentPath;

    private final String HTML_BEGGINGING = "<!DOCTYPE html>\n" +
            "<html lang=\"pl\">\n" +
            "<title>Page Title</title>\n" +
            "<meta charset=\"UTF-8\">";

    FileHandler(String basePath) {
        this.basePath = Paths.get(basePath);
    }

    @Override
    public void handle(HttpExchange exchange) throws
                                              IOException {
        String path = exchange.getRequestURI()
                              .getPath();
        currentPath = Paths.get(basePath.toString() + path);
        System.out.println(currentPath);

        if (!currentPath.toFile()
                        .getCanonicalPath()
                        .startsWith(basePath.toString())) {
            manageTraversalFound(exchange);
        }

        if (currentPath.toFile()
                       .isFile()) {
            manageFile(exchange);
        } else if (currentPath.toFile()
                              .isDirectory()) {
            manageDirectory(exchange);
        } else {
            manageNofFound(exchange);
        }
    }

    private void manageFile(HttpExchange exchange) throws
                                                   IOException {
        exchange.getResponseHeaders()
                .set(CONTENT_TYPE, TEXT_HTML);
        exchange.getResponseHeaders()
                .add(CONTENT_DISPOSITION,
                     "attachment; filename=" + currentPath.toFile()
                                                          .getName());
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

        return HTML_BEGGINGING + pathStream
                .sorted(FileComparator::compareFiles)
                .map(this::getHTML)
                .collect(Collectors.joining());
    }

    @SneakyThrows
    private String getHTML(Path path) {
        boolean directory = path.toFile()
                                .isDirectory();
        boolean currentLocation = currentPath.equals(path);

        // todo to jest niepotrzebne chbya
        String decode = URLDecoder.decode(path.toString(), "UTF-8");
        String fileName = new File(decode).getName();

        if (currentLocation || !path.toFile()
                                    .exists()) {
            return "";
        }
        String serverPath = path.toString()
                                .replace(basePath.toString(), "");
        serverPath = serverPath.replace("\\", "/");

        return new StringBuilder()
                .append(directory ? "<b>" : "")
                .append("<a href=\"")
                .append(serverPath)
                .append("\">")
                .append(fileName)
                .append("</a>")
                .append(directory ? "</b>" : "")
                .append("<br>")
                .toString();

    }
}
