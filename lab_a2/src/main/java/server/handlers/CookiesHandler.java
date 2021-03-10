package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.stream.IntStream;

public class CookiesHandler implements HttpHandler {
    private static final int COOKIE_LENGTH = 20;

    // todo randomizeCookies
    @Override
    public void handle(HttpExchange exchange) throws
                                              IOException {

        String cookieText = getRandomString();
        String response = "Cookie: " + cookieText;

        exchange.getResponseHeaders().add("Content-Type", "text/html");
        exchange.getResponseHeaders().add("Set-Cookie", "randomCookie1=ABC; Path=/echo; Domain=localhost");
        exchange.getResponseHeaders().add("Set-Cookie", "randomCookie2=XYZ");
        exchange.getResponseHeaders().add("Set-Cookie", "randomCookie3=POI; Domain=google.com");

        exchange.sendResponseHeaders(200, response.getBytes().length);
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

