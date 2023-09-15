//https://github.com/serde-rs/json/issues/404#issuecomment-674293399

use serdejsonbench::JsonIterator;

fn main() -> std::io::Result<()> {
    let iter = JsonIterator::new(
        r#"../../json/256MB.json"#.into(),
    );
    let mut count = 0;
    for json in iter {
        let json = json.unwrap();
        assert_eq!("FULL", json.delta_mode);
        count = count + 1;
        if count % 1000 == 0 {
            println!("{count}");
        }
    }

    assert_eq!(68495, count);
    println!("done");
    Ok(())
}
