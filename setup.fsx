type [<Measure>] Byte
type [<Measure>] MB
type [<Measure>] KB

let generateJsonArray data times path = 
    use f = System.IO.File.OpenWrite(path)

    f.WriteByte('['B)

    for i in 1L .. times do
        f.Write(data:byte[])
        if i < times then f.WriteByte(','B)
    
    f.WriteByte(']'B)

let generateNdjson data times path = 
    use f = System.IO.File.OpenWrite(path)

    for i in 1L .. times do
        f.Write(data:byte[])
        if i < (times) then f.WriteByte('\n'B)
    
let mb = 1024L * 1024L<Byte/MB>  
let kb = 1024L<Byte/KB>  

let bytes (x:int) = int64 x * 1L<Byte>

let data = System.IO.File.ReadAllBytes("small.json")

let length = data.Length |> bytes
System.IO.Directory.CreateDirectory("./json")
"json/256MB.ndjson" |> generateNdjson data (256L<MB> * mb / length)

"json/256MB.json" |> generateJsonArray data (256L<MB> * mb / length)
"json/256KB.json" |> generateJsonArray data (256L<KB> * kb / length)

System.IO.File.Copy("small.json", "json/small.json")