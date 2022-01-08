package org.apache.poi.examples.hsmf;

import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.ValueSource;
import picocli.CommandLine;

import java.io.IOException;
import java.io.UncheckedIOException;
import java.nio.file.*;
import java.util.Comparator;
import java.util.function.Consumer;
import java.util.stream.Stream;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertTrue;

public class Msg2txtTest {
    /**
     * Test resource directory.
     * Files from https://svn.apache.org/viewvc/poi/trunk/test-data/hsmf/
     */
    private static final String RESOURCES = "src/test/resources/";

    private Path temp;

    @BeforeEach
    public void setup() throws IOException {
        temp = Files.createTempDirectory("msg2txt");
    }

    @AfterEach
    public void cleanup() throws IOException {
        Consumer<Path> erase = p -> {
            try {
                Files.delete(p);
            } catch (IOException e) {
                throw new UncheckedIOException(e);
            }
        };

        try (Stream<Path> stream = Files.walk(Paths.get(RESOURCES))) {
            PathMatcher matcher = FileSystems.getDefault().getPathMatcher("glob:**.msg");
            stream.skip(1).filter(p -> !matcher.matches(p)).sorted(Comparator.reverseOrder()).forEach(erase);
        }

        try (Stream<Path> stream = Files.walk(temp)) {
            stream.sorted(Comparator.reverseOrder()).forEach(erase);
        }
    }

    @Test
    public void extract() {
        int exitCode = run(RESOURCES + "example_received_unicode.msg");

        assertEquals(0, exitCode);
        assertTrue(Files.isRegularFile(Paths.get(RESOURCES, "example_received_unicode.txt")));
        assertTrue(Files.isDirectory(Paths.get(RESOURCES, "example_received_unicode-att")));
        assertTrue(Files.isRegularFile(Paths.get(RESOURCES, "example_received_unicode-att", "alfresco.gif")));
    }

    @Test
    public void multiple() {
        int exitCode = run(RESOURCES + "simple_test_msg_1.msg", RESOURCES + "simple_test_msg_2.msg");

        assertEquals(0, exitCode);
        assertTrue(Files.isRegularFile(Paths.get(RESOURCES, "simple_test_msg_1.txt")));
        assertTrue(Files.isRegularFile(Paths.get(RESOURCES, "simple_test_msg_2.txt")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-o", "--output", "/o"})
    public void outputDir(String arg) {
        int exitCode = run(arg, temp.toString(), RESOURCES + "example_received_unicode.msg");

        assertEquals(0, exitCode);
        assertTrue(Files.isRegularFile(temp.resolve("example_received_unicode.txt")));
        assertTrue(Files.isDirectory(temp.resolve("example_received_unicode-att")));
        assertTrue(Files.isRegularFile(temp.resolve(Paths.get("example_received_unicode-att", "alfresco.gif"))));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-t", "--text-only", "/t"})
    public void textOnly(String arg) {
        int exitCode = run(arg, RESOURCES + "example_received_unicode.msg");

        assertEquals(0, exitCode);
        assertTrue(Files.isRegularFile(Paths.get(RESOURCES, "example_received_unicode.txt")));
        assertTrue(Files.notExists(Paths.get(RESOURCES, "example_received_unicode-att")));
    }

    @ParameterizedTest
    @ValueSource(strings = {"-h", "--help", "/?"})
    public void help(String arg) {
        int exitCode = run(arg, RESOURCES + "example_received_unicode.msg");

        assertEquals(0, exitCode);
        assertTrue(Files.notExists(Paths.get(RESOURCES, "example_received_unicode.txt")));
        assertTrue(Files.notExists(Paths.get(RESOURCES, "example_received_unicode-att")));
    }

    @Test
    public void missingFileArgument() {
        int exitCode = run();

        assertEquals(2, exitCode);
    }

    private int run(String... args) {
        return new CommandLine(new Msg2txt()).execute(args);
    }
}
