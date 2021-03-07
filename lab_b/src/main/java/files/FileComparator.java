package files;

import java.io.File;
import java.nio.file.Path;

public class FileComparator {

    private FileComparator() {}

    public static int compareFiles(Path path1, Path path2) {
        File first = path1.toFile();
        File second = path2.toFile();

        if (first.isDirectory() && second.isDirectory())
            return first.compareTo(second);

        if (first.isDirectory())
            return compareToFile(first, second);

        if (second.isDirectory())
            return -(compareToFile(second, first));

        return compareFiles(first, second);
    }

    private static int compareFiles(File first, File second) {
        File firstParentFile = first.getParentFile();
        File secondParentFile = second.getParentFile();

        if (isSubDir(firstParentFile, secondParentFile))
            return -1;

        if (isSubDir(secondParentFile, firstParentFile))
            return 1;

        return first.compareTo(second);
    }

    private static int compareToFile(File directory, File file) {
        File fileParent = file.getParentFile();
        if (directory.equals(fileParent))
            return -1;

        if (isSubDir(directory, fileParent))
            return -1;

        return directory.compareTo(file);
    }

    private static boolean isSubDir(File directory, File subDir) {
        for (File parentDir = directory.getParentFile(); parentDir != null; parentDir = parentDir.getParentFile()) {
            if (subDir.equals(parentDir)) {
                return true;
            }
        }

        return false;
    }
}
