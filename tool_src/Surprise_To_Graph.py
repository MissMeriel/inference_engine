#!/usr/env/python

import matplotlib.pyplot as plt
import numpy as np
import sys, math, os, re
import glob, operator

surprises = {}
varnames = set()



def get_varname(inv):
   varname = inv.split(' | ')[0]
   varname = varname.replace('P(', '')
   return varname
   
# made to handle driving dataset specific naming paradigm
def get_varname_simple(inv):
   varname = inv.split(' | ')[0]
   varname = varname.replace('P(', '')
   varname = varname.split('=')
   return "=".join(varname[1:])
   
   
def get_givens(inv):
   givens = inv.split(' | ')[1].replace(' )', '')
   return givens.split(' ')
   

def parse_line(line):
   global surprises, varnames
   line = line.split(' = ')
   print line
   surprise = float(line[1].replace('\n', ''))
   invariant = line[0]
   try:
      surprises[invariant].append(surprise)
   except KeyError:
      surprises[invariant] = [surprise]
   varnames.add(get_varname_simple(invariant))


def parse_surprise_file(surprise_filename):
   with open(surprise_filename, 'r') as f:
      lines = f.readlines()
      #file_pattern = r'[0-9a-zA-Z_-/]*.txt'
      file_pattern = r'.*.txt'
      for line in lines:
         if re.match(file_pattern, line.replace('\n', '')) or line == '\n':
            continue
         else:
            parse_line(line)

      
def surprises_converge(surprise):
   epsilon = (sum(surprise) / len(surprise)) / 100
   surprise = np.array(surprise)
   midpoint = int(len(surprise) / 2.0)
   if surprise[midpoint:].var() < epsilon:
      return True
   return False
   
def clean_variance(variances):
   clean_variances = []
   for i in range(0, len(variances), 3):
      clean_variances.append(variances[i])
   return clean_variances


def calc_local_variance(arr):
   avg = arr.sum() / len(arr)
   variance = sum(abs(arr - avg)**2)/len(arr)
   return variance

def calc_variance():
   global surprises
   surprise_variance_map = {}
   for inv in surprises.keys():
      surprise_arr = np.array(surprises[inv])
      variances = []
      for i in range(len(surprise_arr)):
         #print("len(surprise_arr): "+str(len(surprise_arr)))
         #print("surprise_arr: "+str(surprise_arr))
         #print
         variance_arr = np.array(surprise_arr[i:])
         variance = calc_local_variance(variance_arr)
         variances.append(variance)
      surprise_variance_map[inv] = variances
   return surprise_variance_map


### apply adapted BIC to select converging models
# n is a constant as we are comparing the same Outcome
def perform_model_selection(converging_inv_map):
   global surprises
   select_converging_inv_map = {}
   for A_event in converging_inv_map.keys():
      invs = converging_inv_map[A_event]
      invs2 = converging_inv_map[A_event]
      for inv in invs:
         givens = get_givens(inv)
         for inv2 in invs2:
            givens2 = get_givens(inv2)
            bic = len(givens)
            try:
               bic = len(givens) - 2 * math.log(np.array(surprises[inv]).var())
            except ValueError:
               bic = len(givens)
            try:
               bic2 = len(givens2) - 2 * math.log(np.array(surprises[inv2]).var())
            except ValueError:
               bic2 = len(givens2)
            if bic > bic2:
               inv = inv2
               givens = givens2
               continue
      select_converging_inv_map[inv] = converging_inv_map[A_event][inv]
   return select_converging_inv_map
   
   
def main():
   global surprises, varnames
   # parse surprise tracker file
   np.seterr(divide='ignore', invalid='ignore')
   try:
      surprise_filename = sys.argv[1]
   except:
      surprise_filename = 'surprise_tracker.txt'
   parse_surprise_file(surprise_filename)
   
   converging_invs = 0
   converging_inv_map = {}
   total_variance_map = {}
   # calc variance and determine convergence
   surprise_variance_map = calc_variance()
   keys = surprise_variance_map.keys()
   keys.sort()
   max_variance = 0.0
   for inv in keys:
      surprise = surprises[inv]
      varname = get_varname_simple(inv)
      convergence = surprises_converge(surprise)
      variances = surprise_variance_map[inv]
      total_variance = np.array(surprise).var()
      try:
         total_variance_map[varname][inv] = total_variance
      except:
         total_variance_map[varname] = {inv: total_variance}
         
   select_total_variance_map = perform_model_selection(total_variance_map)
   #total_variance_map = {k: v for k, v in sorted(total_variance_map.items(), key=lambda item: item[1])}
   #total_variance_map = sorted(total_variance_map.items(), key=operator.itemgetter(1))
   #sorted(total_variance_map.items(), key=lambda kv: kv[1])
   sorted_surprise_variance_map = sorted(surprise_variance_map.items(), key=operator.itemgetter(1))
   #select_converging_inv_map = perform_model_selection(converging_inv_map)
   fig = plt.figure()
   ax = plt.gca()
   ax.tick_params(axis = 'both', which = 'major', labelsize = 24)
   ax.tick_params(axis = 'both', which = 'minor', labelsize = 24)
   plt.ylabel('Surprise Variance', fontsize=36)
   plt.xlabel('Iterations', fontsize=36)

   for inv in select_total_variance_map.keys():
      variances = surprise_variance_map[inv]
      variances = clean_variance(variances)
      
      varname = get_varname_simple(inv)
      # using top 5 invariants for graph
      if (select_total_variance_map[inv] > 4 and "P(EventW_Machine=EventW_Machine==" not in inv):
         print inv
         print select_total_variance_map[inv]
         print variances
         print
         plt.plot(range(len(variances)), variances, label=varname)
   plt.legend(fontsize=21)
   #ax.set_yscale('log')
   plt.show()
   
   #fig.suptitle('Variance of Surprise Ratio for Converging Invariants', fontsize=32)
   plt.show()
   

if __name__ == '__main__':
   main()