#!/bin/bash

dt=$(date '+%d_%m_%Y_%H_%M_%S');
res_path="results_${dt}"
mkdir "$res_path"
for scn in wsn_80 wsn_100 wsn_150 wsn_200 wsn_300
do
  for alg in bf ga hc hcs fhc fhcs
  do
    echo "running with algorithm ${alg} in scenario ${scn}... output has been redirected to ${res_path}"
    ./run_alg.sh -s $scn -a $alg >> "${res_path}/${scn}_${alg}.txt"
  done
done
