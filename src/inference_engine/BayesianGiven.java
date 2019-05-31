package inference_engine;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;

public class BayesianGiven extends Given {

   HashMap<String, HashMap<String, Double>> priors;
   HashMap<String, HashMap<String, Double[]>> cumulative_probabilities;
   public ArrayList<BayesianBin> bins = new ArrayList<BayesianBin>();
   int trace_total;
   boolean debug = true;
   
   public BayesianGiven(String name, HashMap<String, HashMap<String, Double>> priors){
      super(name);
      this.priors = priors;
   }
   
   public BayesianBin add_bin(String type, String sample){
      BayesianBin new_bin = new BayesianBin(type, sample, priors);
      bins.add(new_bin);
      return new_bin;
   }
   
   public BayesianBin contains_bin(String template){
      for (BayesianBin b : bins){
         if (b.template.equals(template)){
            return b;
         }
      }
      return null;
   }
   
   public double get_bin_probability(BayesianBin bin){
      return bin.num_samples/(double)bin.total;
   }
   
   public void set_total(int i ){
      trace_total = i;
      for (BayesianBin bin : bins){
         bin.set_total(i);
      }
   }
   
   public void set_priors(HashMap<String, HashMap<String, Double>> priors){
      this.priors = priors;
      for (BayesianBin bin : bins){
         bin.set_priors(priors);
      }
   }
   
   
   public void set_cumulative_probabilities(HashMap<String, HashMap<String, Double[]>> cumulative_probabilities){
      this.cumulative_probabilities = cumulative_probabilities;
      for (BayesianBin bin : bins){
         bin.set_cumulative_probabilities(cumulative_probabilities);
      }
   }
   
   @Override
   public String toString(){
      String str = "";
      //System.out.println("All bins: "+bins.size());
      for (BayesianBin b : bins){
         b.set_trace_total(trace_total);
         String prob = String.format("%.0f%%", get_bin_probability(b)*100);
         str += b.toString(prob)+"\n";
      }
      if (str.equals("")){
         return "(0 bins)";
      }
      return str;
   }

   public ArrayList<BayesianBin> get_bins(){
      return bins;
   }
   
}
