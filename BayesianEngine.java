import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Set;
import java.util.logging.Logger;
import static java.lang.System.out;

public class BayesianEngine extends BasicEngine {
   
   HashMap<String, HashMap<String, Double>> priors = null;
   ArrayList<BayesianGiven> givens = new ArrayList<BayesianGiven>();
   HashMap<String, HashMap<String, Double[]>> cumulative_probabilities = null;
   HashSet<String> vars_of_interest = new HashSet<String>();
   int trace_total;
   
   public static final Logger debugBayesianEngine = Logger.getLogger("BayesianEngine");
   public BayesianEngine(Object[][] csv_array, ArrayList<String> givens,
                      ArrayList<String> events, HashMap<String, HashMap<String, Double>> priors){
      super(csv_array, givens, events);
      this.priors = priors;
      //out.println("constructor priors: " + priors);
   }
   
   public void build_givens(){
      out.println("\nBUILD GIVENS\npriors: " + priors);
      for(String s : givens_vars) {
         System.out.format("%s%n", s.toString());
         System.out.format("%s%n", priors.get(s));
         out.println(priors.get(s));
         this.givens.add(new BayesianGiven(s.toString(), priors));
      }
   }
   
   public void build_vars_of_interest(){
      vars_of_interest.addAll(givens_vars);
      vars_of_interest.addAll(events_vars);
      out.print("vars_of_interest:");
      out.println(vars_of_interest);
   }
   
   public void initialize_cumulative_var_probabilities(){
      cumulative_probabilities = new HashMap<String, HashMap<String, Double[]>>();
      for (int i = 0; i < csv_array[0].length; i++){
         cumulative_probabilities.put((String)csv_array[0][i], new HashMap<String, Double[]>());
      }
   }
   
   public void loop_through_trace(){
      debugBayesianEngine.info("Begin looping through trace");
      //debugBayesianEngine.info("Length of trace: "+csv_array.length);
      //debugBayesianEngine.info("Trace variables: "+csv_array[0]));
      Object[] row;
      build_givens();
      initialize_cumulative_var_probabilities();
      build_vars_of_interest();
      for (int i = 1; i < csv_array.length; i++) {
         out.println("\n\nLoop "+i);
         trace_total = i;
         row = csv_array[i];
         int given_count = 0;
         BayesianBin b = null;
         boolean bin_updated = false;
         // update P(A) for all vars
         for (String s : vars_of_interest){
            int event_index = get_var_index(s);
            String event_val = (String) row[event_index];
            update_cumulative_probabilites(s, event_val, i);
         }
         out.println("Updated cumulative probabilities:");
         print_cumulative_probabilities();
         
         for (String e: events_vars){
            //get only event vars
            int event_index = get_var_index(e);
            String event_val = (String) row[event_index];
            for (BayesianGiven g: this.givens) {
               // update givens values
               int given_index = get_var_index(g.name);
               String given_val = (String) row[given_index];
               System.out.println("Given "+g.name+" index in csv:"+given_index);
               //System.out.println(g.name+":"+given_val);
               // check for existing bin
               b = g.contains_bin(given_val);
               if(b != null && !bin_updated){
                  // add to existing bin
                  b.update_count(i);
                  //out.println("Updated "+b.template+" bin count to "+b.total);
               } else if(b == null) {
                  // create new bin to add to
                  b = g.add_bin(g.name, given_val);
                  //out.println("Updated "+b.template+" bin count to "+b.total);//+"for BayesianGiven "+g.name);
               }
               //update events in bins
               for (String ev: events_vars){
                  event_index = get_var_index(ev);
                  event_val = (String) row[event_index];
                  Event new_event = new Event(ev, event_val);
                  Event bin_event = b.contains_event(new_event);
                  if(bin_event != null){
                     bin_event.update();
                     //update bin probabilities: joint & reversed given
                     // event_type, event_value, [b_count, b+a_count, a_count, total]
                     b.update(ev, event_val);
                  } else {
                     //System.out.println("updating event in bin "+b.template);
                     b.add_event(new Event(ev, event_val));
                  }
               }
            }
            bin_updated = true;
         }
      }
      out.println("\nFINISHED TRACE");
      print_cumulative_probabilities();
      //System.exit(0);
      for (int i = 0; i< givens.size(); i++){
         givens.get(i).set_total(trace_total);
         out.println(givens.get(i)+" \n\n");
      }
   } // end loop_through_trace()

   
   public void update_cumulative_probabilites(String type, String val, int i){
      HashMap<String, Double[]> cumulative_probability = null;
      try{
         cumulative_probability = cumulative_probabilities.get(type);
         //out.println("Retrieved " +cumulative_probability_toString(cumulative_probability)+" from key "+type);
         Double[] d_array = cumulative_probability.get(val);
         d_array[0] = ++d_array[0]; d_array[1] = (double)i;
         cumulative_probability.put(val, d_array);
      } catch(NullPointerException ex){
         //cumulative_probability = new HashMap<String, Double[]>();
         Double[] d_array = {1.0, (double)i};
         cumulative_probability.put(val, d_array);
      }
      cumulative_probabilities.put(type, cumulative_probability);
      //update all i's
      Set<String> keys1 = cumulative_probabilities.keySet();
      for(String k1 : keys1){
         cumulative_probability = cumulative_probabilities.get(k1);
         Set<String> keys2 = cumulative_probability.keySet();
         for(String k2:keys2){
            Double[] d = cumulative_probability.get(k2);
            d[1] = (double) i;
            cumulative_probability.put(k2, d);
         }
      }
   }
   
   
   public void print_cumulative_probabilities(){
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
      str += "}\n";
      out.println( str);
   }
   
   public void print_cumulative_probability(HashMap<String,Double[]> cumulative_probability){
      Set<String> keys = cumulative_probability.keySet();
      String str = "{";
      for (String s : keys){
         str += s+": " +BayesianBin.double_array_toString(cumulative_probability.get(s)) ;
      }
      str+= "}";
      out.print(str);
   }
   
   public String cumulative_probability_toString(HashMap<String,Double[]> cumulative_probability){
      Set<String> keys = cumulative_probability.keySet();
      String str = "{";
      for (String s : keys){
         str += s+":"+BayesianBin.double_array_toString(cumulative_probability.get(s)) ;
      }
      str+= "}";
      return str;
   }
   
}