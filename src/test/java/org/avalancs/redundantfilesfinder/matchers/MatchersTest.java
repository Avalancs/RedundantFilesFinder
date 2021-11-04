package org.avalancs.redundantfilesfinder.matchers;

import java.nio.file.Path;
import java.util.Arrays;
import java.util.List;

import static org.mockito.Mockito.*;

public abstract class MatchersTest {
    protected Matcher matcher;

    // Mocks given as input to matchers
    protected final List<Path> pathMocks;

    // the names of the mocked files
    protected final List<String> names;

    // The sizes of the mocked files
    protected final List<Long> sizes;

    public MatchersTest() {
        /*
            These are just debug names, but indicate the usage.
            The same letter means the files are identical in name and the number means they are identical in size
            And for content equality lets say that a1 content equals with subfolder/a1 but b2 and subfolder/b2 only equals in size
         */
        pathMocks = Arrays.asList(
                mock(Path.class, "a1"),
                mock(Path.class, "subfolder/a1"),
                mock(Path.class, "subfolder/subfolder/a2"),
                mock(Path.class, "b2"),
                mock(Path.class, "subfolder/b2"),
                mock(Path.class, "subfolder/subfolder/b2")
        );

        names = Arrays.asList(
            "a.txt",
            "a.txt",
            "a.txt",
            "b.txt",
            "b.txt",
            "b.txt"
        );

        sizes = Arrays.asList(
            1L,
            1L,
            2L,
            2L,
            3L,
            3L
        );
    }

    protected Matcher createMatcherSpy(Class<? extends Matcher> matcherToMock) {
        try {
            Matcher matcherSpy = spy(matcherToMock.getConstructor().newInstance());
            for(int i = 0; i<pathMocks.size(); i++) {
                doReturn(names.get(i)).when(matcherSpy).getFileName(pathMocks.get(i));
                doReturn(sizes.get(i)).when(matcherSpy).getFileSize(pathMocks.get(i));
            }

            // the most recent doReturn takes precedence over older ones, so the specific cases should be at the bottom
            doReturn(false).when(matcherSpy).fileContentsEqual(any(), any());
            doReturn(true).when(matcherSpy).fileContentsEqual(pathMocks.get(0), pathMocks.get(1));
            return matcherSpy;
        } catch(Exception e) {
            throw new RuntimeException("Could not instantiate Matcher spy!", e);
        }
    }
}
