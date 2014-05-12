#!/bin/bash -eux

ant

root="$(dirname "$0")"
app="$root/Cronometer.app"
rm -rf "$app"
mkdir "$app"
cp -r "$root/release/osx/Contents" "$app"

mkdir -p "$app/Contents/Resources/Java"
cp -r "$root/lib"/* "$app/Contents/Resources/Java/"
