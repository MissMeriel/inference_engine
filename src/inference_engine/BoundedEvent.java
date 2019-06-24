package inference_engine;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Set;
import static java.lang.System.out;
import static java.lang.Math.abs;
import java.util.function.Predicate;

public class BoundedEvent<T> extends BayesianEvent<T>{
   double lower_bound = Double.MIN_VALUE;
   double upper_bound = Double.MAX_VALUE;
   Predicate<Object> tester = (Object o) -> {Double x = Double.valueOf(o.toString()); return x > this.lower_bound && x < this.upper_bound;};
   String id = null;
   
   public BoundedEvent(String var_name, T val, double p_A,  double lower_bound, double upper_bound){
      super(var_name, val, p_A);
      this.lower_bound = lower_bound;
      this.upper_bound = upper_bound;
   }
   
   public BoundedEvent(String var_name, T val, double p_A, double bound, Bound bound_type){
      super(var_name, val, p_A);
      switch(bound_type){
         case UPPER:
            upper_bound = bound;
            break;
         case LOWER:
            lower_bound = bound;
            break;
      }
   }
   
   public BoundedEvent(String var_name, String id, double p_A, Predicate<Object> tester){
      super(var_name, null, p_A);
      this.tester = tester;
      this.id = id;
   }
   
   public boolean check_bounds(Object o){
      return tester.test(o);
   }
   
   @Override
   public String generate_bayesian_probability(HashMap<String, HashMap<String, Double[]>> cumulative_probabilities){
      debug = false;
      //if(true) out.format("%nENTER generate_bayesian_probability: for %s %s",var_name, id);
      String str = "";
      Set<String> keys1 = pBAs.keySet();
      for(String key1 : keys1){
         HashMap<String, Double> val_map = (HashMap<String, Double>) pBAs.get(key1);
         Set<String> keys2 = val_map.keySet();
         for(String key2 : keys2){
            //Double[] A_arr = cumulative_probabilities.get(var_name).get(val.toString());
            if(debug) {
               out.format("%ngenerate_bayesian_probability: cumulative_probabilities.get(%s).get(%s)=%n", key1, key2);
               out.println("CUMULATIVE PROBABILITIES:");
               print_cumulative_probabilities(cumulative_probabilities);
            }
            Double[] A_arr = get_cumulative_probability(key1, key2, cumulative_probabilities);
            if(debug) out.println("A_arr for "+key1+":"+key2+" == null? "+(A_arr == null));
            //if(debug) out.format("%.2f / %.2f %n", A_arr[0], A_arr[1]);
            if(debug) Driver.print_priors(Global.priors);
            //double pB = A_arr[0].doubleValue() / A_arr[1].doubleValue();
            if(debug) out.format("total_probabilities.get(%s).get(%s)%n",key1,key2);
            double pB = total_probabilities.get(key1).get(key2);
            if(debug) out.format("pB = %.3f / %.3f = %.3f%n", A_arr[0].doubleValue(), A_arr[1].doubleValue(), pB);
            double pA = (double) get_prior(var_name, id)[0];
            if(debug) {
               out.format("calculating probability for P(%s=%s|%s=%s)%n",var_name, id, key1, key2);
               out.format("pA = get_prior(%s, %s) = %.2f%n", var_name, id, pA);
            }
            double actual_pA = ((double)num_samples) / A_arr[1].doubleValue();
            if(debug) out.format("actual_pA = %.3f / %.3f = %.3f%n", ((double)num_samples), A_arr[1].doubleValue(), actual_pA);
            //Double pBA = (val_map.get(key2) / (double) A_arr[1].doubleValue()) / actual_pA;
            Double pBA = (val_map.get(key2) / ((double)num_samples));
            if(debug) {
               out.format("pBAs: ");
               print_pBAs();
               out.format("pBA = (%.3f / %.3f) / %.3f = %.3f%n", val_map.get(key2), (double) A_arr[1].doubleValue(), actual_pA, pBA);
            }
            double pAB = (pA * pBA) / pB;
            RawType rawtype1 = Global.types.get(var_name);
            switch(rawtype1){
               case INT:
               case DOUBLE:
               case STRING:{
                  str += String.format("\nP(%s|", id);
                  break;}
               case INTEXP:
               case DOUBLEEXP:
               case STRINGEXP: {
                  str += String.format("\nP(%s|", id);
                  break;}
               case INTDELTA:
               case DOUBLEDELTA: {
                  str += String.format("\nP(rate of change of %s|", id);
                  break;}
            }
            RawType rawtype2 = Global.types.get(key1);
            switch(rawtype2){
               case INT:
               case DOUBLE:
               case STRING:{
                  str += String.format("%s=%s) ",key1,key2);
                  break;}
               case INTEXP:
               case DOUBLEEXP:
               case STRINGEXP: {
                  str += String.format("%s) ", key2);
                  break;}
               case INTDELTA:
               case DOUBLEDELTA: {
                  str += String.format("rate of change of %s) ", key2);
                  break;}
            }
            if(true)  str += String.format("= (%.3f * %.3f) / %.3f ", pA, pBA, pB);
            str += String.format("= %.3f", pAB);
            if(pAB > 1.0){
               out.println(str);
               out.println("^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^^");
               out.println("PROBABILITY > 1 (count="+ (++gt_one_count) +")");//System.exit(0);
               out.format("calculating probability for P(%s=%s|%s=%s)%n",var_name, val.toString(), key1, key2);
               out.format("pB = %.3f / %.3f = %.3f%n", A_arr[0].doubleValue(), A_arr[1].doubleValue(), pB);
               /*System.out.print("PRIORS: ");*/ Driver.print_priors(Global.priors);
               System.out.print("CUMULATIVE_PROBABILITIES: "); print_cumulative_probabilities(cumulative_probabilities);
               out.format("pA = get_prior(%s, %s) = %.2f%n", var_name, val.toString(), pA);
               out.format("actual_pA = %.3f / %.3f = %.3f%n", ((double)num_samples), A_arr[1].doubleValue(), actual_pA);
               out.format("pBAs: ");
               print_pBAs();
               out.format("pBA = (%.3f / %.3f) / %.3f = %.3f%n", val_map.get(key2), (double) A_arr[1].doubleValue(), actual_pA, pBA);
               out.println("\n\n");
            }
         }
      }
      return str;
      //return regroup_probabilities_by_given(str);
   }

   
   @Override
   public boolean equals(Object o){
      if(o instanceof BoundedEvent){
         BoundedEvent be = (BoundedEvent) o;
         out.format("BoundedEvent equals: this.equals(be):%n");
         out.println("\t"+this.toString());
         out.println("\t"+(be.toString()));
         boolean nameeq = (this.var_name.equals(be.var_name));
         boolean ideq = this.id.equals(be.id);
         boolean boundeq = lower_bound == be.lower_bound && upper_bound == be.upper_bound;
         return nameeq && ideq && boundeq ;
      } else {
         return false;
      }
   }
   
   @Override
   public String toString(){
      String str = String.format("BoundedEvent %s:%s p_A:%.2f", var_name, this.id, p_A);
      str+= " pBAs: " + pBAs;
      str += " num_samples: " +num_samples;
      //str += " tester: "+tester.toString();
      return str;
   }
}
