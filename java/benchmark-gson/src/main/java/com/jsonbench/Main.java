package com.jsonbench;

import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.Iterator;
import java.util.NoSuchElementException;

import org.openjdk.jmh.annotations.Benchmark;
import com.google.gson.Gson;
import com.google.gson.stream.JsonReader;

public class Main {
    static Gson gson = new Gson();

    public static class Json {
        public String bulk;
        public String deltaMode;
        public String dateStartBulk;
        public String dateEndBulk;
    }

    private static void assertInvariant(final Json p) {
        if (!p.deltaMode.equals("FULL")) {
            throw new RuntimeException("bad json");
        }
    }

    @Benchmark
    public void smallJson() throws Exception {
        assertInvariant(getSmall(Main.gson));
    }

    @Benchmark
    public void mediumJson() throws Exception {
        for (final var x : getMedium(Main.gson)) {
            assertInvariant(x);
        }
    }

    @Benchmark
    public void largeJson() throws Exception {
        for (final var x : getLarge(Main.gson)) {
            assertInvariant(x);
        }
    }

    @Benchmark
    public void largeLazyJson() throws Exception {
        var count = 0;
        try (final var iterator = getLazyLarge(Main.gson)) {
            while (iterator.hasNext()) {
                assertInvariant(iterator.next());
                count++;
            }
        }
        if (count != 143702) {
            throw new RuntimeException("bad count");
        }
    }

    private static Json deserialize(final Gson gson, final String filePath) throws FileNotFoundException {
        final var reader = new JsonReader(new FileReader(filePath));
        return gson.fromJson(reader, Json.class);
    }

    static Json getSmall(final Gson gson) throws FileNotFoundException {
        return deserialize(gson, "../../json/small.json");
    }

    public static Json[] getMedium(final Gson gson) throws FileNotFoundException {
        final var reader = new JsonReader(new FileReader("../../json/256KB.json"));
        return gson.fromJson(reader, Json[].class);
    }

    public static Json[] getLarge(final Gson gson) throws FileNotFoundException {
        final var reader = new JsonReader(new FileReader("../../json/256MB.json"));
        return gson.fromJson(reader, Json[].class);
    }

    public static interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {
    }

    private static CloseableIterator<Json> deserializeLazy(final Gson gson, final String pathName)
            throws IOException {
        return new CloseableIterator<Json>() {
            private final JsonReader reader = new JsonReader(new FileReader(pathName));

            private Json current;

            {
                reader.beginArray();
                current = getNext();
            }

            private Json getNext() throws IOException {
                if (!reader.hasNext()) {
                    reader.endArray();
                    return null;
                }
                return gson.fromJson(reader, Json.class);
            }

            @Override
            public boolean hasNext() {
                return current != null;
            }

            @Override
            public Json next() {
                var entry = current;
                try {
                    current = getNext();
                } catch (IOException e) {
                    throw new NoSuchElementException(e);
                }
                return entry;
            }

            @Override
            public void close() throws IOException {
                reader.close();
            }
        };
    }

    static CloseableIterator<Json> getLazyLarge(final Gson gson) throws IOException {
        return deserializeLazy(gson, "../../json/256MB.json");
    }

    public static void main(final String[] args) throws Exception {
        if (args.length == 3 && args[0].equals("--console") && args[1].equals("--times")) {
            var times = Integer.parseInt(args[2]);
            var main = new Main();
            var start = System.currentTimeMillis();
            for (var i = 0; i < times; i++) {
                System.out.print(".");
                main.largeLazyJson();
            }
            var end = System.currentTimeMillis();
            var elapsed = end - start;
            var avg = elapsed / times;
            System.out.printf("done in %dms avg %dms%n", elapsed, avg);
        } else {
            org.openjdk.jmh.Main.main(args);
        }
    }
}
