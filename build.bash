#!/bin/bash -ex

root="$(dirname "$0")"
export CLASSPATH="$root/lib/jfreechart-1.0.6.jar:$CLASSPATH"
export CLASSPATH="$root/lib/jcommon-1.0.10.jar:$CLASSPATH"
export CLASSPATH="$root/lib/crdb_005.jar:$CLASSPATH"
export CLASSPATH="$root/lib/jcommon-1.0.10.jar:$CLASSPATH"
export CLASSPATH="$root/lib/jfreechart-1.0.6.jar:$CLASSPATH"
export CLASSPATH="$root/lib/swingx-0.9.3.jar:$CLASSPATH"
export CLASSPATH="$root/lib/usda_sr23.jar:$CLASSPATH"

ant
