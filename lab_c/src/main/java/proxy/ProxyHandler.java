package proxy;

import com.sun.net.httpserver.Headers;
import com.sun.net.httpserver.HttpExchange;
import com.sun.net.httpserver.HttpHandler;
import lombok.extern.slf4j.Slf4j;

import java.io.*;
import java.net.HttpURLConnection;
import java.net.URI;
import java.net.URL;
import java.nio.charset.StandardCharsets;
import java.util.Collections;
import java.util.List;
import java.util.Objects;

@Slf4j
public class ProxyHandler implements HttpHandler {

    public void handle(HttpExchange exchange) throws
                                              IOException {
        URI uri = exchange.getRequestURI();
        String method = exchange.getRequestMethod();
        Headers requestHeaders = exchange.getRequestHeaders();
        InputStream requestBody = exchange.getRequestBody();

        Response res = Response.builder()
                               .build();

        try {
            res = createRequest(uri, method, requestBody, requestHeaders);
        } catch (IOException e) {
            e.printStackTrace();
        }

        byte[] content = res.getBody();
        int responseCode = res.getCode();

        log.info("Code: {}. Len: {}. Uri: {}", responseCode, content.length, uri.toString());

        res.getHeaderFields()
           .entrySet()
           .stream()
           .filter(Objects::nonNull)
           .filter(entry -> entry.getKey() != null)
           .filter(entry -> !entry.getKey()
                                  .equalsIgnoreCase("Transfer-Encoding"))
           .forEach(entry -> exchange.getResponseHeaders()
                                     .add(entry.getKey(), getHeaderValue(entry.getValue())));

        exchange.getResponseHeaders()
                .add("Via", "Uber-Proxy alpha 1.0");
        exchange.sendResponseHeaders(responseCode, getContentLength(res, content));
        OutputStream os = exchange.getResponseBody();
        os.write(content);
        os.close();
    }

    private Response createRequest(URI uri, String method, InputStream requestBody, Headers requestHeaders) throws
                                                                                                            IOException {
        URL urlObj = new URL(uri.toString());
        HttpURLConnection urlCon = (HttpURLConnection) urlObj.openConnection();
        urlCon.setInstanceFollowRedirects(false);

        requestHeaders.forEach((k, v) -> urlCon.setRequestProperty(k, getHeaderValue(v)));
        urlCon.setRequestMethod(method);


//        String s = readResponse(requestBody);
        byte[] bytes = requestBody.readAllBytes();
        log.info("Bytes len: {}", bytes.length);

        if (bytes.length > 0) {
            urlCon.setDoOutput(true);

            DataOutputStream wr = new DataOutputStream(urlCon.getOutputStream());
            wr.write(bytes);
            wr.flush();
            wr.close();
        }

        InputStream inputStream;
        try {
            inputStream = urlCon.getInputStream();
        } catch (Exception e) {
            inputStream = urlCon.getErrorStream();
        }

        byte[] responseBytes = inputStream.readAllBytes();
        urlCon.disconnect();
        return Response.builder()
                       .body(responseBytes)
                       .code(urlCon.getResponseCode())
                       .headerFields(urlCon.getHeaderFields())
                       .build();
    }

    private String getHeaderValue(List<String> v) {
        return String.join("; ", v);
    }

    private int getContentLength(Response res, byte[] content) {
        return Integer.valueOf(res.getHeaderFields()
                                  .getOrDefault("Content-Length",
                                                Collections.singletonList(String.valueOf(content.length)))
                                  .get(0));
    }

    private String readResponse(InputStream requestBody) throws
                                                         IOException {
        InputStreamReader isr = new InputStreamReader(requestBody, StandardCharsets.UTF_8);
        BufferedReader br = new BufferedReader(isr);
        String value = br.readLine();

        return value;
    }
}
