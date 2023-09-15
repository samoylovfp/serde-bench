package com.jsonbench;

import java.io.File;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;
import java.util.NoSuchElementException;
import org.openjdk.jmh.annotations.Benchmark;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;
import com.fasterxml.jackson.core.JsonParser;
import com.fasterxml.jackson.core.JsonToken;
import com.fasterxml.jackson.core.exc.StreamReadException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.DatabindException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.fasterxml.jackson.databind.json.JsonMapper;

public class Main {
	static final ObjectMapper mapper = JsonMapper.builder()
			// .addModule(new Jdk8Module())
			// .addModule(new JavaTimeModule())
			.build();

	@JsonIgnoreProperties(ignoreUnknown = true)
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
		assertInvariant(getSmall(mapper));
	}

	@Benchmark
	public void mediumJson() throws Exception {
		var count = 0;
		try (final var iterator = getLazyMedium(mapper)) {
			while (iterator.hasNext()) {
				assertInvariant(iterator.next());
				count++;
			}
		}
		if (count != 143702) {
			throw new RuntimeException("bad count");
		}
	}

	@Benchmark
	public void largeJson() throws Exception {
		for (final var x : getLarge(mapper)) {
			assertInvariant(x);
		}
	}

	@Benchmark
	public void largeLazyJson() throws Exception {
		var count = 0;
		try (final var iterator = getLazyLarge(mapper)) {
			while (iterator.hasNext()) {
				assertInvariant(iterator.next());
				count++;
			}
		}
		if (count != 143702) {
			throw new RuntimeException("bad count");
		}
	}

	static List<Json> getLarge(final ObjectMapper mapper)
			throws IOException, StreamReadException, DatabindException {
		return deserializeList(mapper, "../../json/256MB.json");
	}

	static CloseableIterator<Json> getLazyLarge(final ObjectMapper mapper)
			throws IOException, StreamReadException, DatabindException {
		return deserializeLazy(mapper, "../../json/256MB.json");
	}

	static CloseableIterator<Json> getLazyMedium(final ObjectMapper mapper)
			throws IOException, StreamReadException, DatabindException {
		return deserializeLazy(mapper, "../../json/256KB.json");
	}

	static Json getSmall(final ObjectMapper mapper) throws IOException, StreamReadException, DatabindException {
		return deserialize(mapper, "../../json/small.json");
	}

	private static List<Json> deserializeList(final ObjectMapper mapper, final String pathname)
			throws StreamReadException, DatabindException, IOException {
		final var file = new File(pathname);
		return mapper.readValue(file, new TypeReference<List<Json>>() {
		});
	}

	public static interface CloseableIterator<T> extends Iterator<T>, AutoCloseable {
	}

	private static CloseableIterator<Json> deserializeLazy(final ObjectMapper mapper, final String pathName)
			throws IOException {
		return new CloseableIterator<Json>() {
			private final JsonParser jsonParser = mapper.getFactory().createParser(new File(pathName));
			private Json current;

			{
				if (jsonParser.nextToken() != JsonToken.START_ARRAY) {
					throw new IllegalStateException("Expected content to be an array");
				}
				current = getNext();
			}

			private Json getNext() throws IOException {
				if (jsonParser.nextToken() == JsonToken.END_ARRAY) {
					return null;
				}
				return mapper.readValue(jsonParser, Json.class);
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
				jsonParser.close();
			}
		};
	}

	private static Json deserialize(final ObjectMapper mapper, final String pathname)
			throws IOException, StreamReadException, DatabindException {
		final var file = new File(pathname);
		return mapper.readValue(file, Json.class);
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
