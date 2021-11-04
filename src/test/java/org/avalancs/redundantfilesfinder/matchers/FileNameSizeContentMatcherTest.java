package org.avalancs.redundantfilesfinder.matchers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class FileNameSizeContentMatcherTest extends MatchersTest {

    @BeforeEach
    public void setUp() {
        matcher = createMatcherSpy(FileNameSizeContentMatcher.class);
    }

    @Test
    public void testSizesMatch() {
        matcher.add(pathMocks.get(0)); // a.txt : 1 byte
        matcher.add(pathMocks.get(1)); // subfolder/a.txt : 1 byte
        matcher.add(pathMocks.get(2)); // subfolder/subfolder/a.txt : 2 bytes

        matcher.preProcess();

        Map<String, List<List<Path>>> resultMap = getMapFromMatcher();
        assertEquals(1, resultMap.keySet().size());
        assertNotNull(resultMap.get("a.txt"));
        assertEquals(1, resultMap.get("a.txt").size());
        assertEquals(pathMocks.get(0), resultMap.get("a.txt").get(0).get(0));
        assertEquals(pathMocks.get(1), resultMap.get("a.txt").get(0).get(1));
    }

    @Test
    public void testSizesDoNotMatch() {
        matcher.add(pathMocks.get(0)); // a.txt : 1 bytes
        matcher.add(pathMocks.get(3)); // b.txt : 1 bytes
        matcher.add(pathMocks.get(4)); // b.txt : 2 bytes

        matcher.preProcess();

        Map<String, List<List<Path>>> resultMap = getMapFromMatcher();
        assertEquals(0, resultMap.keySet().size());
    }

    @Test
    public void testComplex() {
        pathMocks.forEach(path -> matcher.add(path));
        matcher.preProcess();

        Map<String, List<List<Path>>> resultMap = getMapFromMatcher();
        assertEquals(1, resultMap.keySet().size());
        assertNotNull(resultMap.get("a.txt"));
        assertEquals(1, resultMap.get("a.txt").size());
        assertEquals(pathMocks.get(0), resultMap.get("a.txt").get(0).get(0));
        assertEquals(pathMocks.get(1), resultMap.get("a.txt").get(0).get(1));
    }

    private Map<String, List<List<Path>>> getMapFromMatcher() {
        return ((FileNameSizeContentMatcher) matcher).matchingFileNamesAndContent;
    }
}
