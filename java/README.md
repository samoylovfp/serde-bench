# json bench

## project setup

### maven

#### install maven locally
Download and install maven from the official download page

#### create project dir
```bash
mkdir new-project
```

create maven pom parent and module

#### init wrapper in the project
https://maven.apache.org/wrapper/
```bash
mvn wrapper:wrapper
```

## json apis
- [x] Gson from Google : https://github.com/google/gson
- [x] Jackson databind
- [x] Jackson parser
- [ ] Openapi based (with and without http call)
- [ ] Any buffer pool based (see constructors for deserialization and serialization)

## benchmark

1 JMH Fork, skipping benchmark unit test.

use -h to see other available arguments of jmh