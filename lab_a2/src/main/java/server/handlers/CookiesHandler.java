package server.handlers;

import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;

import java.io.IOException;
import java.io.OutputStream;
import java.util.Random;
import java.util.stream.IntStream;

public class CookiesHandler implements HttpHandler {
    private static final int COOKIE_LENGTH = 20;
    private static final String SET_COOKIE = "Set-Cookie";

    @Override
    public void handle(HttpExchange exchange) throws
                                              IOException {
        String cookieWithEchoPath = String.format("randomCookie1=%s; Path=/echo; Domain=localhost", getRandomString());
        String cookieWithoutPath = String.format("randomCookie2=%s", getRandomString());
        String cookieWithInvalidDomain = String.format("randomCookie3=%s; Domain=google.com", getRandomString());
        String response = String.format("Cookies: %n%s%n%s%n%s", cookieWithEchoPath, cookieWithoutPath, cookieWithInvalidDomain);

        exchange.getResponseHeaders().add("Content-Type", "text/html");
        exchange.getResponseHeaders().add(SET_COOKIE, cookieWithEchoPath);
        exchange.getResponseHeaders().add(SET_COOKIE, cookieWithoutPath);
        exchange.getResponseHeaders().add(SET_COOKIE, cookieWithInvalidDomain);

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

