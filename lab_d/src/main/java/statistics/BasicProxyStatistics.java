package statistics;

import lombok.Getter;
import lombok.extern.slf4j.Slf4j;
import proxy.Response;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Objects;
import java.util.concurrent.ConcurrentHashMap;

@Slf4j
public class BasicProxyStatistics implements ProxyStatistics {
    @Getter
    private final ConcurrentHashMap<String, Statistics> statisticsSentMap = new ConcurrentHashMap<>();
    @Getter
    private final ConcurrentHashMap<String, Statistics> statisticsReceivedMap = new ConcurrentHashMap<>();
    private Path sentData = Paths.get(new File("statistics/sent.csv").getAbsolutePath());
    private Path receivedData = Paths.get(new File("statistics/received.csv").getAbsolutePath());

    public BasicProxyStatistics() throws
                                  IOException {
        loadOrUpdateMap(sentData, statisticsSentMap);
        loadOrUpdateMap(receivedData, statisticsReceivedMap);
    }

    @Override
    public synchronized void addSentData(Response response) throws
                                                            IOException {
        String domain = response.getUri()
                                .getHost();

        statisticsSentMap.putIfAbsent(domain, new Statistics(domain));
        Statistics statistics = this.statisticsSentMap.get(domain);
        statistics.add(response);

        updateSentFile();
    }

    @Override
    public synchronized void addReceivedData(Response response) throws
                                                                IOException {
        String domain = response.getUri()
                                .getHost();

        statisticsReceivedMap.putIfAbsent(domain, new Statistics(domain));
        Statistics statistics = this.statisticsReceivedMap.get(domain);
        statistics.add(response);

        updateReceivedFile();
    }

    private void loadMap(Path path, ConcurrentHashMap<String, Statistics> statisticsMap) throws
                                                                                         IOException {
        Files.readAllLines(path)
             .stream()
             .map(Statistics::getFromLine)
             .filter(Objects::nonNull)
             .forEach(statistics -> statisticsMap.put(statistics.getDomain(), statistics));
    }

    private void loadOrUpdateMap(Path path,
                                 ConcurrentHashMap<String, Statistics> statisticsMap) throws
                                                                                      IOException {
        if (path.toFile()
                .exists()) {
            loadMap(path, statisticsMap);
            log.info("Statistics file {} updated", path);
        } else {
            createStatisticsFile(path);
            log.warn("Statistics file {} created", path);
        }
    }

    private boolean createStatisticsFile(Path path) throws
                                                    IOException {
        File file = new File(path.toString());
        return file.getParentFile()
                   .mkdirs() && file.createNewFile();
    }

    private synchronized void updateSentFile() throws
                                               IOException {
        String content = StatisticsFormatter.getContent(statisticsSentMap);

        Files.write(sentData, content.getBytes());
        log.info("File {} updated", sentData.toAbsolutePath());
    }

    private synchronized void updateReceivedFile() throws
                                                   IOException {
        String content = StatisticsFormatter.getContent(statisticsReceivedMap);

        Files.write(receivedData, content.getBytes());
        log.info("File {} updated", receivedData.toAbsolutePath());
    }
}
