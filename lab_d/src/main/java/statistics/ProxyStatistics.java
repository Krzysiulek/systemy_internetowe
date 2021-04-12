package statistics;

import proxy.Response;

import java.io.IOException;

public interface ProxyStatistics {
    void addSentData(Response response) throws
                                        IOException;

    void addReceivedData(Response response) throws
                                            IOException;
}
