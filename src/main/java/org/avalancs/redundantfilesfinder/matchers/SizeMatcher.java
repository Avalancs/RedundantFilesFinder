package org.avalancs.redundantfilesfinder.matchers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SizeMatcher extends Matcher {
    final Map<Long, List<Path>> matchingSizes = new HashMap<>();

    @Override
    public void add(Path path) {
        try {
            long size = getFileSize(path);
            if(matchingSizes.containsKey(size)) {
                matchingSizes.get(size).add(path);
            } else {
                List<Path> pathList = new ArrayList<>();
                pathList.add(path);
                matchingSizes.put(size, pathList);
            }
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    @Override
    public void preProcess() {
        // Remove files from the map that are not duplicates
        matchingSizes.keySet().removeIf(fileSize -> matchingSizes.get(fileSize).size() < 2);
    }

    @Override
    public void printResult() {
        matchingSizes.keySet().forEach(fileSize -> {
            System.out.println(fileSize + " bytes: ");
            List<Path> matching = matchingSizes.get(fileSize);

            matching.forEach(match -> System.out.println("\t" + match.toString()));
            System.out.println();
        });
    }
}
