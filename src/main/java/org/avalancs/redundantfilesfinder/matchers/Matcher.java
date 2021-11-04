package org.avalancs.redundantfilesfinder.matchers;

import org.apache.commons.io.FileUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

public abstract class Matcher {
    /**
     * Add given path to the internal representation
     * @param path
     */
    public abstract void add(Path path);

    /**
     * Remove non-redundant files from the internal representation
     */
    public abstract void preProcess();

    /**
     * Prints the redundant files, must be called after {@link #preProcess()}
     */
    public abstract void printResult();

    /**
     * Get the name of the file. Necessary for mocking
     */
    String getFileName(Path path) {
        return path.getFileName().toString();
    }

    /**
     * Get the size of the file. Necessary for mocking
     */
    long getFileSize(Path path) throws IOException {
        return Files.size(path);
    }

    /**
     * Compares if the two files denoted by the paths have the same content byte-wise using Apache Commons.
     * Necessary for mocking
     */
    boolean fileContentsEqual(Path file, Path otherFile) throws IOException {
        return FileUtils.contentEquals(file.toFile(), otherFile.toFile());
    }

    /**
     * @param bytes the file size in bytes, e.g. from {@link Files#size}
     * @return a string for describing the size in gigabytes, megabytes, kilobytes or bytes where applicable.
     */
    protected String byteSizeToString(Long bytes) {
        if(bytes == null) {
            return "0 bytes:";
        }
        long gigabytes = bytes / (1024L*1024L*1024L);
        long megabytes = bytes / (1024L*1024L);
        long kilobytes = bytes / 1024L;

        if(gigabytes > 0L) {
            return gigabytes + " GB (" + megabytes + " MB):";
        } else if(megabytes > 0L) {
            return megabytes + " MB (" + bytes + " bytes):";
        } else if(kilobytes > 0L) {
            return kilobytes + " KB (" + bytes + " bytes):";
        } else {
            return bytes + " bytes:";
        }
    }
}
