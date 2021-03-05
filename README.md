# WSN-Sink-Placement

Optimizing Sink Node Placement in WSNs

## How to Run

```
./run_gui
```

* it is highly recommended that you install `graphviz` before running the project because it leads to significant speedup in plotting the graphs.
to check if you have a graphviz installation use `dot` command.


## Getting different algorithms results

```
./run_algs -s <scenario_name> -a <algorithm_name> [-n <repeats>]
```

`<scenario_name>` is one of the folder names located in `data` folder. `<repeats>` is an optional argument which specifies numbers of restarting. for `<algorithm_name>` check the following list:


Baseline algorithms : 
* Brute Force `bf`
* Genetic Algorithm `ga`
* Hill Climbing `hc`
* Hill Climbing (Stochastic) `hcs`

Proposed algorithms :
* Fast Hill Climbing `fhc`
* Fast Hill Climbing (Stochastic) `fhcs`
