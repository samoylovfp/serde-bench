use std::time::Duration;

use criterion::{criterion_group, criterion_main, Criterion};

use serdejsonbench::{read_from_file, readone_from_file, JsonIterator};

fn json_simple_deserializer_small(c: &mut Criterion) {
    c.bench_function("json simple deserializer small", |b| {
        b.iter(|| {
            let data = readone_from_file(r#"../../json/small.json"#).unwrap();
            assert_eq!("FULL", data.delta_mode);
        })
    });
}

fn json_array_simple_deserializer_large(c: &mut Criterion) {
    c.bench_function("json simple deserializer large", |b| {
        b.iter(|| {
            let data = read_from_file(r#"../../json/256MB.json"#).unwrap();

            assert_eq!(68495, data.len());
        })
    });
}

fn json_stream_deserializer_large(c: &mut Criterion) {
    c.bench_function("json stream deserializer large", |b| {
        b.iter(|| {
            let iter = JsonIterator::new(r#"../../json/256MB.json"#.into());
            let mut count = 0;
            for json in iter {
                let json = json.unwrap();
                assert_eq!("FULL", json.delta_mode);
                assert_eq!("false", json.bulk);
                count = count + 1;
            }

            assert_eq!(68495, count);
        })
    });
}

criterion_group! {
    name = benches;
    config = Criterion::default().measurement_time(Duration::from_secs(40)).sample_size(10);
    targets =
    json_simple_deserializer_small,

    json_stream_deserializer_large,
    json_array_simple_deserializer_large,
}
criterion_main!(benches);
