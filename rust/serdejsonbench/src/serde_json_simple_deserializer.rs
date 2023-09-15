use std::{error::Error, path::Path};

use crate::Json;

pub fn read_from_file<'d, P: AsRef<Path>>(path: P) -> Result<Vec<Json<'d>>, Box<dyn Error>> {
    let contents = Box::new(std::fs::read_to_string(path)?);
     // FIXME: hold on to the data instead of leaking it
    Ok(serde_json::from_str(Box::leak(contents))?)
}

pub fn readone_from_file<'d, P: AsRef<Path>>(path: P) -> Result<Json<'d>, Box<dyn Error>> {
    let contents = Box::new(std::fs::read_to_string(path)?);
    // FIXME: hold on to the data instead of leaking it
    Ok(serde_json::from_str(Box::leak(contents))?)
}
