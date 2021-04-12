package proxy;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;

import java.net.URI;
import java.util.List;
import java.util.Map;

@Getter
@Builder
@AllArgsConstructor
public class Response {
    private byte[] body;
    private long bodyLength;
    private int code;
    private URI uri;
    private Map<String, List<String>> headerFields;
}
