#!/bin/bash

rootdir='./src/sweep/'
outfilebase='./src/sweep'
outfile='all_drone_concat.csv'
#echo "outputfilebase="  $outfilebase
touch $outfilebase/$outfile
head -1 sweep_for_target_2019-04-12-20-46-25_not_interpolated.csv > $outfilebase/$outfile
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
	#echo ${D} "all files in D"
	if [[ $D =~ \not_interpolated.csv$ ]]; then
	#if [ -d ${D} ]; then
		echo $D "is non interpolated bag"
               	#concatfilebase="${D/$rootdir/$outfilebase}"
		#echo "concatfilebase" $concatfilebase
		#mkdir -p $concatfilebase
		#touch $concatfilebase/$outfile
		#for filename in $D/*_not_interpolated.csv; do
			echo "Adding" $D "to concatenated csv file" $outfilebase/$outfile
			#cat $filename >> $concatfilebase/concat.csv
			tail -n +2 $D >> $outfilebase/$outfile
			#echo $filename
			#cat $concatfilebase/concat.csv
		#done
	#break
	fi
done
