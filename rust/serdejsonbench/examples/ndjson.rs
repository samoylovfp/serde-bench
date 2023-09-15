use std::fs::File;

use serde_json::Deserializer;
use serdejsonbench::Json;

//TODO: This example is extremely slow !
fn main() {
    let file = File::open(r#"../../json/256MB.ndjson"#).unwrap();
    let iter: serde_json::StreamDeserializer<'_, serde_json::de::IoRead<File>, Json> =
        Deserializer::from_reader(file).into_iter::<Json>();

    let mut count = 0;
    for json in iter {
        let json = json.unwrap();
        assert_eq!("FULL", json.delta_mode);
        assert_eq!("false", json.bulk);
        count = count + 1;
        if count % 1000 == 0 {
            println!("{count}");
        }
    }

    assert_eq!(68495, count);
}
