package server;

import java.net.URISyntaxException;
import java.net.URL;
import java.nio.file.Path;
import java.util.Objects;

public class ResourcesUtils {

    public static Path getFile(String fileName) {
        URL resource = ResourcesUtils.class.getClassLoader()
                                           .getResource(fileName);

        try {
            return Path.of(Objects.requireNonNull(resource)
                                  .toURI());
        } catch (URISyntaxException e) {
            return Path.of("");
        }
    }
}
