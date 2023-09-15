module jsonbench 

open BenchmarkDotNet.Attributes

type [<CLIMutable; Struct>] Json = {
    bulk: string
    deltaMode: string
    dateStartBulk: string
    dateEndBulk: string
}

let deserialize jsonPath : Json seq = 
    seq {
        use file = System.IO.File.OpenRead(jsonPath)
        yield! System.Text.Json.JsonSerializer.Deserialize(file)
    }

type Benchmarks () =
    [<Benchmark>]
    member __.Json256MB () =
        let actual = deserialize """../../../../../../../../../json/256MB.json"""
        let length = actual |> Seq.length
        if length <> 68495 then failwithf "bad count (%i)" length