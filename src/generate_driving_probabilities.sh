#!/bin/bash

source /etc/profile.d/modules.sh
module load java

rootdir="./subjecttraces"
outfile="driving_priors_update1.txt"
temp_outfile="driving_temp_outfile.txt"
surprise_file="driving_surprise_outfile.txt"
pattern=''
config_file="example_files/driving_allvars_mini.bayesianconfig"
config_file2="example_files/driving_allvars_mini2.bayesianconfig"
config_file3="example_files/driving_allvars_mini3.bayesianconfig"
config_file4="example_files/driving_allvars_mini4.bayesianconfig"
config_file5="example_files/driving_allvars_mini5.bayesianconfig"
priors_file="example_files/driving_allvars_uni.priors"
total_observations=30
temp_observations=0
echo "" > $outfile
echo "" > $temp_outfile
echo "" > $surprise_file
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
	if [ -d ${D} ]; then
      echo
		#echo ${D} "all files in D"
		#basename ${D}
		basename ${D} >> $outfile
      temp_observations=$(wc -l $D/all_concat.csv | awk '{ print $1 }')
		#for E in `find ${D} -mindepth 1 -maxdepth 1`; do
			#echo "E:" ${E}
	        	#if [[ $E =~ concat.csv ]]; then
		echo "Generating probabilites for" $D/all_concat.csv
			#echo $D >> $outfile
		java -Xms256m -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver $D/all_concat.csv $config_file $priors_file > $temp_outfile
      java -Xms256m -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver $D/all_concat.csv $config_file2 $priors_file >> $temp_outfile
      java -Xms256m -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver $D/all_concat.csv $config_file3 $priors_file >> $temp_outfile
      java -Xms256m -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver $D/all_concat.csv $config_file4 $priors_file >> $temp_outfile
      java -Xms256m -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver $D/all_concat.csv $config_file5 $priors_file >> $temp_outfile

      echo Done with probability generation
      
      cat $temp_outfile >> $outfile
      python Surprise_Writer.py $temp_outfile $priors_file
      echo total_observations: $total_observations temp_observations: $temp_observations
      # update priors
      echo "Updating priors using data from " $D/all_concat.csv
      python Bayesian_Update_Priors.py $temp_outfile $priors_file $total_observations $temp_observations
      total_observations=$(expr $total_observations + $temp_observations)
      priors_file=example_files/$(ls -t example_files | head -1)
	fi
done
java -classpath commons-lang3-3.9/*:. Probability_To_Csv $outfile horiz
python Surprise_To_Graph.py surprise_tracker.txt
