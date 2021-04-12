package statistics;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.RequiredArgsConstructor;
import org.apache.commons.lang.math.NumberUtils;
import proxy.Response;

@Getter
@RequiredArgsConstructor
@AllArgsConstructor
public class Statistics {
    private final String domain;
    private long requestCount;
    private long bodyLength;

    /**
     * Line format:
     * domain; requestCount; bodyLength \n
     */
    static Statistics getFromLine(String line) {
        final String[] splitResult = line.split(";");

        if (splitResult.length == 3 && NumberUtils.isNumber(splitResult[1]) && NumberUtils.isNumber(splitResult[2])) {
            String domain = splitResult[0];
            long requestCount = Long.parseLong(splitResult[1]);
            long bodyLength = Long.parseLong(splitResult[2]);

            return new Statistics(domain, requestCount, bodyLength);
        }

        return null;
    }

    synchronized void add(Response response) {
        requestCount += 1;
        bodyLength += response.getBodyLength();
    }

}
