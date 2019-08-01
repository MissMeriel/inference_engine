#!/bin/bash

source /etc/profile.d/modules.sh
module load java

rootdir="./subjecttraces"
outfile="driving_testformatting.txt"
pattern=''
config_file="example_files/driving_allvars.bayesianconfig"
priors_file="example_files/driving_allvars.priors"
echo "" > $outfile
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
	if [ -d ${D} ]; then
		#echo ${D} "all files in D"
		#basename ${D}
		basename ${D} >> $outfile
		#for E in `find ${D} -mindepth 1 -maxdepth 1`; do
			#echo "E:" ${E}
	        	#if [[ $E =~ concat.csv ]]; then
		echo "Generating probabilites for" $D/all_concat.csv
			#echo $D >> $outfile
		java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver $D/all_concat.csv $config_file $priors_file >> $outfile
	fi
done
java -classpath commons-lang3-3.9/*:. Probability_To_Csv $outfile
