use serdejsonbench::JsonIterator;
use std::{io::Write, time::Instant};

fn parse() {
    let iter = JsonIterator::new(r#"../../json/256MB.json"#.into());
    let mut count = 0;
    for json in iter {
        let json = json.unwrap();
        assert_eq!("FULL", json.delta_mode);
        count = count + 1;
    }
    assert_eq!(68495, count);
}

fn main() {
    let args: Vec<String> = std::env::args().skip(1).collect();

    if args.len() == 3 && args[0] == "--console" && args[1] == "--times" {
        let now = Instant::now();
        let times: u32 = args[2].as_str().parse().unwrap();

        for _ in 0..times {
            print!(".");
            std::io::stdout().flush().unwrap();
            parse();
        }

        let elapsed = now.elapsed().as_millis();
        let avg = elapsed / u128::from(times);
        println!("done in {elapsed}ms avg {avg}ms");
    } else {
        panic!("bad arguments {args:?}")
    }

    println!("done");
}
