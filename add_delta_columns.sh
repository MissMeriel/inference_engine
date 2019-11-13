#!/bin/bash

rootdir='./driving_sim_data/DrivingRawData20190807(weatherAdded)/'
outfilebase='./src/subjecttraces_weather/'
outfile='all_concat.csv'
echo "outputfilebase="  $outfilebase
mkdir -p $outfilebase
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
	#echo ${D} "all files in D"
	if [ -d ${D} ]; then
		#echo $D "is directory"
               	concatfilebase="${D/$rootdir/$outfilebase}"
		echo "concatfilebase" $concatfilebase
		head -1 $D/001.csv > $concatfilebase/$outfile
		# add columns Speed_Delta, CurrentWheel_MachineDelta to outfile
		
			#echo "Adding" $filename "to concatenated csv file" $concatfilebase/$outfile
			#cat $filename >> $concatfilebase/concat.csv
			tail -n +2 $filename >> $concatfilebase/$outfile
			#echo $filename
			#cat $concatfilebase/concat.csv
		done
	#break
	fi
done
