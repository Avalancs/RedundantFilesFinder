package org.avalancs.redundantfilesfinder;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

/**
 * A matcher which considers files with the same name and size redundant
 */
public class FileNameAndSizeMatcher implements Matcher {
    private final Map<String, Map<Long, List<Path>>> matchingFileNameAndSizes = new HashMap<>();

    @Override
    public void add(Path path) {
        String fileName = path.getFileName().toString();
        Map<Long, List<Path>> sizeMap;
        Long fileSize = null;
        List<Path> pathList;
        try {
            fileSize = Files.size(path);
        } catch (IOException e) {
            e.printStackTrace();
            return;
        }

        if(matchingFileNameAndSizes.containsKey(fileName)) {
            sizeMap = matchingFileNameAndSizes.get(fileName);
        } else {
            sizeMap = new HashMap<>();
            matchingFileNameAndSizes.put(fileName, sizeMap);
        }

        if(!sizeMap.containsKey(fileSize)) {
            pathList = new ArrayList<>();
            sizeMap.put(fileSize, pathList);
        } else {
            pathList = sizeMap.get(fileSize);
        }

        pathList.add(path);
    }

    @Override
    public void preProcess() {
        // remove size sets if there is no 2 files with the same name and size
        matchingFileNameAndSizes.keySet().forEach(
                sizeSet -> {
                        Map<Long, List<Path>> sizeMap = matchingFileNameAndSizes.get(sizeSet);
                        sizeMap.keySet().removeIf(sizeKey -> sizeMap.get(sizeKey).size() < 2);
                }
        );

        // remove all filenames which does not have at least 1 size set
        matchingFileNameAndSizes.keySet().removeIf(
                sizeSet -> matchingFileNameAndSizes.get(sizeSet).isEmpty()
        );
    }

    @Override
    public void printResult() {
        matchingFileNameAndSizes.values().forEach(duplicate -> {
            duplicate.keySet().forEach(
                sizeKey -> {
                    List<Path> paths = duplicate.get(sizeKey);
                    String fileName = paths.get(0).getFileName().toString();
                    System.out.println(fileName + " (" + sizeKey + " bytes)");

                    for (Path p : paths) {
                        System.out.println("\t " + p.toString());
                    }
                }

            );
        });
    }
}
