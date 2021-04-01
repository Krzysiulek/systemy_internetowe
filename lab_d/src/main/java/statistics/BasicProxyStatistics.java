package statistics;

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
    private final ConcurrentHashMap<String, Statistics> statisticsMap = new ConcurrentHashMap<>();
    private Path path = Paths.get(new File("statistics/statistics.csv").getAbsolutePath());

    public BasicProxyStatistics() throws
                                  IOException {
        if (path.toFile()
                .exists()) {
            loadMap();
            log.info("Statistics file {} updated", path);
        } else {
            createStatisticsFile();
            log.warn("Statistics file {} created", path);
        }
    }

    @Override
    public void add(Response response) throws
                                       IOException {
        String domain = response.getUri()
                                .getHost();

        statisticsMap.putIfAbsent(domain, new Statistics(domain));
        Statistics statistics = this.statisticsMap.get(domain);
        statistics.add(response);

        updateFile();
    }

    private void loadMap() throws
                           IOException {
        Files.readAllLines(path)
             .stream()
             .map(Statistics::getFromLine)
             .filter(Objects::nonNull)
             .forEach(statistics -> this.statisticsMap.put(statistics.getDomain(), statistics));
    }

    private boolean createStatisticsFile() throws
                                           IOException {
        File file = new File(path.toString());
        return file.getParentFile()
                   .mkdirs() && file.createNewFile();
    }

    private void updateFile() throws
                              IOException {
        String content = StatisticsFormatter.getContent(statisticsMap);

        Files.write(path, content.getBytes());
        log.info("File {} updated", path.toAbsolutePath());
    }
}
