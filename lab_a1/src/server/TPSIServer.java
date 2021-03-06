package server;

import com.sun.net.httpserver.HttpServer;

import java.net.InetSocketAddress;


public class TPSIServer {
    private static final int PORT = 8000;

    public static void main(String[] args) throws
                                           Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/file", new FileRootHandler());
        System.out.println("Starting server on port: " + PORT);
        server.start();
    }
}
