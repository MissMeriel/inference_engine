package inference_engine;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Set;
import static java.lang.System.out;
import static java.lang.Math.abs;

public class BayesianEvent<T> extends TypedEvent{
   
   Prior prior_attribution = Prior.PROB_A; //default is PROB_A
   HashMap<String, HashMap<String, Double>> pBAs = null;
   double p_A;
   double p_B;
   TreeSet<String> vars_of_interest = new TreeSet<String>();
   boolean debug = false;
   static int gt_one_count = 0;
   /**
    * default constructor: p_A from .priors file
    **/
   public BayesianEvent(String var_name, T val, double p_A, TreeSet<String> vars_of_interest){
      super(var_name, val);
      this.p_A = p_A;
      this.vars_of_interest = vars_of_interest; //consider removing this.var_name
      initialize_pBAs();
   }
   
   /**
    * configuration constructor: p_A from .priors file
    **/
   public BayesianEvent(String var_name, T val, Prior prior_attribution){
      super(var_name, val);
      this.prior_attribution = prior_attribution;
   }
   
   public void initialize_pBAs(){
      pBAs = new HashMap<String, HashMap<String, Double>>();
      for(String voi : vars_of_interest){
         if(!voi.equals(this.var_name)){
            pBAs.put(voi, new HashMap<String, Double>());
         }
      }
   }

   
   public void update_conditionals(ArrayList event_values, boolean debug){
      int i = 0;
      Double d;
      //this.debug = debug;
      for(String voi: vars_of_interest){
         Object[] temp = new Object[]{0.0, event_values.get(i)};
         if(!voi.equals(this.var_name)){
            try{
               temp = get_pBA(voi, event_values.get(i).toString());
               if(debug) out.format("update_conditionals: temp from get_pBA=%s%n", temp_toStr(temp));
            } catch(NullPointerException ex){
               if(debug) out.format("update_conditionals: Caught null ptr from get_pBA%n");
            }
            try{
               if(debug) out.format("update_conditionals: temp=%s%n", temp_toStr(temp));
               double dbl = ((Double)temp[0]).doubleValue(); dbl++;
               if(debug) out.format("update_conditionals: Getting %s pBA%n",voi);
               HashMap<String, Double> pBA = pBAs.get(voi);
               if(debug)out.format("update_conditionals: Got %s pBA: %s%n",voi, pBA);
               pBA.put(temp[1].toString(), dbl); //change later
               pBAs.put(voi, pBA);
               if(debug)out.format("update_conditionals: Put %s:%s into %s:%s %n", voi, event_values.get(i).toString(), temp[1].toString(), temp[0].toString());
            } catch(NullPointerException ex){
               pBAs.get(voi).put(event_values.get(i).toString(), 1.0);
               if(debug) out.format("update_conditionals: Caught null ptr inside update_conditionals%nPut %s:1.0 into %s:%s %n", event_values.get(i).toString(),voi, event_values.get(i).toString());
               if(debug) out.println(ex.getLocalizedMessage());
            }
         }
         i++;
         if(debug) out.println("Updated conditionals in "+var_name+"="+val+": "+pBAs);
      }
   }
   
   
   public String temp_toStr(Object[] temp){
      return String.format("[ %s, %s]", temp[0].toString(), temp[1].toString());
   }
   
   
   /**
    * @return Object[] return_val
    * return_val[0] == pBA count; return_val[1] == pBA name
    **/
   public Object[] get_pBA(String voi_name, String event_val){
      double event_val_count = Double.MAX_VALUE; //goes into r[0]
      Double voi_threshold = 0.0;
      if(debug) {
         out.format("Inside get_pBA(%s, %s)%n", voi_name, event_val);
         out.format("get_pBAs: pBAs:");
         print_pBAs();
         out.format("get_pBA: getting %s threshold %n", voi_name);
      }
      try{
         voi_threshold = new Double(Global.thresholds.get(voi_name));
      } catch(NullPointerException ex){
         voi_threshold = Double.MAX_VALUE;
      }
      if(debug) out.format("get_pBA: got %s threshold %f%n", voi_name, voi_threshold);
      Object[] r = new Object[2];
      try{
         event_val_count = pBAs.get(voi_name).get(event_val);
         if(debug) out.format("get_pBA: pBAs.get(%s).get(%s)= %f%n", voi_name, event_val, event_val_count);
         r = new Object[]{event_val_count, event_val.toString()};
         return r;
      } catch (NullPointerException ex) {
         //exact match not found --> iterate over pBAs looking for closest one
         HashMap<String, Double> pBA_counts = pBAs.get(voi_name);
         if(debug) out.format("get_pBA: pBAs.get(%s): %s%n", voi_name, pBA_counts);
         Set<String> keys = pBA_counts.keySet();
         RawType type_enum = Global.types.get(voi_name);
         String closest_match_key = null;
         for(String key : keys){
            event_val_count = pBA_counts.get(key);
            if(debug) out.format("get_pBA: Got key %s count %f from pBAs%n", key, event_val_count);
            switch(type_enum){
               case DOUBLE:
                  double dbl = Double.parseDouble(event_val);
                  double key_val_dbl = Double.parseDouble(key);
                  if(debug) out.format("get_pBA: attempting to parse double %s%n", key);
                  double key_val = Double.parseDouble(key);
                  if(Fuzzy.eq(dbl, key_val, voi_threshold.doubleValue()) && voi_threshold != Double.MAX_VALUE) {
                     if(closest_match_key != null){
                        double closest_match_key_double = Double.parseDouble(closest_match_key);
                        //if(debug) out.format("get_cumulative_probability: abs(%.2f-%.2f) < abs(%.2f-%.2f) = %s%n",dbl,closest_match_key_double,dbl,key_val_dbl,(abs(dbl-closest_match_key_double) < abs(dbl-key_val_dbl)));
                        if(abs(dbl-closest_match_key_double) > abs(dbl-key_val_dbl)){
                           closest_match_key = key;
                        }
                     } else {
                        closest_match_key = key;
                     }
                  } else if(Math.round(dbl) == Math.round(key_val)) {
                     r[0] = event_val_count; r[1] = key;
                     return r;
                  }
                  break;
               case INT:
                  int i = (int) (Double.parseDouble(event_val));
                  int key_val_int = (int) (Double.parseDouble(key));
                  if(Fuzzy.eq(i, key_val_int, voi_threshold.doubleValue()) && voi_threshold != Double.MAX_VALUE){
                     if(closest_match_key != null){
                        double closest_match_key_int = Double.valueOf(closest_match_key);
                        if(abs(i-closest_match_key_int) > abs(i-key_val_int)){
                           closest_match_key = key;
                        }
                     } else {
                        closest_match_key = key;
                     }
                  } else if(i == key_val_int) {
                     r[0] = event_val_count; r[1] = key;
                     return r;
                  }
                  break;
               case STRING:
                  if(event_val.equals(key)){
                     r[0] = event_val_count; r[1] = event_val;
                     return r;
                  }
                  break;
            }
         } //end for keys
         if(debug) {
            out.format("get_pBA: d=%f%n",event_val_count);
            out.format("get_pBA: d==Double.MAX_VALUE: %s%n",(event_val_count==Double.MAX_VALUE));
         }
         if(event_val_count == Double.MAX_VALUE){
            if(debug) out.format("get_pBA: %s:%s not found in existing events for %s:%s%n",voi_name, event_val, this.var_name, this.val);
            throw new NullPointerException(String.format("get_pBA(): %s:%s not found in existing events for %s:%s%n",voi_name, event_val, this.var_name, this.val));
         }
         event_val_count = pBA_counts.get(closest_match_key);
         r[0] = event_val_count; r[1] = closest_match_key;
         return r;
      } //end catch
      //return r;
   }
   
   public void print_cumulative_probabilities(HashMap<String, HashMap<String, Double[]>> cumulative_probabilities){
      //System.out.println(cumulative_probabilities);
      String str = "{";
      Set<String> keys = cumulative_probabilities.keySet();
      for (String s : keys){
         str += " "+s+":{";
         HashMap<String, Double[]> hash = cumulative_probabilities.get(s);
         Set<String> keys2 = hash.keySet();
         for (String s2 : keys2){
            str += s2+":[";
            Double[] d = hash.get(s2);
            
            for (Double dd : d){
               str += dd.doubleValue() + " ";
            }
            str += "]";
         }
         str+="}";
      }
      str += "}";
      out.println( str);
   }
   
   public static String print_thresholds(){
      String return_string = "";
      Set<String> keys = Global.thresholds.keySet();
      out.format("%nTHRESHOLDS:%n");
      for(String str : keys){
         out.format("%s : %s%n", str, Global.thresholds.get(str));
         return_string += String.format("%s : %s%n", str, Global.thresholds.get(str));
      }
      return return_string;
   }
   
   public void print_pBAs(){
      //System.out.println(cumulative_probabilities);
      String str = "{";
      Set<String> keys = pBAs.keySet();
      for (String s : keys){
         str += " "+s+":{";
         HashMap<String, Double> hash = pBAs.get(s);
         Set<String> keys2 = hash.keySet();
         for (String s2 : keys2){
            str += s2+"=";
            Double d = hash.get(s2);
            str += d.doubleValue() + " ";
         }
         str+="}";
      }
      str += "}";
      out.println( str);
   }
   
   
   public String generate_bayesian_probability(HashMap<String, HashMap<String, Double[]>> cumulative_probabilities){
      debug = false;
      if(debug) out.format("%nENTER generate_bayesian_probability: for %s %s",var_name, val.toString());
      String str = "";
      Set<String> keys1 = pBAs.keySet();
      for(String key1 : keys1){
         HashMap<String, Double> val_map = pBAs.get(key1);
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
            double pB = A_arr[0].doubleValue() / A_arr[1].doubleValue();
            if(debug) out.format("pB = %.3f / %.3f = %.3f%n", A_arr[0].doubleValue(), A_arr[1].doubleValue(), pB);
            
            /*HashMap<String,Double> val_map1 = Global.priors.get(var_name);
            Set<String> keys = val_map1.keySet();
            for(String key : keys){
               out.println(val.toString()+".equals("+key+")? "+(val.toString().equals(key)));
            }*/
            double pA = (double) get_prior(var_name, val.toString())[0];
            if(debug) out.format("calculating probability for P(%s=%s|%s=%s)%n",var_name, val.toString(), key1, key2);
            if(debug) out.format("pA = get_prior(%s, %s) = %.2f%n", var_name, val.toString(), pA);
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
            str += String.format("\nP(%s=%s|%s=%s) ", var_name, val.toString(), key1, key2);
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
   }
   
   
   public Double[] get_cumulative_probability(String voi_name, String val, HashMap<String, HashMap<String, Double[]>> cumulative_probabilities){
      Double[] d_arr = new Double[2];
      try{
         d_arr = cumulative_probabilities.get(voi_name).get(val);
         if(d_arr == null){
            throw new NullPointerException();
         }
      } catch(NullPointerException ex1){
         Double voi_threshold = 0.0;
         if(debug) {
            out.format("Inside get_cumulative_probability(%s, %s)%n", voi_name, val);
            out.format("get_cumulative_probability: priors:");
            Driver.print_priors(Global.priors);
            out.format("get_cumulative_probability: getting %s threshold %n", voi_name);
         }
         try{
            voi_threshold = new Double(Global.thresholds.get(voi_name));
         } catch(NullPointerException ex){
            voi_threshold = Double.MAX_VALUE;
         }
         if(debug) out.format("get_cumulative_probability: Null pointer on Global.priors.get(%s).get(%s)%n", var_name, val.toString());
         //exact match not found --> iterate over pBAs looking for closest one
         HashMap<String, Double[]> cp_map = cumulative_probabilities.get(voi_name);
         if(debug) out.format("get_cumulative_probability: priors.get(%s): %s%n", voi_name, cp_map);
         Set<String> keys = cp_map.keySet();
         RawType type_enum = Global.types.get(voi_name);
         String closest_match_key = null;
         for(String key : keys){
            if(debug) out.format("get_cumulative_probability: priors.get(%s).get(%s)%n", voi_name, key);
            switch(type_enum){
               case DOUBLE:
                  double dbl = Double.parseDouble(val);
                  if(debug) out.format("get_cumulative_probability: attempting to parse double key %s%n", key);
                  double key_val = Double.parseDouble(key);
                  if(Fuzzy.eq(dbl, key_val, voi_threshold.doubleValue()) && voi_threshold != Double.MAX_VALUE) {
                     if(closest_match_key != null){
                        double closest_match_key_double = Double.parseDouble(closest_match_key);
                        if(debug) out.format("get_cumulative_probability: abs(%.2f-%.2f) < abs(%.2f-%.2f) = %s%n",dbl,closest_match_key_double,dbl,key_val,(abs(dbl-closest_match_key_double) < abs(dbl-key_val)));
                        if(abs(dbl-closest_match_key_double) > abs(dbl-key_val)){
                           closest_match_key = key;
                        }
                     } else {
                        closest_match_key = key;
                     }
                  } else if(Math.round(dbl) == Math.round(key_val)) {
                     return cumulative_probabilities.get(voi_name).get(key);
                  }
                  break;
               case INT:
                  int i = (int) (Double.parseDouble(val));
                  int key_val_int = (int) (Double.parseDouble(key));
                  if(Fuzzy.eq(i, key_val_int, voi_threshold.doubleValue()) && voi_threshold != Double.MAX_VALUE){
                     return cumulative_probabilities.get(voi_name).get(key);
                  } else if(i == key_val_int) {
                     return cumulative_probabilities.get(voi_name).get(key);
                  }
                  break;
               case STRING:
                  if(val.equals(key)){
                     return cumulative_probabilities.get(voi_name).get(key);
                  }
                  break;
            }
         }
         if(debug) {
            Double[] ret_arr = cumulative_probabilities.get(voi_name).get(closest_match_key);
            out.format("get_cumulative_probability: returning cumulative_probabilities.get(%s).get(%s)= [%.2f, %.2f] %n", voi_name, closest_match_key, ret_arr[0], ret_arr[1]);
         }         
         return cumulative_probabilities.get(voi_name).get(closest_match_key);
      }
      return d_arr;
   }
   
   
   public Object[] get_prior(String voi_name, String val){
      debug = true;
      double event_val_count = Double.MAX_VALUE; //goes into r[0]
      Double voi_threshold = 0.0;
      if(debug) {
         out.format("Inside get_prior(%s, %s)%n", voi_name, val);
         out.format("get_prior: priors:");
         Driver.print_priors(Global.priors);
         out.format("get_prior: getting %s threshold %n", voi_name);
      }
      try{
         voi_threshold = new Double(Global.thresholds.get(voi_name));
      } catch(NullPointerException ex){
         voi_threshold = Double.MAX_VALUE;
      }
      if(debug) out.format("get_prior: got %s threshold %f%n", voi_name, voi_threshold);
      Object[] r = new Object[2];
      try{
         double pA = Global.priors.get(voi_name).get(val);//(double) num_samples / A_arr[1].doubleValue();
         return new Object[]{pA, val};
      } catch(NullPointerException e){
         if(debug/*true*/) out.format("get_prior: Null pointer on Global.priors.get(%s).get(%s)%n", var_name, val.toString());
         //exact match not found --> iterate over pBAs looking for closest one
         HashMap<String, Double> prior_map = Global.priors.get(voi_name);
         if(debug) out.format("get_prior: priors.get(%s): %s%n", voi_name, prior_map);
         if(debug/*true*/) out.format("prior_map null? %s%n", (prior_map == null));
         Set<String> keys = prior_map.keySet();
         RawType type_enum = Global.types.get(voi_name);
         String closest_match_key = null;
         for(String key : keys){
            event_val_count = prior_map.get(key);
            if(debug) out.format("get_prior: priors.get(%s).get(%s) = %f%n", voi_name, key, event_val_count);
            switch(type_enum){
               case DOUBLE:
                  double dbl = Double.parseDouble(val);
                  if(debug) out.format("get_prior: attempting to parse double key %s%n", key);
                  double key_val = Double.parseDouble(key);
                  System.out.format("Fuzzy.eq(%f, %f, %f) = %s && voi_threshold %f != Double.MAX_VALUE = %s%n", dbl, key_val, voi_threshold.doubleValue(), (Fuzzy.eq(dbl, key_val, voi_threshold.doubleValue())), voi_threshold, (voi_threshold != Double.MAX_VALUE));
                  if(Fuzzy.eq(dbl, key_val, voi_threshold.doubleValue()) && voi_threshold != Double.MAX_VALUE) {
                     if(closest_match_key != null){
                        double closest_match_key_double = Double.parseDouble(closest_match_key);
                        if(debug) out.format("get_cumulative_probability: abs(%.2f-%.2f) < abs(%.2f-%.2f) = %s%n",dbl,closest_match_key_double,dbl,key_val,(abs(dbl-closest_match_key_double) < abs(dbl-key_val)));
                        if(abs(dbl-closest_match_key_double) > abs(dbl-key_val)){
                           closest_match_key = key;
                        }
                     } else {
                        closest_match_key = key;
                     }
                  } else if(Math.round(dbl) == Math.round(key_val)) {
                     r[0] = event_val_count; r[1] = key;
                     return r;
                  }
                  break;
               case INT:
                  int i = (int) (Double.parseDouble(val));
                  int key_val_int = (int) (Double.parseDouble(key));
                  if(Fuzzy.eq(i, key_val_int, voi_threshold.doubleValue()) && voi_threshold != Double.MAX_VALUE){
                     r[0] = event_val_count; r[1] = key;
                     return r;
                  } else if(i == key_val_int) {
                     r[0] = event_val_count; r[1] = key;
                     return r;
                  }
                  break;
               case STRING:
                  if(val.equals(key)){
                     r[0] = event_val_count; r[1] = val;
                  }
                  break;
               case INTEXP:
                  break;
               case DOUBLEEXP:
                  break;
            }
         }
         System.out.print("PRIOR MAP: ");
         System.out.println(prior_map);
         System.out.println("closest_match_key: "+closest_match_key);
         event_val_count = prior_map.get(closest_match_key);
         r[0] = event_val_count; r[1] = closest_match_key;
         
         if(debug) {
            out.format("get_prior: d=%f%n",event_val_count);
            out.format("get_prior: d==Double.MAX_VALUE: %s%n",(event_val_count==Double.MAX_VALUE));
         }
         if(event_val_count == Double.MAX_VALUE){
            if(debug) out.format("get_prior: %s:%s not found in existing events for %s:%s%n",voi_name, val, this.var_name, this.val);
            throw new NullPointerException(String.format("get_prior: %s:%s not found in existing events for %s:%s%n",voi_name, val, this.var_name, this.val));
         }
         return r;
      }
      //return r;
   }
   
   
   @Override
   public String toString(){
      String str = String.format("BayesianEvent %s:%s p_A:%.2f", var_name, val.toString(), p_A);
      str+= " pBAs: " + pBAs;
      str += " num_samples: " +num_samples;
      return str;
   }
   
}
