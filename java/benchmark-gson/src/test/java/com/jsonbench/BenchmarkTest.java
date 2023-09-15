package com.jsonbench;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNull;

import java.util.concurrent.TimeUnit;

import org.junit.jupiter.api.Test;
import org.openjdk.jmh.annotations.Benchmark;
import org.openjdk.jmh.annotations.Mode;
import org.openjdk.jmh.runner.Runner;
import org.openjdk.jmh.runner.options.OptionsBuilder;
import org.openjdk.jmh.runner.options.TimeValue;

import com.jsonbench.Main.Json;

public class BenchmarkTest {

    private static void assertInvariant(final Json p) {
        assertEquals("FULL", p.deltaMode);
        if (p.dateStartBulk != null) {
            assertEquals("2023-09-06", p.dateStartBulk);
        }
        assertNull(p.dateEndBulk);
        assertEquals("false", p.bulk);
    }

    @Test
    public void runBenchmarks() throws Exception {
        var options = new OptionsBuilder()
                .include(this.getClass().getName() + ".*")
                .mode(Mode.AverageTime)
                .timeUnit(TimeUnit.MILLISECONDS)
                .warmupTime(TimeValue.seconds(1))
                .warmupIterations(3)
                .threads(1)
                .measurementIterations(5)
                .forks(1)
                .shouldFailOnError(true)
                .shouldDoGC(true)
                .build();

        new Runner(options).run();
    }

    @Test
    @Benchmark
    public void simpleJsonTest() throws Exception {
        assertInvariant(Main.getSmall(Main.gson));
    }

    @Test
    @Benchmark
    public void mediumJsonTest() throws Exception {
        var count = 0;
        for (final var x : Main.getMedium(Main.gson)) {
            assertInvariant(x);
            count++;
        }

        if (count != 140) {
			throw new RuntimeException("bad count");
		}
    }

    @Test
    @Benchmark
    public void largeJsonTest() throws Exception {
        var count = 0;
        for (final var x : Main.getLarge(Main.gson)) {
            assertInvariant(x);
            count++;
        }

		if (count != 143702) {
			throw new RuntimeException("bad count");
		}
    }

    @Test
    @Benchmark
    public void largeLazyJsonTest() throws Exception {
        var count = 0;
        try (final var iterator = Main.getLazyLarge(Main.gson)) {
            while (iterator.hasNext()) {
                assertInvariant(iterator.next());
                count++;
            }
        }
        assertEquals(143702, count);
    }
}
