package org.avalancs.redundantfilesfinder.matchers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class FileNameMatcherTest extends MatchersTest {

    @BeforeEach
    public void setUp() {
        matcher = createMatcherSpy(FileNameMatcher.class);
    }

    @Test
    public void testFileNamesMatch() {
        matcher.add(pathMocks.get(0)); // a.txt
        matcher.add(pathMocks.get(1)); // subfolder/a.txt

        matcher.preProcess();

        Map<String, List<Path>> resultMap = getMapFromMatcher();
        assertEquals(1, resultMap.keySet().size());
        assertNotNull(resultMap.get("a.txt"));
        assertEquals(2, resultMap.get("a.txt").size());
        assertEquals(pathMocks.get(0), resultMap.get("a.txt").get(0));
        assertEquals(pathMocks.get(1), resultMap.get("a.txt").get(1));
    }

    @Test
    public void testFileNamesDoNotMatch() {
        matcher.add(pathMocks.get(0)); // a.txt
        matcher.add(pathMocks.get(3)); // b.txt

        matcher.preProcess();

        Map<String, List<Path>> resultMap = getMapFromMatcher();
        assertEquals(0, resultMap.keySet().size());
    }

    @Test
    public void testComplex() {
        pathMocks.forEach(path -> matcher.add(path));
        matcher.preProcess();

        Map<String, List<Path>> resultMap = getMapFromMatcher();
        assertEquals(2, resultMap.keySet().size());
        assertNotNull(resultMap.get("a.txt"));
        assertNotNull(resultMap.get("b.txt"));
        assertEquals(3, resultMap.get("a.txt").size());
        assertEquals(3, resultMap.get("b.txt").size());
        assertEquals(pathMocks.get(0), resultMap.get("a.txt").get(0));
        assertEquals(pathMocks.get(1), resultMap.get("a.txt").get(1));
        assertEquals(pathMocks.get(2), resultMap.get("a.txt").get(2));
        assertEquals(pathMocks.get(3), resultMap.get("b.txt").get(0));
        assertEquals(pathMocks.get(4), resultMap.get("b.txt").get(1));
        assertEquals(pathMocks.get(5), resultMap.get("b.txt").get(2));
    }

    private Map<String, List<Path>> getMapFromMatcher() {
        return ((FileNameMatcher) matcher).matchingFileNames;
    }
}
