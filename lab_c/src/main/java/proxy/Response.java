package proxy;

import lombok.Builder;
import lombok.Data;

import java.util.List;
import java.util.Map;

@Data
@Builder
class Response {
    private byte[] body;
    private int code;
    private Map<String, List<String>> headerFields;
}
