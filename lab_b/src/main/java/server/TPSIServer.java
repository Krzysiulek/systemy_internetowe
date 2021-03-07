package server;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;
import java.nio.file.Path;
import java.util.Scanner;


public class TPSIServer {
    private static final int PORT = 8000;

    public static void main(String[] args) throws
                                           Exception {
        System.out.println("Pass path to directory to share:");
        String path = new Scanner(System.in).nextLine();
        System.out.println("Sharing directory: " + path);

        if (pathExists(path)) {
            HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
            server.createContext("/", new FileHandler(path));
            server.createContext("/../", new FileHandler(path));
            server.start();
            System.out.println("Server started on: http://localhost:" + PORT);
        } else {
            System.out.println("Path " + path + " doesn't exists. Stopping server.");
        }
    }


    private static boolean pathExists(String path) {
        return Path.of(path).toFile().exists();
    }
}
