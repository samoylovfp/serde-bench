# gson benchmark

## benchmark
```bash
benchmark-gson> mvn clean package -DskipTests && C:\Users\cboudereau\jdk-21\bin\java.exe -cp .\target\uber-benchmark-gson-0.0.1-SNAPSHOT.jar com.jsonbench.Main -prof jfr -f 1 -bm AverageTime -tu ms
```

## console benchmark
```bash
benchmark-gson> mvn clean package -DskipTests && C:\Users\cboudereau\jdk-21\bin\java.exe -cp .\target\uber-benchmark-gson-0.0.1-SNAPSHOT.jar com.jsonbench.Main --console --times 10
```

