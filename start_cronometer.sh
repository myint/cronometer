#!/bin/bash
# ----------------------------------------------------------------------------
# Description   Start Cronometer
#
# Date          2017-feb-24
# Author        Dimitar Misev
# ----------------------------------------------------------------------------

# script name
PROG=$(basename $0)

# determine script directory
SOURCE="${BASH_SOURCE[0]}"
while [ -h "$SOURCE" ] ; do SOURCE="$(readlink "$SOURCE")"; done
SCRIPT_DIR="$( cd -P "$( dirname "$SOURCE" )" && pwd )"

CLASSPATH="."
for f in "$SCRIPT_DIR/lib"/*; do
  CLASSPATH="$CLASSPATH:$f"
done

java -cp $CLASSPATH ca.spaz.cron.Cronometer
