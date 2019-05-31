package inference_engine;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;
import static java.lang.System.out;

public class BayesianBin extends Bin {

   HashMap<String, HashMap<String, Double>> priors; //prior P(A)
   HashMap<String, HashMap<String,Double[]>> conditional_priors; //P(B|A)
   HashMap<String, HashMap<String, Double[]>> cumulative_probabilities;
   int instance_count = 0;
   int total = 0;
   boolean debug = false;
   
   public BayesianBin(String template, HashMap<String, HashMap<String, Double>> priors){
      super(template);
      this.priors = priors;
      this.conditional_priors = new HashMap<String, HashMap<String,Double[]>>();
   }
   
   public BayesianBin(String type, String template, HashMap<String, HashMap<String, Double>> priors){
      super(type, template);
      this.priors = priors;
      this.conditional_priors = new HashMap<String, HashMap<String,Double[]>>();
   }
   
   public void update(String event_type, String event_val){
      if(debug) out.format("\nUpdating %s:%s bin %s %s%n", this.type, this.template, event_type, event_val);
      HashMap<String,Double[]> event_conditional_priors = null;
      event_conditional_priors = conditional_priors.get(event_type);
      if(event_conditional_priors ==  null){
         event_conditional_priors = new HashMap<String, Double[]>();
      }
      try{
         //out.format("Retrieving array for %s%n", event_val);
         Double[] d = event_conditional_priors.get(event_val);
         //out.println("Updating "+double_array_toString(d));
         d[0] = ++d[0]; d[1] = ++d[1];
         this.conditional_priors.put(event_type, event_conditional_priors);
      } catch(NullPointerException ex){
         //if(BayesianEngine.debug) out.println("Caught null pointer");
         Double[] d = {1.0,1.0};
         event_conditional_priors.keySet();
         event_conditional_priors.put(event_val, d);
         this.conditional_priors.put(event_type, event_conditional_priors);
      }
      //out.println("Updated "+event_type+" "+event_val);
      if(BayesianEngine.debug) out.println("conditional_priors: "+conditional_priors_toString());
      //return ++num_samples;
      
   }
   
   public int update_count(int i){
      this.total = i;
      return ++num_samples;
      //return ++num_samples;
   }
   
   public void set_total(int i){
      this.total = i;
   }
   
   public void set_priors(HashMap<String, HashMap<String, Double>> priors){
      this.priors = priors;
   }
   
   public void set_cumulative_probabilities(HashMap<String, HashMap<String, Double[]>> cumulative_probabilities){
      this.cumulative_probabilities = cumulative_probabilities;
   }

   public double get_event_probability(Event e){
      // get conditional: P(B|A)
      /*System.out.println("\nBayesianBin "+this.template+" get_event_probability:"+e.type+" "+e.sample);
      System.out.println("conditional_priors: "+conditional_priors_toString());
      System.out.println(conditional_priors.get(e.type));
      System.out.println(double_array_toString(conditional_priors.get(e.type).get(e.sample)));*/
      Double[] d = conditional_priors.get(e.var_name).get(e.val);
      /*out.println("d[0]"+d[0].doubleValue());
      out.println("d[1]"+d[1].doubleValue());
      out.println("instance_count:"+instance_count);
      out.println("total:"+total);
      /*out.println("e.num_samples "+e.num_samples);
      out.println("this.num_samples "+this.num_samples);
      out.println("e.num_samples / (double) this.num_samples "+(e.num_samples / (double) this.num_samples));*/
      //out.println(d[2].doubleValue());
      // get prior: P(A)
      double p_A = priors.get(e.var_name).get(e.val);
      double p_BgivenA = (d[0]/num_samples) / (double) total;
      //double p_BgivenA = (d[0]/instance_count) / p_A;
      double p_B = d[1]/(double)total;
      out.format("p_BgivenA:%s   p_A:%s   p_B:%s%n", p_BgivenA, p_A, p_B);
      /*out.println(e.type+" "+priors.get(e.type));
      out.println(e.sample);*/
      //return posterior conditional
      //return e.num_samples / (double) this.num_samples;
      return p_A * p_BgivenA / p_B;
   }
   
   public double get_event_probability(double p_A, double p_BgivenA, double p_B){
      return (p_A * p_BgivenA) / p_B;
   }
   
   public void set_trace_total(int i ){
      this.total = i;
   }
   
   public String toString(String probability){
      sort_events();
      out.println("\nCONDITIONAL PRIORS FOR BIN "+type+"::"+template+":\n"+conditional_priors_toString());
      String str = this.type+" "+this.template+" ("+this.num_samples+") "+probability;
      //out.println("bin string: "+str);
      HashMap<String, String> event_groups = new HashMap<String,String>();
      //out.println("number of events:"+bin_events.size());
      //out.println("conditional_priors:"+conditional_priors_toString());
      Set<String> keys = conditional_priors.keySet();
      for (String key : keys){
         HashMap<String,Double[]> pBA_map = conditional_priors.get(key);
         //out.println(x.get);
         str += "\n\t"+key ;//+":: ";
         Set<String> keys2 = pBA_map.keySet();
         for(String key2 : keys2){
            //out.format("key:%s   key2:%s%n",key,key2);
            Double[] dd = pBA_map.get(key2);
            double p_BA = dd[0] / this.num_samples;
            //out.format("%nkey:%s key2:%s%n",key,key2);
            Double[] d = cumulative_probabilities.get(key).get(key2);
            //double p_A = d[0]/d[1];
            double p_A = priors.get(key).get(key2);
            double p_B = num_samples/(double)total;
            if(debug) str+="\t\t"+String.format("p_A:%f p_BA:%f p_B:%f%n", p_A, p_BA, p_B);
            //out.format("",d[0],d[1],);
            str+= "\n\t\t"+key2+":\t"+String.format("%.00f%%",(get_event_probability(p_A, p_BA, p_B)*100));
         }
         //get cumulative prob of A and B (B comes from bin)
      }
      return str;
   }
   
   public static String double_array_toString(Double[] double_array){
      String str = "[";
      for (Double dd : double_array){
         str += dd.doubleValue() + " ";
      }
      str += "]";
      return str;
   }
   
   
   public String conditional_priors_toString(){
      String str = "{";
      Set<String> keys = conditional_priors.keySet();
      for (String s : keys){
         str += " "+s+":{";
         HashMap<String, Double[]> hash = conditional_priors.get(s);
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
      return str;
   }
   
}
