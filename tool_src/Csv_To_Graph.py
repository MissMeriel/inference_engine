#!/usr/env/python

import matplotlib.pyplot as plt
import numpy as np
import sys, math

def distance(x1, y1, x2, y2):
   return math.sqrt((x1 - x2)**2 + (y1 - y2)**2)

def main():
   gen_header = 'Invariant,Avg,Prior,Ratio,Avg Observations,Avg A Samples'
   #avg_linecount = 177368 /17
   gen_header = gen_header.split(',')
   filename = sys.argv[1]
   avg_a_samples = []
   ratio = []
   invs = []
   with open(filename, 'r') as f:
      lines = f.readlines()
      header = ''
      for i in range(len(lines)):
         #print(lines[i])
         if i == 0:
            header = lines[i]
            continue
         linesplit = lines[i].split(',')
         #print(linesplit)
         invs.append(linesplit[0])
         ratio.append(float(linesplit[3]))
         observations = float(linesplit[4])
         #print observations
         try:
            #avg_a_samples.append(math.log(observations, 1.3))
            avg_a_samples.append(observations)
         except:
            avg_a_samples.append(0)
         
   
   ideal = [max(ratio), max(avg_a_samples)]
   distances = []
   for i in range(len(ratio)):
      dist = distance(ratio[i], avg_a_samples[i], ideal[0], ideal[1])
      distances.append(dist)
   index_of_max = np.argmin(distances)
   
   fig = plt.figure()
   plt.scatter(ratio, avg_a_samples, color="orange")
   plt.scatter(ratio[index_of_max], avg_a_samples[index_of_max], color="black")
   print(index_of_max)
   print("surprise ratio range: {}, {}".format(min(ratio), max(ratio)))
   print("observations range: {}, {}".format(min(avg_a_samples), max(avg_a_samples)))
   print(max(avg_a_samples))
   print(invs[index_of_max])
   print(ratio[index_of_max])
   print(avg_a_samples[index_of_max])
   plt.scatter(ideal[0], ideal[1], color='blue')
   #fig.suptitle('Surprise Ratio versus Event Support', fontsize=32)
   plt.ylabel('$Log_{1.2}($Support$)$', fontsize=30)
   plt.xlabel('Surprise Ratio', fontsize=30)
   plt.show()

if __name__ == '__main__':
   main()