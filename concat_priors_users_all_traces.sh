#!/bin/bash

rootdir='./driving_sim_data/PreProcess_PupilChange_0222/'
outfilebase='./src/subjecttraces/'
outfile='prior_users_all_traces_concat.csv'
echo "outputfilebase="  $outfilebase
need_header=true
mkdir -p $outfilebase
i=0
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
	#echo ${D} "all files in D"
	if [ -d ${D} ]; then
		#echo $D "is directory"
               	concatfilebase="${D/$rootdir/$outfilebase}"
		echo "concatfilebase" $concatfilebase
		mkdir -p $concatfilebase
		touch $concatfilebase/$outfile
		if [ $need_header ]; then
			head -1 $D/001.csv > $concatfilebase/$outfile
			need_header=false
		fi
		for filename in $D/*.csv; do
			#echo "Adding" $filename "to concatenated csv file" $concatfilebase/$outfile
			#cat $filename >> $concatfilebase/concat.csv
			tail -n +2 $filename >> $outfilebase/$outfile
			#echo $filename
			#cat $concatfilebase/concat.csv
		done
	i=$i+1
	if [[ $i -ge  9 ]]; then
		break;
	fi
	#break
	fi
done
echo Output to $outfilebase/$outfile
