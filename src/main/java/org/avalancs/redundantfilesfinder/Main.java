package org.avalancs.redundantfilesfinder;

import javax.swing.*;
import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    private static Matcher fileMatcher;

    public static void main(String[] args) throws IOException {
        File rootDir = chooseFolder();
        Boolean fileSizeMatch;
        Boolean contentMatch = null;

        if(rootDir != null) {
            fileSizeMatch = shouldFileSizesMatch();
            if(fileSizeMatch == null) {
                return;
            } else if(fileSizeMatch == true) {
                contentMatch = shouldFileContentMatch();
                if(contentMatch == null) return;
            }

            if(fileSizeMatch == true && contentMatch != true) {
                fileMatcher = new FileNameAndSizeMatcher();
            } else if(fileSizeMatch == true && contentMatch == true) {
                fileMatcher = new FileNameSizeContentMatcher();
            } else {
                fileMatcher = new FileNameMatcher();
            }

            iterateOverDirectory(Paths.get(rootDir.getAbsolutePath()));
            fileMatcher.preProcess();
            System.out.println("***List of redundant files:");
            fileMatcher.printResult();
        }
    }

    /**
     * Displays a pop-up dialog to chose the root folder, from where the redundancy check will start
     * @return File object pointing to the root directory, or null if user cancels the pop-up
     */
    private static File chooseFolder() {
        JFileChooser chooser = new JFileChooser();
        chooser.setDialogTitle("Select root directory");
        chooser.setFileSelectionMode(JFileChooser.DIRECTORIES_ONLY);
        chooser.setAcceptAllFileFilterUsed(false); // disable the "All files" option.
        if (chooser.showOpenDialog(null) == JFileChooser.APPROVE_OPTION) {
            File rootDir = chooser.getSelectedFile();
            if(!rootDir.exists() || !rootDir.isDirectory()) {
                throw new RuntimeException("rootDir is not a valid folder! " + rootDir.getAbsolutePath());
            }

            return rootDir;
        } else {
            System.out.println("No Selection");
            return null;
        }
    }

    /**
     * Displays a pop-up question whether the file size has to match as well as the name for files to be considered redundant
     * @return true on Yes; false on No; null on Cancel
     */
    private static Boolean shouldFileSizesMatch() {
        int result = JOptionPane.showConfirmDialog(null, "Should file size also have to match to be considered redundant?", "File size matching", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION) {
            return true;
        } else if(result == JOptionPane.NO_OPTION) {
            return false;
        } else {
            return null;
        }
    }

    private static Boolean shouldFileContentMatch() {
        int result = JOptionPane.showConfirmDialog(null, "Should file content also have to match? WARNING: MIGHT TAKE FOREVER FOR LARGE FILES", "File size matching", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION) {
            return true;
        } else if(result == JOptionPane.NO_OPTION) {
            return false;
        } else {
            return null;
        }
    }

    /**
     * Iterates over the files in the given directory denoted by {@code currentDir}, and adds them to {@link #fileMatcher}
     * @param currentDir
     * @throws IOException If the user does not have permission to list {@code currentDir}
     */
    private static void iterateOverDirectory(Path currentDir) throws IOException {
        Files.list(currentDir).
                filter(file -> Files.isRegularFile(file)).
                forEach(file -> fileMatcher.add(file));
        Files.list(currentDir).
                filter(file -> Files.isDirectory(file)).
                forEach(file -> {
            try {
                iterateOverDirectory(file);
            } catch (IOException e) {
                e.printStackTrace();
            }
        });
    }
}
