package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.net.HttpCookie;
import java.util.Random;
import java.util.stream.IntStream;

public class CookiesHandler implements HttpHandler {
    private static final int COOKIE_LENGTH = 20;

    // todo sprawdzarka tu nie przechodzi cała
    @Override
    public void handle(HttpExchange exchange) throws
                                              IOException {

        String cookieText = getRandomString();
        String response = "Cookie: " + cookieText;

        exchange.getResponseHeaders().add("Content-Type", "text/html");

        HttpCookie cookie = new HttpCookie("random-cookie", cookieText);
        cookie.setDomain("http://localhost:8000/");
        exchange.getResponseHeaders().add("Set-Cookie", cookie.toString());

        exchange.sendResponseHeaders(200, response.length());
        OutputStream os = exchange.getResponseBody();
        os.write(response.getBytes());
        os.close();
    }


    private String getRandomString() {
        StringBuilder builder = new StringBuilder();
        String letters = "1234567890qazxswedcvfrtgbnhyujmkiolp";

        IntStream.range(0, COOKIE_LENGTH)
                 .forEach(character -> builder.append(letters.charAt(new Random().nextInt(letters.length()))));

        return builder.toString();
    }
}

