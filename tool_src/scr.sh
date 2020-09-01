#!/bin/bash
PATH="$HOME/bin:$HOME/.local/bin:$PATH"
export PATH

rootdir="sweep/"
outfile="sweepsplit_compoundevents2.txt"
config_file=" example_files/sweep_allvars_compound_minimalex.bayesianconfig"
priors_file="example_files/sweep_allvars_minimalex.priors"
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
   #echo ${D} "all files in D"
   if [[ $D =~ \not_interpolated.csv$ ]]; then
      continue	
   fi
   if [[ $D =~ \interpolated.csv$ ]]; then
		echo "Generating daikon files for" $D
		python ~/daikon/daikon-ext/convertcsv_rowppt.py $D
		decls="${D//csv/decls}"
		dtrace="${D//csv/dtrace}"
		echo "Generating daikon invariants for" $D
		daikon-java-full $decls $dtrace
	fi
done
