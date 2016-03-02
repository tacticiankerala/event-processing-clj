#!/bin/bash
set -e
lein clean
lein uberjar
docker build -t event-processing:0.1.0 . 
