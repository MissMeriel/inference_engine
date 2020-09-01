#!/bin/bash

export rootdir=$(pwd)
export tracedir=$(cd ./drone_traces/; pwd)
export tooldir=$(cd ../tool_src; pwd)
outfile=$rootdir/drone_traces_outfile.txt
config_file=$rootdir/example_config_files/sweep_allvars_compound_minimalex.bayesianconfig
priors_file=$rootdir/example_config_files/sweep_allvars_minimalex.priors
echo "" > $outfile
cd $tooldir
for D in `find ${tracedir} -mindepth 1 -maxdepth 1`; do
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
cd $rootdir
