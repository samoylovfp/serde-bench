# json serialization/deserializaton benchmark

json serialization and deserialization benchmark

- [x] deserialization
 - [x] java
 - [x] dotnet
 - [x] rust

- [ ] serialization

## setup
```bash
./uncompress.sh
```

## prepare json files
a basic small.json file (from https://json.org/example.html) is used to create samples
### generate json files
```
fsi ./setup.fsx
```

### create the setup files
```bash
./compress.sh
```