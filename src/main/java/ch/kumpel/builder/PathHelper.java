package ch.kumpel.builder;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class PathHelper {

    public static final String DELIMITER = "/";

    public static List<String> splitPath(String path) {
        return new ArrayList<String>(Arrays.asList(path.split(DELIMITER)));
    }

    public static String createPath(List<String> path) {
        String pathString = "";
        for (String pathPart : path) {
            pathString += pathPart + DELIMITER;

        }
        return pathString;
    }

}
