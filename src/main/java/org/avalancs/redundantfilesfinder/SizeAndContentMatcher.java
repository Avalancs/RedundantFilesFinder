package org.avalancs.redundantfilesfinder;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class SizeAndContentMatcher implements Matcher {
    private final Map<Long, List<Path>> matchingSizes = new HashMap<>();
    private final Map<Long, List<List<Path>>> matchingSizeAndContent = new HashMap<>();

    @Override
    public void add(Path path) {
        try {
            long size = Files.size(path);
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
            List<Path> files = new ArrayList<>();
            files.addAll(matchingSizes.get(fileSize));

            do {
                Path file = files.get(0);
                files.remove(file);
                List<Path> matchingFiles = new ArrayList<>();
                matchingFiles.add(file);

                for(Path otherFile : files) {
                    try {
                        if(FileUtils.contentEquals(file.toFile(), otherFile.toFile())) {
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
                System.out.println(fileSize + " bytes: ");
                for(Path match : matches) {
                    System.out.println("\t" + match.toString());
                }
                System.out.println();
            });
        });
    }
}
