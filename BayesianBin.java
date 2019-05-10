import java.util.HashMap;
import java.util.ArrayList;
import java.util.Set;
import java.util.logging.Logger;
import static java.lang.System.out;

public class BayesianBin extends Bin {

   HashMap<String, HashMap<String, Double>> priors; //P(A)
   HashMap<String, HashMap<String,Double[]>> conditional_priors; //P(B|A)
   int instance_count = 0;
   int total = 0;
   
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
   
   public int update(String event_type, String event_val){
      out.format("\nUpdating %s:%s bin %s %s%n", this.type, this.template, event_type, event_val);
      try {
         HashMap<String,Double[]> event_conditional_priors = conditional_priors.get(event_type);
         out.format("Retrieving array for %s%n", event_val);
         Double[] d = event_conditional_priors.get(event_val);
         out.println("Updating "+double_array_toString(d));
         ++d[0]; ++d[1];
         this.conditional_priors.put(event_type, event_conditional_priors);
      } catch(NullPointerException ex){
         out.println("Caught null pointer");
         //HashMap<String,Double[]> event_conditional_priors = conditional_priors.get(event_type);
         Double[] d = {1.0,1.0};
         HashMap<String, Double[]> event_conditional_priors = new HashMap<String, Double[]>();
         event_conditional_priors.put(event_val, d);
         this.conditional_priors.put(event_type, event_conditional_priors);
      }
      out.println(conditional_priors_toString());
      return ++num_samples;
   }
   
   public int update_count(int i){
      this.total = i;
      return ++instance_count;
   }

   public double get_event_probability(Event e){
      // get conditional: P(B|A)
      System.out.println("\nBayesianBin "+this.template+" get_event_probability:"+e.type+" "+e.sample);
      System.out.println(conditional_priors_toString());
      System.out.println(conditional_priors.get(e.type));
      System.out.println(double_array_toString(conditional_priors.get(e.type).get(e.sample)));
      Double[] d = conditional_priors.get(e.type).get(e.sample);
      out.println(d[0].doubleValue());
      double p_BgivenA = (d[0]/d[2]) / d[1];
      // get prior: P(A)
      double p_A = d[1];
      //return posterior conditional
      return e.num_samples / (double) this.num_samples;
   }
   
      
   public String toString(String probability){
      sort_events();
      String str = this.type+" "+this.template+" ("+this.num_samples+") "+probability;
      HashMap<String, String> event_groups = new HashMap<String,String>();
      for (Event e : bin_events){
         if(event_groups.get(e.type) == null){
            event_groups.put(e.type, "\n\t\t"+e.type +" ");
         }
      }
      for (Event e : bin_events){
         String event_group_string = event_groups.get(e.type);
         event_group_string += "\n\t\t\t"+e.toString() +" "+ String.format("%.00f%%",get_event_probability(e)*100);
         event_groups.put(e.type, event_group_string);
      }
      for (String k : event_groups.keySet()){
         str += event_groups.get(k);
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