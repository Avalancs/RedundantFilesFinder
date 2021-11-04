package org.avalancs.redundantfilesfinder.matchers;

import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A matcher that considers files with matching names redundant
 */
public class FileNameMatcher extends Matcher {
    final Map<String, List<Path>> matchingFileNames = new HashMap<>();

    @Override
    public void add(Path path) {
        String fileName = getFileName(path);
        if(matchingFileNames.containsKey(fileName)) {
            matchingFileNames.get(fileName).add(path);
        } else {
            List<Path> pathList = new ArrayList<>();
            pathList.add(path);
            matchingFileNames.put(fileName, pathList);
        }
    }

    @Override
    public void preProcess() {
        matchingFileNames.keySet().removeIf(fileName -> matchingFileNames.get(fileName).size() < 2);
    }

    @Override
    public void printResult() {
        matchingFileNames.values().forEach(duplicate -> {
            System.out.println(duplicate.get(0).getFileName().toString() + " :");
            duplicate.forEach(path -> System.out.println("\t " + path.toString()));
            System.out.println();
        });
    }
}
