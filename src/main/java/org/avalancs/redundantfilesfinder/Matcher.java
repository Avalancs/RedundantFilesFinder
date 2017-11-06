package org.avalancs.redundantfilesfinder;

import java.nio.file.Path;

public interface Matcher {
    /**
     * Add given path to the internal representation
     * @param path
     */
    void add(Path path);

    /**
     * Remove non-redundant files from the internal representation
     */
    void preProcess();

    /**
     * Prints the redundant files, must be called after {@link #preProcess()}
     */
    void printResult();
}
