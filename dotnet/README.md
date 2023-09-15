# json benchmark

## install benchmark template
```bash
dotnet new install BenchmarkDotNet.Templates
```

## setup benchmark
```bash
dotnet new benchmark -lang F#
```

## run benchmark
```bash
dotnet/jsonbench> dotnet run -c Release
```

## run in console mode
```bash
dotnet run -c Release -- --console --times 10
```