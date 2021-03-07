package server;

import com.sun.net.httpserver.HttpContext;
import com.sun.net.httpserver.HttpServer;
import server.handlers.*;

import java.net.InetSocketAddress;


public class TPSIServer {
    private static final int PORT = 8000;

    public static void main(String[] args) throws
                                           Exception {
        HttpServer server = HttpServer.create(new InetSocketAddress(PORT), 0);
        server.createContext("/", new RootHandler());
        server.createContext("/echo", new EchoHandler());
        server.createContext("/redirect", new RedirectHandler());
        server.createContext("/cookies", new CookiesHandler());
        server.createContext("/auth", new AuthHandler());

        HttpContext basicAuthenticatorContext = server.createContext("/auth2", new Auth2Handler());
        basicAuthenticatorContext.setAuthenticator(new MyBasicAuthenticator());

        server.start();

        System.out.println("Server started on: http://localhost:" + PORT);
    }
}
