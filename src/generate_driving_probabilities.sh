#!/bin/bash

rootdir="./subjecttraces"
outfile="driving_allvars.txt"
pattern=''
echo "" > $outfile
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
	if [ -d ${D} ]; then
		#echo ${D} "all files in D"
		#basename ${D}
		basename ${D} >> $outfile
		#for E in `find ${D} -mindepth 1 -maxdepth 1`; do
			#echo "E:" ${E}
	        	#if [[ $E =~ concat.csv ]]; then
		echo "Generating probabilites for" $D/concat.csv
			#echo $D >> $outfile
		#java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver $E/concat.csv example_files/driving_allvars.bayesianconfig example_files/driving_allvars.priors >> $outfile
	fi
done
java -classpath commons-lang3-3.9/:. Probability_To_Csv $outfile
