import java.util.HashMap;
import java.util.ArrayList;
import java.util.logging.Logger;

public class BayesianGiven extends Given {

   HashMap<String, HashMap<String, Double>> priors;
   public ArrayList<BayesianBin> bins = new ArrayList<BayesianBin>();
   int trace_total;
   
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
      double total = 0.0;
      for (Bin b : bins){
         total += b.num_samples;
      }
      return bin.instance_count/(double)bin.total;
   }
   
   public void set_total(int i ){
      trace_total = i;
   }
   
   @Override
   public String toString(){
      String str = "";
      //System.out.println(bins.size());
      for (BayesianBin b : bins){
         b.set_trace_total(trace_total);
         String prob = String.format("%.0f%%", get_bin_probability(b)*100);
         str += b.toString(prob)+"\n";
      }
      return str;
   }

}