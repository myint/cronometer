#!/bin/bash -ex

root="$(dirname "$0")"

for jar in "$root/lib"/*.jar
do
    export CLASSPATH="$root/$jar:$CLASSPATH"
done

ant

app="$root/CRONoMeter.app"
rm -rf "$app"
mkdir "$app"
cp -r "$root/release/osx/Contents" "$app"

mkdir -p "$app/Contents/Resources/Java"
cp -r "$root/lib"/* "$app/Contents/Resources/Java/"
