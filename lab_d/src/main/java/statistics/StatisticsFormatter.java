package statistics;

import lombok.AccessLevel;
import lombok.NoArgsConstructor;

import java.util.concurrent.ConcurrentHashMap;
import java.util.stream.Collectors;

@NoArgsConstructor(access = AccessLevel.PRIVATE)
class StatisticsFormatter {
    private static final String CSV_LINE_FORMAT = "%s; %s; %s %n";
    private static final String CSV_HEADER = String.format(CSV_LINE_FORMAT, "Domain", "Request count", "Data [b]");

    static String getContent(ConcurrentHashMap<String, Statistics> statisticsMap) {
        return CSV_HEADER + statisticsMap
                .values()
                .stream()
                .map(StatisticsFormatter::getCsvLine)
                .collect(Collectors.joining());
    }


    private static String getCsvLine(Statistics statistics) {
        return String.format(CSV_LINE_FORMAT,
                             statistics.getDomain(),
                             statistics.getRequestCount(),
                             statistics.getBodyLength());
    }

}
