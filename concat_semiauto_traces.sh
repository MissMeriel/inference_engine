#!/bin/bash

rootdir='./driving_sim_data/PreProcess_PupilChange_0222/'
outfilebase='./src/subjecttraces/'
outfile='semiauto_concat.csv'
echo "outputfilebase="  $outfilebase
mkdir -p $outfilebase
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
	#echo ${D} "all files in D"
	if [ -d ${D} ]; then
		#echo $D "is directory"
               	concatfilebase="${D/$rootdir/$outfilebase}"
		echo "concatfilebase" $concatfilebase
		mkdir -p $concatfilebase
		touch $concatfilebase/$outfile
		head -1 $D/001.csv > $concatfilebase/$outfile
		for filename in $D/*.csv; do
			#echo ""
			echo "Analyzing "$filename" for semiauto characteristics" #$filename "is directory"
			#dtracefile=$rootdir/S*/dtrace/*
			#echo "dtracefile" $dtracefile
                        #outfilebase="${filename/decls/new_inv.gz\/all}"
                        #xmlfile="${outfilebase/decls/xml}"
                        #decls_base=$(basename "$filename" )
			# PARSE ALARM COLUMN INTO ARRAY
			readarray -t eCollection < <(cut -d, -f23 $filename)
			#printf "%s\n" "${eCollection[0]}"
			#echo "eCollection: " ${eCollection[*]}
			# LOOK FOR SEMIAUTO PATTERN OF ALARMS
			# PCOT COTP OTPC TPCO PCTO CTOP TOPC OPCT
			# LEAST-LETTER UNIQUE PATTERNS:
			# PC COT CTO OT OPC TPC TO
			first_alarm="-"
			second_alarm="-"
			third_alarm="-"
			emptystring="-"
			P="pedestrian detected"
			T="truck detected"
			C="cyclist detected"
			O="obstacle detected"
			F="false alarm"
			found=false
			next=false
			for alarm in "${eCollection[@]}"; do
				if [ "$alarm" == "EventW_Machine" ]; then
					continue
				elif [ "$alarm" != " "  -a  "$alarm" != "$F" -a "$first_alarm" == "$emptystring" ]; then
					#set first alarm
					first_alarm=$alarm
					echo "first_alarm: " $first_alarm
				elif [ "$first_alarm" != "$emptystring" -a "$second_alarm" == "$emptystring" -a "$third_alarm" == "$emptystring" ]; then
					#set second alarm
                                        if [ "$first_alarm" == "$P" -a "$alarm" == "$C" ]; then
                        			#two-letter pattern; we're done. add to concat file
                                                second_alarm=$alarm
                                                found=true
                                                echo "FOUND; second_alarm=" $second_alarm
						echo
                                                break
							
                              		elif [ "$first_alarm" == "$O" -a "$alarm" == "$T" ]; then
                                                #two-letter pattern; we're done. add to concat file
                                                second_alarm=$alarm
                                                found=true
                                                echo "FOUND; second_alarm=" $second_alarm
						echo
                                        	break

                                   	elif [ "$first_alarm" == "$T" -a "$alarm" == "$O" ]; then
                                                #two-letter pattern; we're done. add to concat file
                                                second_alarm=$alarm
                                                found=true
                                                echo "FOUND; second_alarm=" $second_alarm
						echo
                                        	break
                                                        
                                	elif [ "$alarm" != "$first_alarm" -a "$alarm" != " " -a  "$alarm" != "$F"  ]; then
                                                # need another letter
                                                second_alarm=$alarm
                                                echo "second_alarm=" $second_alarm
                        	                continue
                	                fi
				elif [ "$first_alarm" != "$emptystring" -a "$second_alarm" != "$emptystring" -a "$third_alarm" == "$emptystring" ]; then
                                        #set third alarm
                                	if [ "$first_alarm" == "$C" -a "$second_alarm" == "$O" -a "$alarm" == "$T" ]; then
                                                #three-letter pattern; we're done. add to concat file
						third_alarm=$alarm
                                                echo "FOUND; third_alarm=" $alarm
                                                found=true
						echo
                                                break

                                        elif [ "$first_alarm" == "$C" -a "$second_alarm" == "$T" -a "$alarm" == "$O" ]; then
                                                #three-letter pattern; we're done. add to concat file
						third_alarm=$alarm
                                                found=true
                                                echo "FOUND; third_alarm=" $alarm
						echo
                                                break

                                        elif [ "$first_alarm" == "$O" -a "$second_alarm" == "$P" -a "$alarm" == "$C" ]; then
                                                #three-letter pattern; we're done. add to concat file
						third_alarm=$alarm
                                                found=true
                                                echo "FOUND; third_alarm=" $alarm
						echo
                                                break

                                        elif [ "$first_alarm" == "$T" -a "$second_alarm" == "$P" -a "$alarm" == "$C" ]; then
                                                #three-letter pattern; we're done. add to concat file
						third_alarm=$alarm
                                                found=true
                                                echo "FOUND; third_alarm=" $alarm
						echo
                                                break

                                       elif [ "$alarm" != " " -a "$alarm" != "$second_alarm" ]; then
                                                #not a semiauto trace, go on to next trace
                                                next=true
						echo "NEXT; third_alarm was " $alarm
						echo
                                                break
                                       fi
                                fi
			done
			#echo "found" $found "next" $next
			if $next ; then
				echo next $next
				#break
			elif $found ; then
				# cat current csv to new concat file
				echo "Adding" $filename "to concatenated csv file" $concatfilebase/semiauto_concat.csv
				#cat $filename >> $concatfilebase/concat.csv
				tail -n +2 $filename >> $concatfilebase/$outfile
				#echo $filename
				#cat $concatfilebase/concat.csv
			fi
		done
	#break
	fi
done
