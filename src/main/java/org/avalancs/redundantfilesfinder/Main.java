package org.avalancs.redundantfilesfinder;

import org.avalancs.redundantfilesfinder.matchers.*;

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

        if(rootDir != null) {
            Boolean namesMatch = shouldFileNamesMatch();
            Boolean fileSizeMatch = shouldFileSizesMatch();
            Boolean contentMatch = null;
            if(namesMatch == null || fileSizeMatch == null) {
                return;
            } else if(fileSizeMatch) {
                contentMatch = shouldFileContentMatch();
                if(contentMatch == null) return;
            }

            if(namesMatch) {
                if(fileSizeMatch && !contentMatch) {
                    fileMatcher = new FileNameAndSizeMatcher();
                } else if(fileSizeMatch && contentMatch) {
                    fileMatcher = new FileNameSizeContentMatcher();
                } else {
                    fileMatcher = new FileNameMatcher();
                }
            } else {
                if(fileSizeMatch && contentMatch) {
                    fileMatcher = new SizeAndContentMatcher();
                } else {
                    fileMatcher = new SizeMatcher();
                }
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
    static File chooseFolder() {
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
     * Displays a pop-up question whether the file name has to match to be considered redundant
     * @return true on Yes; false on No; null on Cancel
     */
    static Boolean shouldFileNamesMatch() {
        int result = JOptionPane.showConfirmDialog(null, "Should names have to match to be considered redundant?", "File name matching", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION) {
            return true;
        } else if(result == JOptionPane.NO_OPTION) {
            return false;
        } else {
            return null;
        }
    }

    /**
     * Displays a pop-up question whether the file size has to match as well as the name for files to be considered redundant
     * @return true on Yes; false on No; null on Cancel
     */
    static Boolean shouldFileSizesMatch() {
        int result = JOptionPane.showConfirmDialog(null, "Should file size also have to match to be considered redundant?", "File size matching", JOptionPane.YES_NO_CANCEL_OPTION, JOptionPane.QUESTION_MESSAGE);
        if(result == JOptionPane.YES_OPTION) {
            return true;
        } else if(result == JOptionPane.NO_OPTION) {
            return false;
        } else {
            return null;
        }
    }

    static Boolean shouldFileContentMatch() {
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
     * @param currentDir the given directory
     * @throws IOException If the user does not have permission to list {@code currentDir}
     */
    static void iterateOverDirectory(Path currentDir) throws IOException {
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
