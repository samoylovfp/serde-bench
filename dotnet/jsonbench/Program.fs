open BenchmarkDotNet.Running
open jsonbench

let (|Int32|_|) x = 
    match System.Int32.TryParse(x:string) with
    | true, x -> Some x
    | false, _ -> None

[<EntryPoint>]
let main argv =
    match argv with
    | [|"--console"; "--times"; Int32 times|] -> 
        printfn "running in console mode"
        let stopwatch = System.Diagnostics.Stopwatch.StartNew()
        for i = 1 to times do
            printf "."
            let actual = deserialize """../../json/256MB.json"""
            let length = actual |> Seq.length
            if length <> 68495 then failwithf "bad count (%i)" length
        stopwatch.Stop()
        let elapsed = stopwatch.Elapsed;
        let ms = elapsed.TotalMilliseconds;
        let stat = ms / float times;
        printfn "done in %.2fms / avg %.2fms" ms stat
    | _ -> BenchmarkRunner.Run<Benchmarks>() |> ignore
    0 // return an integer exit code