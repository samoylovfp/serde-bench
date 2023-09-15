use serde::de::{Deserializer, SeqAccess, Visitor};

use serde_json;
use std::fs::File;
use std::io::BufReader;
use std::path::PathBuf;
use std::sync::mpsc::{sync_channel, Receiver, SyncSender};
use std::{fmt, thread};

use crate::Json;

type DeserializeResult = Result<Json, String>;

pub struct JsonIterator {
    receiver: Receiver<DeserializeResult>,
}

struct JsonVisitor {
    sender: SyncSender<DeserializeResult>,
}

impl Iterator for JsonIterator {
    type Item = DeserializeResult;

    fn next(&mut self) -> Option<Self::Item> {
        self.receiver.recv().ok() //ok() because a RecvError implies we are done
    }
}

impl JsonIterator {
    pub fn new(path: PathBuf) -> Self {
        let (sender, receiver) = sync_channel::<DeserializeResult>(0);

        thread::spawn(move || {
            let mut data: Vec<u8> = std::fs::read_to_string(path).unwrap().bytes().collect();
            let mut deserializer = simd_json::Deserializer::from_slice(&mut data).unwrap();
            let result = deserializer.deserialize_seq(JsonVisitor {
                sender: sender.clone(),
            });
            if let Err(e) = result {
                _ = sender.send(Err(e.to_string()));
            }
        });

        Self { receiver }
    }
}

impl<'de> Visitor<'de> for JsonVisitor {
    type Value = ();

    fn expecting(&self, formatter: &mut fmt::Formatter) -> fmt::Result {
        formatter.write_str("array of Json")
    }

    fn visit_seq<A>(self, mut seq: A) -> Result<Self::Value, A::Error>
    where
        A: SeqAccess<'de>,
    {
        while let Some(val) = seq.next_element::<Json>()? {
            if self.sender.send(Ok(val)).is_err() {
                break; //receiver has disconnected.
            }
        }
        Ok(())
    }
}
