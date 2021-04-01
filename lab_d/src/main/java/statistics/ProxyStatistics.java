package statistics;

import proxy.Response;

import java.io.IOException;

public interface ProxyStatistics {
    void add(Response response) throws
                                IOException;
}
