#!/bin/bash

outfile="speed_eventalert.txt"

echo S6 > $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S6/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S7 >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S7/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S8 >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S8/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S9 >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S9/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S10 >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S10/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S11 >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S11/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S12 >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S12/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S13 >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S13/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S14 >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S14/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S15 >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S15/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S16  >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S16/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S17  >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S17/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S21  >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S21/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S22  >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S22/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S23  >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S23/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S24  >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S24/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S25  >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S25/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile 
echo S26  >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S26/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile  
echo S27  >> $outfile; java -classpath .:./*:commons-lang3-3.9/*:./mjparser/*:./commons-math3-3.6.1/* inference_engine.Driver subjecttraces/S27/all_concat.csv example_files/example_constraint.bayesianconfig example_files/example_constraint.priors >> $outfile

java Probability_To_Csv $outfile 