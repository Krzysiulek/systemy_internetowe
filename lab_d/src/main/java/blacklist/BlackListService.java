package blacklist;

import lombok.extern.slf4j.Slf4j;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.Collections;
import java.util.List;

@Slf4j
public class BlackListService {
    private List<String> blackList;

    public BlackListService() {
        blackList = getBlacklist();
    }

    public boolean isInBlackList(URI uri) {
        String host = uri.getHost();

        if (blackList.contains(host)) {
            log.warn("Host {} blacklisted!", host);
            return true;
        }

        return false;
    }

    private List<String> getBlacklist() {
        try {
            Path path = Paths.get(BlackListService.class.getResource("/blacklist.txt")
                                                        .toURI());
            return Files.readAllLines(path);
        } catch (IOException | URISyntaxException e) {
            log.error(e.getMessage());
            return Collections.emptyList();
        }
    }
}
