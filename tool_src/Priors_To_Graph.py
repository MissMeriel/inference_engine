#!/usr/env/python

import matplotlib.pyplot as plt
import numpy as np
import sys, math, os, re
import glob

varnames = []

# make each priors file into a map
# priors_map[varname][value] = prior
def parse_priors_file(filename):
   global varnames
   priors_map = {}
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


def parse_maps(maps):
   global varnames
   prior_prog_map = {}
   for v in varnames:
      prior_prog_map[v] = {}
      temp = {}
      for m in maps:
         for value in m[v].keys():
            prior = m[v][value]
            try:
               temp[value].append(prior)
            except:
               temp[value] = [prior]
      print(temp)
      prior_prog_map[v] = temp
   return prior_prog_map
   
   
def main():
   try:
      dirname = sys.argv[1]
   except:
      dirname = './example_files/'
   priors_file_pattern = r'driving_allvars_uni[0-9]*.priors'
   #files = [f for f in os.listdir(dirname) if re.match(r'[0-9]+.*\.jpg', f)]
   files = [f for f in os.listdir(dirname) if re.match(priors_file_pattern, f)]
   files.sort()
   print(files)
   maps = []
   for f in files:
      maps.append(parse_priors_file(dirname+f))
   mapped_priors = parse_maps(maps)
   
   for m in mapped_priors.keys():
      #print("m={}".format(m))
      for v in mapped_priors[m].keys():
         priors = mapped_priors[m][v]
         fig = plt.figure()
         plt.plot(range(len(priors)), priors, color="orange")
         if "=" or ">" or "<" in v:
            fig.suptitle('Priors for '+v, fontsize=32)
         else:
            fig.suptitle('Priors for '+m+"=="+v, fontsize=32)
         plt.ylabel('Prior', fontsize=30)
         plt.xlabel('Iterations', fontsize=30)
         plt.show()

if __name__ == '__main__':
   main()