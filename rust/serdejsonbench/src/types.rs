use serde::Deserialize;

#[derive(Deserialize, Debug)]
#[serde(rename_all = "camelCase")]
pub struct Json<'d> {
    pub bulk: &'d str,
    pub delta_mode: &'d str
}
