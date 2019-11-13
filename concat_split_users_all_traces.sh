#!/bin/bash

rootdir='./src/subjecttraces_weather/split/prior_traces'
outfilebase='./src/subjecttraces_weather/split/prior_traces'
outfile='all_users_all_traces_concat.csv'
echo "outputfilebase="  $outfilebase
need_header=true
mkdir -p $outfilebase
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
	#echo ${D} "all files in D"
	if [ -d ${D} ]; then
		#echo $D "is directory"
               	#concatfilebase="${D/$rootdir/$outfilebase}"
               	concatfilebase=$outfilebase
		echo "concatfilebase" $concatfilebase
		mkdir -p $concatfilebase
		touch $concatfilebase/$outfile
		#if [ $need_header ]; then
		#	head -1 $D/all_concat.csv > $concatfilebase/$outfile
		#	need_header=false
		#fi
		#for filename in $D/*.csv; do
			filename=$D'/all_concat.csv'
			echo "Adding" $filename "to concatenated csv file" $concatfilebase/$outfile
			#cat $filename >> $concatfilebase/concat.csv
			tail -n +2 $filename >> $outfilebase/$outfile
			#echo $filename
			#cat $concatfilebase/concat.csv
		#done
	#break
	fi
done
