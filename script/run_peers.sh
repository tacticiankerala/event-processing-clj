#!/usr/bin/env bash
set -o errexit
set -o nounset
set -o xtrace

export BIND_ADDR=$(hostname --ip-address)
export APP_NAME=$(echo "event-processing" | sed s/"-"/"_"/g)
exec java -cp /srv/event-processing.jar "$APP_NAME.launcher.launch_prod_peers" $NPEERS 
