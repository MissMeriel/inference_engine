#!/usr/env/python

import matplotlib.pyplot as plt
import numpy as np
import sys, math, os, re
import glob

varnames = []
invariant_map = {}
surprise_map = {}
priors_map = {}

# make each priors file into a map
# priors_map[varname][value] = prior
def parse_priors_file(filename):
   global varnames, priors_map
   with open(filename, 'r') as f:
      lines = f.readlines()
      for line in lines:
         linesplit = line.split(',')
         varname = linesplit[0]
         varnames.append(varname)
         temp = {}
         for i in range(1, len(linesplit)):
            linesplit2 = linesplit[i].split(':=')
            temp[linesplit2[0]] = float(linesplit2[1])
            priors_map[varname] = temp
   return priors_map


def parse_invariant_line(line):
   global invariant_map
   if line != '\n':
      linesplit = line.split(" = ")
      invariant = linesplit[0]
      #if 'P(Mode_H2M=autonomous' in invariant:  
      #   A_predicate = linesplit[0].split(" | ")[0].replace("P(", "")
      #   print A_predicate
      #   if('manual' in A_predicate or 'autonomous' in A_predicate):
      #      print 'YES'
      #   #exit()
      A_predicate = linesplit[0].split(" | ")[0].replace("P(", "")
      if('manual'  in A_predicate or 'autonomous'  in A_predicate):
         pass
      else:
         A_predicate = A_predicate.split("=")[1:]
         A_predicate = "=".join(A_predicate)
      posterior = linesplit[-1].replace('\n', '').split(' ')[0]
      posterior = float(posterior)
      try:
         invariant_map[A_predicate][invariant] = posterior
      except:
         invariant_map[A_predicate] = {invariant : posterior}


def parse_invariants_file(invariants_filename):
   with open(invariants_filename, 'r') as f:
      lines = f.readlines()
      for line in lines:
         parse_invariant_line(line)


def get_varname_from_event(event):
   global varnames
   for varname in varnames:
      if varname in event:
         return varname
   return ""

def clean_event(A_event, varname):
   if '==' in A_event:
      return A_event
   return A_event.split(varname+'=')[-1]

def deduce_prior(events_map):
   total = 0.0
   for key in events_map.keys():
      total += events_map[key]
   return 1.0 - total

def calculate_surprise():
   global invariant_map, priors_map, surprise_map
   for A_event in invariant_map.keys():
      varname = get_varname_from_event(A_event)
      for invariant in invariant_map[A_event].keys():
         posterior = invariant_map[A_event][invariant]
         event = clean_event(A_event, varname)
         try:
            prior = priors_map[varname][event]
         except:
            prior = deduce_prior(priors_map[varname])
         surprise_map[invariant] = posterior / prior
   

def write_surprise(invariants_filename, surprise_outfile):
   global surprise_map
   with open(surprise_outfile, 'a') as f:         
      f.write(invariants_filename+'\n')
      for invariant in surprise_map.keys():
         surprise = surprise_map[invariant]
         f.write('{} = {}\n'.format(invariant, surprise))
      f.write('\n\n\n\n')
   
def main():
   invariants_filename=sys.argv[1]
   priors_filename=sys.argv[2]
   surprise_outfile="surprise_tracker.txt"
   parse_priors_file(priors_filename)
   parse_invariants_file(invariants_filename)
   calculate_surprise()
   write_surprise(invariants_filename, surprise_outfile)
   
if __name__ == '__main__':
   main()