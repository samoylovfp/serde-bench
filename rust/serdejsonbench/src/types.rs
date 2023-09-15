use serde::Deserialize;

#[derive(Deserialize, Debug)]
#[serde(rename_all = "camelCase")]
pub struct Json {
    pub bulk: String,
    pub delta_mode: String,
}
