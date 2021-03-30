package proxy;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.DataOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.util.List;
import java.util.Map;

import static proxy.Constant.TRANSFER_ENCODING;
import static proxy.Constant.VIA;

@Slf4j
public class ProxyHandler implements HttpHandler {
    private static final String PROXY_INFO = "Uber-Kris-Proxy alpha 1.0";

    public void handle(HttpExchange exchange) throws
                                              IOException {
        Response res = createRequest(exchange);

        res.getHeaderFields()
           .entrySet()
           .stream()
           .filter(this::isValidHeader)
           .forEach(entry -> addHeader(exchange, entry));

        exchange.getResponseHeaders()
                .add(VIA, PROXY_INFO);

        exchange.sendResponseHeaders(res.getCode(), res.getBodyLength());
        OutputStream os = exchange.getResponseBody();
        os.write(res.getBody());
        os.close();
    }

    private Response createRequest(HttpExchange exchange) throws
                                                          IOException {
        Headers requestHeaders = exchange.getRequestHeaders();
        InputStream requestBody = exchange.getRequestBody();

        HttpURLConnection connection = getConnection(exchange);
        requestHeaders.forEach((k, v) -> connection.setRequestProperty(k, getHeaderValue(v)));

        byte[] bodyBytes = requestBody.readAllBytes();
        if (shouldWriteBody(bodyBytes)) {
            writeBody(bodyBytes, connection);
        }

        byte[] responseBytes = getInputStream(connection).readAllBytes();
        log.info("Code: {}. Len: {}. Uri: {}",
                 connection.getResponseCode(),
                 responseBytes.length,
                 exchange.getRequestURI()
                         .toString());
        connection.disconnect();

        return Response.builder()
                       .body(responseBytes)
                       .bodyLength(responseBytes.length)
                       .code(connection.getResponseCode())
                       .headerFields(connection.getHeaderFields())
                       .build();
    }

    private HttpURLConnection getConnection(HttpExchange exchange) throws
                                                                   IOException {
        URI uri = exchange.getRequestURI();
        String method = exchange.getRequestMethod();

        URL urlObj = new URL(uri.toString());
        HttpURLConnection connection = (HttpURLConnection) urlObj.openConnection();
        connection.setInstanceFollowRedirects(false);
        connection.setRequestMethod(method);

        return connection;
    }

    private String getHeaderValue(List<String> v) {
        return String.join("; ", v);
    }

    private boolean isValidHeader(Map.Entry<String, List<String>> entry) {
        return null != entry.getKey() && !entry.getKey()
                                               .equalsIgnoreCase(TRANSFER_ENCODING);
    }

    private void addHeader(HttpExchange exchange, Map.Entry<String, List<String>> entry) {
        exchange.getResponseHeaders()
                .add(entry.getKey(), getHeaderValue(entry.getValue()));
    }

    private InputStream getInputStream(HttpURLConnection urlCon) {
        try {
            return urlCon.getInputStream();
        } catch (Exception e) {
            log.error(e.getMessage());
            return urlCon.getErrorStream();
        }
    }

    private void writeBody(byte[] requestBody, HttpURLConnection connection) throws
                                                                             IOException {
        connection.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(connection.getOutputStream());
        wr.write(requestBody);
        wr.flush();
        wr.close();
    }

    private boolean shouldWriteBody(byte[] requestBody) {
        return requestBody.length > 0;
    }
}
