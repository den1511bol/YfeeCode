#!/bin/sh
# YfeeCode — developer: @yfeeeee
# Compile and run one C# file offline with Roslyn directly (no MSBuild, no NuGet): works under proot.
f="$1"
[ -f "$f" ] || { echo "Нет файла: $f"; exit 1; }
export DOTNET_CLI_TELEMETRY_OPTOUT=1 DOTNET_NOLOGO=1 DOTNET_SYSTEM_GLOBALIZATION_INVARIANT=1
export DOTNET_EnableWriteXorExecute=0 DOTNET_TieredPGO=0 DOTNET_gcServer=0 DOTNET_GCHeapHardLimit=0x20000000
d=/tmp/yfeecs
rm -rf "$d"; mkdir -p "$d" || exit 1
csc=$(find /usr/lib/dotnet/sdk /usr/share/dotnet/sdk /usr/lib/dotnet8/sdk -name csc.dll -path '*bincore*' 2>/dev/null | head -1)
ref=$(find /usr/lib/dotnet /usr/share/dotnet -type d -path '*Microsoft.NETCore.App.Ref*/ref/net8.0' 2>/dev/null | head -1)
rt=$(find /usr/lib/dotnet /usr/share/dotnet -maxdepth 4 -type d -path '*shared/Microsoft.NETCore.App/8*' 2>/dev/null | head -1)
[ -n "$csc" ] || { echo "Не найден компилятор C# (csc.dll). Выполни: linux dotnet"; exit 1; }
[ -n "$ref" ] || { echo "Не найдены справочные сборки .NET (Microsoft.NETCore.App.Ref). Выполни: lx apk add dotnet8-targeting-pack"; exit 1; }
[ -n "$rt" ] || { echo "Не найден runtime .NET 8"; exit 1; }
ver=$(basename "$rt")
cp "$f" "$d/Program.cs"
printf 'global using System;\nglobal using System.IO;\nglobal using System.Linq;\nglobal using System.Collections.Generic;\nglobal using System.Threading;\nglobal using System.Threading.Tasks;\n' > "$d/Globals.cs"
refs=""
for r in "$ref"/*.dll; do refs="$refs -r:$r"; done
# shellcheck disable=SC2086
dotnet "$csc" -nologo -nowarn:CS8933,CS8019 -langversion:latest -target:exe -out:"$d/app.dll" $refs "$d/Program.cs" "$d/Globals.cs" || exit 1
printf '{"runtimeOptions":{"tfm":"net8.0","framework":{"name":"Microsoft.NETCore.App","version":"%s"}}}' "$ver" > "$d/app.runtimeconfig.json"
cd "$(dirname "$f")" 2>/dev/null
exec dotnet "$d/app.dll"
