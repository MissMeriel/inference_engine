#!/bin/bash

rootdir="./sweep_newinterp/split/exp_traces/"
outfile="sweepsplit_compoundevents3.txt"
config_file=" example_files/sweep_allvars_compound_minimalex.bayesianconfig"
priors_file="example_files/sweep_allvars_minimalex.priors"
pattern=''
echo "" > $outfile
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
   #echo ${D} "all files in D"
   if [[ $D =~ \not_interpolated.csv$ ]]; then
		continue	
	fi
   if [[ $D =~ \interpolatedtest.csv$ ]]; then
		echo "Generating probabilites for" $D
		echo $'\n\n\n\n' $D >> $outfile
		java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/*:./com.google.guava_1.6.0.jar:./java-cup-11a-runtime.jar inference_engine.Driver $D $config_file $priors_file >> $outfile
	fi
done
java -classpath commons-lang3-3.9/:. Probability_To_Csv $outfile bag horiz
