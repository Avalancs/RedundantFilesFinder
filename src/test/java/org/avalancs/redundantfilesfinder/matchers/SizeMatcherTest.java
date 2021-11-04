package org.avalancs.redundantfilesfinder.matchers;


import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.nio.file.Path;
import java.util.List;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;


public class SizeMatcherTest extends MatchersTest {

    @BeforeEach
    public void setUp() {
        matcher = createMatcherSpy(SizeMatcher.class);
    }

    @Test
    public void testSizesMatch() {
        matcher.add(pathMocks.get(0)); // a.txt : 1 byte
        matcher.add(pathMocks.get(1)); // subfolder/a.txt : 1 byte
        matcher.add(pathMocks.get(2)); // subfolder/subfolder/a.txt : 2 bytes

        matcher.preProcess();

        Map<Long, List<Path>> resultMap = getMapFromMatcher();
        assertEquals(1, resultMap.keySet().size());
        assertNotNull(resultMap.get(1L));
        assertEquals(2, resultMap.get(1L).size());
        assertEquals(pathMocks.get(0), resultMap.get(1L).get(0));
        assertEquals(pathMocks.get(1), resultMap.get(1L).get(1));
    }

    @Test
    public void testSizesDoNotMatch() {
        matcher.add(pathMocks.get(0)); // a.txt : 1 bytes
        matcher.add(pathMocks.get(4)); // b.txt : 2 bytes

        matcher.preProcess();

        Map<Long, List<Path>> resultMap = getMapFromMatcher();
        assertEquals(0, resultMap.keySet().size());
    }

    @Test
    public void testComplex() {
        pathMocks.forEach(path -> matcher.add(path));
        matcher.preProcess();

        Map<Long, List<Path>> resultMap = getMapFromMatcher();
        assertEquals(3, resultMap.keySet().size());
        assertNotNull(resultMap.get(1L));
        assertNotNull(resultMap.get(2L));
        assertNotNull(resultMap.get(3L));
        assertEquals(2, resultMap.get(1L).size());
        assertEquals(2, resultMap.get(2L).size());
        assertEquals(pathMocks.get(0), resultMap.get(1L).get(0));
        assertEquals(pathMocks.get(1), resultMap.get(1L).get(1));
        assertEquals(pathMocks.get(2), resultMap.get(2L).get(0));
        assertEquals(pathMocks.get(3), resultMap.get(2L).get(1));
        assertEquals(pathMocks.get(4), resultMap.get(3L).get(0));
        assertEquals(pathMocks.get(5), resultMap.get(3L).get(1));
    }

    private Map<Long, List<Path>> getMapFromMatcher() {
        return ((SizeMatcher) matcher).matchingSizes;
    }
}
