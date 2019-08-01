#!/bin/bash

rootdir='../ros_tello/clean_bags/sweep/'
outfilebase='./src/sweep_newinterp3/'
#echo "outputfilebase="  $outfilebase
mkdir -p $outfilebase
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
	#echo ${D} "all files in D"
	#if [ -d ${D} ]; then
	#echo $D "is directory"
	for filename in $D; do
		if [[ $filename =~ \sweep_for_target_2019-04-08-09-57-52.bag$ ]]; then
			continue
		fi
		if [[ $filename =~ \.bag$ ]]; then
			echo
			echo "Running python bag_reader2.py " $filename
			python ../bag2csv/bag_reader2.py -b $filename
		fi
	done
	#fi
done


