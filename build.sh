#!/bin/sh
# Builds the program into build/drawing.jar.
# Run "./build.sh test" to also compile and run the tests.
set -e
cd "$(dirname "$0")"
OUT=${OUT:-build}

rm -rf "$OUT/classes" "$OUT/test-classes"
mkdir -p "$OUT/classes" "$OUT/test-classes"
javac -Xlint:all -Werror -d "$OUT/classes" src/drawing/*.java
jar --create --file "$OUT/drawing.jar" --main-class drawing.App -C "$OUT/classes" .

if [ "$1" = "test" ]; then
    javac -Xlint:all -Werror -cp "$OUT/classes" -d "$OUT/test-classes" test/drawing/*.java
    java -Djava.awt.headless=true -cp "$OUT/classes:$OUT/test-classes" drawing.Tests
fi
