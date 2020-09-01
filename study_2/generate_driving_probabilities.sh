#!/bin/bash

source /etc/profile.d/modules.sh
module load java

export rootdir=$(pwd)
export tracedir=$(cd ./subjecttraces/; pwd)
export tooldir=$(cd ../tool_src; pwd)
outfile=$rootdir/driving_priors_update.txt
temp_outfile=$rootdir/driving_temp_outfile.txt
surprise_file=$rootdir/driving_surprise_outfile.txt
config_file=$rootdir/example_config_files/driving_allvars_mini.bayesianconfig
config_file2=$rootdir/example_config_files/driving_allvars_mini2.bayesianconfig
config_file3=$rootdir/example_config_files/driving_allvars_mini3.bayesianconfig
config_file4=$rootdir/example_config_files/driving_allvars_mini4.bayesianconfig
config_file5=$rootdir/example_config_files/driving_allvars_mini5.bayesianconfig
priors_file=$rootdir/example_config_files/driving_allvars_uni.priors
total_observations=30
temp_observations=0
echo "" > $outfile
echo "" > $temp_outfile
echo "" > $surprise_file
cd $tooldir

for D in `find ${tracedir} -mindepth 1 -maxdepth 1`; do
	if [ -d ${D} ]; then
      echo
		basename ${D} >> $outfile
      temp_observations=$(wc -l $D/all_concat.csv | awk '{ print $1 }')
		echo "Generating probabilites for" $D/all_concat.csv
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
      priors_file=$rootdir/example_config_files/$(ls -t $rootdir/example_config_files | head -1)
	fi
done
java -classpath commons-lang3-3.9/*:. Probability_To_Csv $outfile horiz
python Surprise_To_Graph.py $tooldir/surprise_tracker.txt
cd $rootdir
