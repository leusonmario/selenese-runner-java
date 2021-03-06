package jp.vmi.selenium.selenese.utils;

import java.io.File;
import java.io.FilenameFilter;
import java.util.regex.Pattern;

import org.apache.commons.exec.OS;

import static org.apache.commons.io.FilenameUtils.*;

/**
 * Path utilities.
 */
public class PathUtils {

    private static class WinExeMatcher implements FilenameFilter {

        private final String[] exts;
        private final String matchingName;

        public WinExeMatcher(String matchingName) {
            String pathext = System.getenv("PATHEXT");
            exts = (pathext != null) ? pathext.split(";") : new String[0];
            this.matchingName = matchingName;
        }

        @Override
        public boolean accept(File dir, String name) {
            return (isExtension(name, exts) && getBaseName(name).equalsIgnoreCase(matchingName))
                || name.equalsIgnoreCase(matchingName);
        }
    }

    private static class UnixExeMatcher implements FilenameFilter {

        private final String matchingName;

        public UnixExeMatcher(String matchingName) {
            this.matchingName = matchingName;
        }

        @Override
        public boolean accept(File dir, String name) {
            return name.equals(matchingName);
        }
    }

    private static FilenameFilter getExeMatcher(String matchingName) {
        return OS.isFamilyWindows()
            ? new WinExeMatcher(matchingName)
            : new UnixExeMatcher(matchingName);
    }

    /**
     * Search executable files.
     *
     * @param filename filename of executable.
     * @return File object of executable.
     */
    public static File searchExecutableFile(final String filename) {
        FilenameFilter exeMatcher = getExeMatcher(filename);
        String[] curList = new File(".").list(exeMatcher);
        if (curList != null && curList.length > 0)
            return new File(".", curList[0]);
        for (String path : System.getenv("PATH").split(Pattern.quote(File.pathSeparator))) {
            File dir = new File(path);
            if (dir.isDirectory()) {
                String[] list = dir.list(exeMatcher);
                if (list != null && list.length > 0)
                    return new File(dir, list[0]);
            }
        }
        return null;
    }
}
