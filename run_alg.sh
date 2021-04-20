#!/bin/bash

printf -v var "%s," "$@"
#var=${var%??}

./gradlew run_algs -q -PrunArgs="$var"