package proxy;

import com.sun.net.httpserver.HttpServer;

import java.io.IOException;
import java.net.InetSocketAddress;

public class MainProxy {
    private static final int PORT = 8009;


    public static void main(String... args) throws
                                            IOException {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new ProxyHandler());
        server.start();
    }
}
