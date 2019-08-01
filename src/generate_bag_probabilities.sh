#!/bin/bash

rootdir="./sweep_newinterp4/"
outfile="sweep_newinterp_allvars7.txt"
config_file=" example_files/sweep_allvars.bayesianconfig"
priors_file="example_files/sweep_allvars.priors"
pattern=''
echo "" > $outfile
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
        #echo ${D} "all files in D"
        if [[ $D =~ \not_interpolated.csv$ ]]; then
		continue	
	fi
        if [[ $D =~ \interpolated.csv$ ]]; then
		echo "Generating probabilites for" $D
		echo $D >> $outfile
		java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver $D $config_file $priors_file >> $outfile
	fi
done
java -classpath commons-lang3-3.9/:. Probability_To_Csv $outfile bag
