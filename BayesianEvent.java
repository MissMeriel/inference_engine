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
   boolean debug = true;
   
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
   
   /*public void update_conditionals(Event ev){
      Event e;
      try{
         e = pBAs.get(ev.var_name);
         e.update();
         pBAs.put(ev.var_name, e);
      } catch(NullPointerException ex){
         pBAs.put(var_name, ev);
      }
   }*/
   
   public void update_conditionals(ArrayList event_values){
      int i = 0;
      Double d;
      
      for(String voi: vars_of_interest){
         Object[] temp = new Object[]{0.0, voi};
         if(!voi.equals(this.var_name)){
            try{
               temp = get_pBA(voi, event_values.get(i).toString());
            } catch(NullPointerException ex){
               out.format("update_conditionals: Caught null ptr from get_pBA");
            }
            try{
               double dbl = ((Double)temp[0]).doubleValue(); dbl++;
               out.format("update_conditionals: Getting %s pBA%n",voi);
               HashMap<String, Double> pBA = pBAs.get(voi);
               out.format("update_conditionals: Got %s pBA: %s%n",voi, pBA);
               pBA.put(event_values.get(i).toString(), dbl); //change later
               out.format("update_conditionals: Put %s:%s into %s:%s %n", voi, event_values.get(i).toString(), temp[1].toString(), temp[0].toString());
            } catch(NullPointerException ex){
               pBAs.get(voi).put(event_values.get(i).toString(), 1.0);
               out.format("update_conditionals: Caught null ptr except inside update_conditionals%nPut %s:%s into %s:%s %n", voi, event_values.get(i).toString(),voi, event_values.get(i).toString());
               out.println(ex.getLocalizedMessage());
            }
         }
         i++;
      }
      if(debug) out.println("Updated conditionals in "+var_name+"="+val+": "+pBAs);
   }
   
   /**
    * @return Object[] return_val
    * return_val[0] == pBA count; return_val[1] == pBA name
    **/
   public Object[] get_pBA(String voi_name, String event_val){
      double d = Double.MAX_VALUE; //goes into r[0]
      Double voi_threshold = 0.0;
      out.format("Inside get_pBA(%s, %s)%n", voi_name, event_val);
      voi_threshold = new Double(Global.thresholds.get(voi_name));
      print_thresholds();
      out.format("Inside get_pBA; got %s threshold %f%n", voi_name, voi_threshold);
      Set<String> keys_test = Global.thresholds.keySet();
      for(String key : keys_test){
         out.format("%s equals %s: %s%n", key, voi_name, key.equals(voi_name));
      }
      Object[] r = new Object[2];
      try{
         d = pBAs.get(voi_name).get(event_val);
         out.format("get_pBA: Got %f from pBAs%n", d);
         r = new Object[]{d, event_val.toString()};
         return r;
      } catch (NullPointerException ex) {
         //iterate over pBAs looking for closest one
         HashMap<String, Double> pBA_counts = pBAs.get(voi_name);
         out.format("get_pBA: Got hashmap from pBAs: %s%n", pBA_counts);
         Set<String> keys = pBA_counts.keySet();
         RawType type_enum = Global.types.get(voi_name);
         for(String key : keys){
            d = pBA_counts.get(key);
            out.format("get_pBA: Got key %s count %f from pBAs%n", key, d);
            switch(type_enum){
               case DOUBLE:
                  double dbl = Double.parseDouble(event_val);
                  double key_val = Double.parseDouble(key);
                  if(Fuzzy.eq(dbl, key_val, voi_threshold.doubleValue())) {
                     r[0] = ++d; r[1] = key;
                     return r;
                  }
                  break;
               case INT:
                  int i = Integer.parseInt(event_val);
                  int key_val_int = Integer.parseInt(key);
                  if(abs(i - key_val_int) < voi_threshold.doubleValue()){
                     r[0] = ++d; r[1] = key;
                     return r;
                  }
                  break;
               case STRING:
                  if(event_val.equals(key)){
                     r[0] = ++d; r[1] = event_val;
                  }
                  break;
            }
         }
         out.format("d=%f%n",d);
         out.format("d==Double.MAX_VALUE: %s%n",(d==Double.MAX_VALUE));
         if(d == Double.MAX_VALUE){
            out.format("%s:%s not found in existing events for %s:%s%n",voi_name, event_val, this.var_name, this.val);
            throw new NullPointerException(String.format("%s:%s not found in existing events for %s:%s%n",voi_name, event_val, this.var_name, this.val));
         }
      }
      return r;
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
   
   
   public String generate_bayesian_probability(HashMap<String, HashMap<String, Double[]>> cumulative_probabilities){
      String str = "";
      Set<String> keys1 = pBAs.keySet();
      for(String key1 : keys1){
         HashMap<String, Double> val_map = pBAs.get(key1);
         Set<String> keys2 = val_map.keySet();
         for(String key2 : keys2){
            //Double[] A_arr = cumulative_probabilities.get(var_name).get(val.toString());
            Double[] A_arr = cumulative_probabilities.get(key1).get(key2);
            double pA = (double) num_samples / A_arr[1].doubleValue();
            double pB = A_arr[0].doubleValue() / A_arr[1].doubleValue();
            Double pBA = val_map.get(key2) / (double) num_samples;
            double pAB = (pA * pBA) / pB;
            str += String.format("\nP(%s=%s|%s=%s):%.2f", var_name, val.toString(), key1, key2, pAB);
         }
      }
      return str;
   }
   
   @Override
   public String toString(){
      String str = String.format("BayesianEvent %s:%s p_A:%.2f", var_name, val.toString(), p_A);
      str+= " pBAs: " + pBAs;
      return str;
   }
   
}