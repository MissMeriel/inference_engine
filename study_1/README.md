# Study 1 -- Drone study
This directory contains traces, example configuration files, and script for running the engine on all traces. To change configuration, change the files referenced in the script.
The output will be a .csv file which the developer can view invariant probabilities on a per-trace basis as well as an average over all traces. Surprise ratio is computed as an average probability over prior.

## To run this study:

```
. ./generate_bag_probabilities.sh
```

## Output:

Invariant output is organized for improved readability in ./drone_outfilehoriz.csv. Layout is as described in the toplevel [README](../README.md).
