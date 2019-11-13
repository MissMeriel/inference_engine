#!/bin/bash

rootdir='./driving_sim_data/backtracking_5'
outfilebase='./src/subjecttraces/'
echo "outputfilebase="  $outfilebase
#mkdir -p $outfilebase
for D in `find ${rootdir} -mindepth 1 -maxdepth 1`; do
	#echo ${D} "all files in D"
	if [ -d ${D} ]; then
		#echo $D "is directory"
			for filename in $D/*.csv; do
				#echo ""
				echo "Analyzing "$filename" for semiauto characteristics" #$filename "is directory"
				# PARSE ALARM COLUMN INTO ARRAY
				readarray -t eCollection < <(cut -d, -f3 $filename)
				#printf "%s\n" "${eCollection[0]}"
				echo "eCollection: " ${eCollection[*]}
				# LOOK FOR SEMIAUTO PATTERN OF ALARMS
				# PCOT COTP OTPC TPCO PCTO CTOP TOPC OPCT
				# LEAST-LETTER UNIQUE PATTERNS:
				# PC COT CTO OT OPC TPC TO
				first_alarm="-"
				second_alarm="-"
				third_alarm="-"
				fourth_alarm="-"
				emptystring="-"
				P="pedestrian alarm"
				T="truck alarm"
				C="cyclist alarm"
				O="obstacle alarm"
				found=false
				next=false
				for alarm in "${eCollection[@]}"; do
					if [ "$alarm" == "Alarm_M2H" ]; then
						continue
					elif [ "$alarm" != " "  -a "$first_alarm" == "$emptystring" ]; then
						#set first alarm
						first_alarm=$alarm
						echo "first_alarm: " $first_alarm
					elif [ "$first_alarm" != "$emptystring" -a "$second_alarm" == "$emptystring" -a "$third_alarm" == "$emptystring" -a "$fourth_alarm" == "$emptystring" ]; then
						#set second alarm
						#echo "SET SECOND ALARM"
                                                if [ "$first_alarm" == "$P" -a "$alarm" == "$C" ]; then
                                                        #two-letter pattern; we're done. add to concat file
                                                        second_alarm=$alarm
                                                        found=true
                                                        echo "FOUND; second_alarm=" $second_alarm
							
                                                elif [ "$first_alarm" == "$O" -a "$alarm" == "$T" ]; then
                                                        #two-letter pattern; we're done. add to concat file
                                                        second_alarm=$alarm
                                                        found=true
                                                        echo "FOUND; second_alarm=" $second_alarm
                                                        
                                                elif [ "$first_alarm" == "$T" -a "$alarm" == "$O" ]; then
                                                        #two-letter pattern; we're done. add to concat file
                                                        second_alarm=$alarm
                                                        found=true
                                                        echo "FOUND; second_alarm=" $second_alarm
                                                        
                                                elif [ "$alarm" != "$first_alarm" -a "$alarm" != " " ]; then
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

                                                elif [ "$first_alarm" == "$C" -a "$second_alarm" == "$T" -a "$alarm" == "$O" ]; then
                                                        #three-letter pattern; we're done. add to concat file
							third_alarm=$alarm
                                                        echo "FOUND; third_alarm=" $alarm

                                                elif [ "$first_alarm" == "$O" -a "$second_alarm" == "$P" -a "$alarm" == "$C" ]; then
                                                        #three-letter pattern; we're done. add to concat file
							third_alarm=$alarm
                                                        echo "FOUND; third_alarm=" $alarm

                                                elif [ "$first_alarm" == "$T" -a "$second_alarm" == "$P" -a "$alarm" == "$C" ]; then
                                                        #three-letter pattern; we're done. add to concat file
							third_alarm=$alarm
                                                        echo "FOUND; third_alarm=" $alarm

                                                elif [ "$alarm" != " " ]; then
                                                        #not a semiauto trace, go on to next trace
                                                        next=true
							echo "NEXT; third_alarm was " $alarm
							echo
                                                fi
                                        fi

				if $next ; then
					break
				fi
				done

				break	
			done
	fi
done


