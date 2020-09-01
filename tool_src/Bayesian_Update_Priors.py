#!/usr/env/python

import matplotlib.pyplot as plt
import numpy as np
import sys

############################################################################
# IN: inferred probabilistic properties from last epoch, priors used by that
#  epoch.
# OUT: new priors for next epoch.
# This calculation is based on frequentist updating to the sampling distribution,
#
############################################################################
priors_map = {}
A_counts = {}

def parse_output_line(line):
   line_equals_split = line.split(" = ")
   inv = line_equals_split[0]
   # parse equation
   eqn = line_equals_split[1].split(" ")
   A_prior = eqn[0]
   likelihood = eqn[2]
   total_probability = eqn[4]
   posterior_and_A_count = line_equals_split[2]
   return


def parse_priors_file(priors_file):
   global priors_map
   with open(priors_file, 'r') as f:
      lines = f.readlines()
      for line in lines:
         linesplit = line.split(',')
         temp_map = {}
         for i in range(1, len(linesplit)):
            termsplit = linesplit[i].split(':=')
            value = termsplit[0]
            prob = float(termsplit[1])
            temp_map[value] = prob
         priors_map[linesplit[0]] = temp_map


def parse_output_file(output_file):
   global A_counts
   with open(output_file, 'r') as f:
      lines = f.readlines()
      for line in lines:
         if line != '\n':
            linesplit = line.split(" = ")
            A_predicate = linesplit[0].split(" | ")[0].replace("P(", "")
            A_count = linesplit[-1].split("A_count=")[-1].replace('\n', '')
            A_count = A_count.replace(")","")
            A_count = float(A_count)
            A_counts[A_predicate] = A_count


def calc_new_prior(prob, A_count, observations_old, observations_new):
   new_prior = (prob * observations_old + A_count) / (observations_old + observations_new)
   #print ("({} * {} + {}) / ({} + {})").format(prob, observations_old,A_count,observations_old,observations_new, new_prior)
   return new_prior

def deduce_new_prior(probs_map):
   total = 0
   for key in probs_map.keys():
      total += probs_map[key]
   return 1.0 - total
   
   
def new_priors_filename(priors_file):
   new_priors_file = priors_file.replace('.priors', '')
   try:
      filenumber = int(new_priors_file[-1])
      filenumber += 1
   except:
      filenumber = 1
   new_priors_file += '{}.priors'.format(filenumber)
   print "new_priors_file: " + new_priors_file
   return new_priors_file

def write_new_priors_file(priors_file, observations_old, observations_new):
   global A_counts, priors_map
   new_priors_file=new_priors_filename(priors_file)
   with open(new_priors_file, 'w') as f:
      for prior_var in priors_map.keys():
         line = prior_var +','
         #print "A COUNTS KEYS: "+ str(A_counts.keys())
         for inv_name in A_counts.keys():
            if prior_var in inv_name:
               A_count = A_counts[inv_name]
               # get old probability
               prob_key = "=".join(inv_name.split("=")[1:])
               if prob_key in priors_map[prior_var].keys():
                  prob = priors_map[prior_var][prob_key]
                  #print
                  #print "PRIOR VAR: " + prior_var
                  #print "INV NAME " + inv_name
                  #print priors_map[prior_var]
                  #print "PROB KEY " +prob_key
                  new_prior = calc_new_prior(prob, A_count, observations_old, observations_new)
                  #print "NEW PRIOR: " + str(new_prior)
               else:
                  # this prior didn't appear in trace so update in relation to the remaining probability
                  prob_key = "=".join(inv_name.split("=")[1:])
                  probs_map = priors_map[prior_var]
                  new_prior = deduce_new_prior(probs_map)
               priors_map[prior_var][prob_key] = new_prior
               line += ("{}:={},").format(prob_key, new_prior)  
         line = line[:-1]+ "\n"
         #print line
         f.write(line)


def main():
   global priors_map
   output_file = sys.argv[1]
   priors_file = sys.argv[2]
   observations_old = float(sys.argv[3])
   observations_new = float(sys.argv[4])
   # extract priors from priors_file
   parse_priors_file(priors_file)
   # extract A counts from output_file 
   parse_output_file(output_file)
   # new priors calculation
   write_new_priors_file(priors_file, observations_old, observations_new)

if __name__ == '__main__':
   main()