package org.avalancs.redundantfilesfinder.matchers;

import java.io.IOException;
import java.nio.file.Path;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class FileNameSizeContentMatcher extends Matcher {
    private final Map<String, List<Path>> matchingFileNames = new HashMap<>();
    final Map<String, List<List<Path>>> matchingFileNamesAndContent = new HashMap<>();

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
        // Remove files from the map that are not duplicates
        matchingFileNames.keySet().removeIf(fileName -> matchingFileNames.get(fileName).size() < 2);

        matchingFileNames.keySet().forEach( fileName -> {
            List<Path> files = new ArrayList<>(matchingFileNames.get(fileName));

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
                    if(!matchingFileNamesAndContent.containsKey(fileName)) {
                        matchingFileNamesAndContent.put(fileName, new ArrayList<>());
                    }
                    matchingFileNamesAndContent.get(fileName).add(matchingFiles);
                }
            } while(!files.isEmpty());
        });

        matchingFileNames.clear(); // don't need them now
    }

    @Override
    public void printResult() {
        matchingFileNamesAndContent.keySet().forEach(fileName -> {
            List<List<Path>> matching = matchingFileNamesAndContent.get(fileName);

            matching.forEach(matches -> {
                System.out.println(fileName + " :");
                matches.forEach(match -> System.out.println("\t" + match.toString()));
                System.out.println();
            });
        });
    }
}
