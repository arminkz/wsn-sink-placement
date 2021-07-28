#!/bin/bash

dt=$(date '+%d_%m_%Y_%H_%M_%S');
res_path="results_${dt}"
mkdir "$res_path"
for scn in wsn_80 wsn_100 wsn_150 wsn_200 wsn_300
do
  echo "running scenario ${scn}... output has been redirected to ${res_path}"
  ./run_alg.sh -s $scn >> "${res_path}/${scn}.txt" &
done
