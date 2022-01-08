package org.apache.poi.examples.hsmf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import picocli.CommandLine;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Msg2txtTest {
    /**
     * Test resource directory.
     * Files from https://svn.apache.org/viewvc/poi/trunk/test-data/hsmf/
     */
    private static final String RESOURCES = "src/test/resources/";

    @BeforeEach
    @AfterEach
    public void cleanup() throws IOException {
        PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.msg");

        try (Stream<Path> stream = Files.walk(Paths.get(RESOURCES))) {
            stream.skip(1).filter(p -> !matcher.matches(p)).sorted(Comparator.reverseOrder()).forEach(p -> {
                try {
                    Files.delete(p);
                } catch (IOException e) {
                    throw new UncheckedIOException(e);
                }
            });
        }
    }

    @Test
    public void extract() {
        int exitCode = run(RESOURCES + "example_received_unicode.msg");

        assertEquals(0, exitCode);
        assertTrue(Files.isRegularFile(Path.of(RESOURCES, "example_received_unicode.txt")));
        assertTrue(Files.isDirectory(Path.of(RESOURCES, "example_received_unicode-att")));
        assertTrue(Files.isRegularFile(Path.of(RESOURCES, "example_received_unicode-att", "alfresco.gif")));
    }

    @Test
    public void multiple() {
        int exitCode = run(RESOURCES + "simple_test_msg_1.msg", RESOURCES + "simple_test_msg_2.msg");

        assertEquals(0, exitCode);
        assertTrue(Files.isRegularFile(Path.of(RESOURCES, "simple_test_msg_1.txt")));
        assertTrue(Files.isRegularFile(Path.of(RESOURCES, "simple_test_msg_2.txt")));
    }

    private int run(String... args) {
        return new CommandLine(new Msg2txt()).execute(args);
    }
}
