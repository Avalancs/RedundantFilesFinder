package org.avalancs.redundantfilesfinder.matchers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.*;

public class SizeAndContentMatcher extends Matcher {
    private final Map<Long, List<Path>> matchingSizes = new HashMap<>();
    final Map<Long, List<List<Path>>> matchingSizeAndContent = new TreeMap<>(Comparator.reverseOrder());

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

        matchingSizes.keySet().forEach( fileSize -> {
            List<Path> files = new ArrayList<>(matchingSizes.get(fileSize));

            do {
                Path file = files.get(0);
                files.remove(file);
                List<Path> matchingFiles = new ArrayList<>();
                matchingFiles.add(file);

                for(Path otherFile : files) {
                    try {
                        if(fileContentsEqual(file, otherFile)) {
                            matchingFiles.add(otherFile);
                        }
                    } catch (IOException e) {
                        System.out.println("Could not compare the following files: " + file.toAbsolutePath().toString() + " " + otherFile.toAbsolutePath().toString());
                        e.printStackTrace();
                    }
                }

                files.removeAll(matchingFiles);
                if(matchingFiles.size() > 1) {
                    if(!matchingSizeAndContent.containsKey(fileSize)) {
                        matchingSizeAndContent.put(fileSize, new ArrayList<>());
                    }
                    matchingSizeAndContent.get(fileSize).add(matchingFiles);
                }
            } while(!files.isEmpty());
        });

        matchingSizes.clear(); // don't need them now
    }

    @Override
    public void printResult() {
        matchingSizeAndContent.keySet().forEach(fileSize -> {
            List<List<Path>> matching = matchingSizeAndContent.get(fileSize);

            matching.forEach(matches -> {
                System.out.println(byteSizeToString(fileSize));
                matches.forEach(match -> System.out.println("\t" + match.toString()));
                System.out.println();
            });
        });
    }
}
