use std::{error::Error, fs::File, io::BufReader, path::Path};

use crate::Json;

pub fn read_from_file<P: AsRef<Path>>(path: P) -> Result<Vec<Json>, Box<dyn Error>> {
    let file = File::open(path)?;
    let reader = BufReader::new(file);

    Ok(simd_json::from_reader(reader)?)
}

pub fn readone_from_file<P: AsRef<Path>>(path: P) -> Result<Json, Box<dyn Error>> {
    let file = File::open(path)?;
    let reader = BufReader::new(file);

    Ok(simd_json::from_reader(reader)?)
}
