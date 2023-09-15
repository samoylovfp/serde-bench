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

## tracing
```bash
dotnet build -c Release && dotnet trace collect -- .\bin\Release\net8.0\jsonbench.exe --console --times 10
```