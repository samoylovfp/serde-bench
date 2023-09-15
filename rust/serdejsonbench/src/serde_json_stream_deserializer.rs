use serde::de::{Deserializer, SeqAccess, Visitor};

use serde_json;
use std::fs::File;
use std::io::BufReader;
use std::path::PathBuf;
use std::sync::mpsc::{sync_channel, Receiver, SyncSender};
use std::{fmt, thread};

use crate::Json;

type DeserializeResult<'d> = Result<Json<'d>, String>;

pub struct JsonIterator<'d> {
    receiver: Receiver<DeserializeResult<'d>>,
}

struct JsonVisitor<'d> {
    sender: SyncSender<DeserializeResult<'d>>,
}

impl<'d> Iterator for JsonIterator<'d> {
    type Item = DeserializeResult<'d>;

    fn next(&mut self) -> Option<Self::Item> {
        self.receiver.recv().ok() //ok() because a RecvError implies we are done
    }
}

impl JsonIterator<'static> {
    pub fn new(path: PathBuf) -> Self {
        let (sender, receiver) = sync_channel::<DeserializeResult>(0);

        thread::spawn(move || {
            let reader = BufReader::with_capacity(8192, File::open(path).unwrap()); //in real scenario may want to send error, instead of unwrapping
            let mut deserializer = serde_json::Deserializer::from_reader(reader);
            if let Err(e) = deserializer.deserialize_seq(JsonVisitor {
                sender: sender.clone(),
            }) {
                let _ = sender.send(Err(e.to_string())); //let _ = because error from calling send just means receiver has disconnected
            }
        });

        Self { receiver }
    }
}

impl<'de> Visitor<'de> for JsonVisitor<'de> {
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
