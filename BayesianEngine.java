import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.logging.Logger;
import static java.lang.System.out;

public class BayesianEngine extends BasicEngine {
   
   HashMap<String, HashMap<String, Double>> priors = null;
   ArrayList<BayesianGiven> givens = new ArrayList<BayesianGiven>();
   HashMap<String, HashMap<String, Double[]>> cumulative_probabilities = null;
   ArrayList<String> vars_of_interest = new ArrayList<String>();
   
   public static final Logger debugBayesianEngine = Logger.getLogger("BayesianEngine");
   public BayesianEngine(Object[][] csv_array, ArrayList<String> givens,
                      ArrayList<String> events, HashMap<String, HashMap<String, Double>> priors){
      super(csv_array, givens, events);
      this.priors = priors;
      //out.println("constructor priors: " + priors);
   }
   
   public void build_givens(){
      out.println("priors: " + priors);
      for(String s : givens_vars) {
         System.out.format("%s%n", s.toString());
         System.out.format("%s%n", priors.get(s));
         out.println(priors.get(s));
         givens.add(new BayesianGiven(s.toString(), priors));
      }
   }
   
   public void build_vars_of_interest(){
      vars_of_interest.addAll(givens_vars);
      vars_of_interest.addAll(events_vars);
   }
   
   public void initialize_cumulative_var_probabilities(){
      cumulative_probabilities = new HashMap<String, HashMap<String, Double[]>>();
      for (int i = 0; i < csv_array[0].length; i++){
         cumulative_probabilities.put((String)csv_array[0][i], new HashMap<String, Double[]>());
      }
   }
   
   public void loop_through_trace(){
      debugBayesianEngine.info("\nBegin looping through trace");
      debugBayesianEngine.info("Length of trace: "+csv_array.length);
      debugBayesianEngine.info("Trace variables: "+csv_array[0].toString());
      Object[] row;
      build_givens();
      initialize_cumulative_var_probabilities();
      build_vars_of_interest();
      for (int i = 1; i < csv_array.length; i++) {
         out.println("\n\nLoop "+i);
         row = csv_array[i];
         int given_count = 0;
         BayesianBin b = null;
         boolean bin_updated = false;
         for (String s : vars_of_interest){
            
         }
         for (String e: events_vars){
            //get only event vars
            int event_index = get_var_index(e);
            String event_val = (String) row[event_index];
            // update cumulative probabilities
            update_cumulative_probabilites(e, event_val, i);
            //System.out.println(e+":"+row[event_index]);
            //debugBayesianEngine.info(e+":"+row[event_index]);
            for (BayesianGiven g: givens) {
               // update givens values
               int given_index = get_var_index(g.name);
               String given_val = (String) row[given_index];
               //System.out.println("Given "+g.name+" index in csv:"+given_index);
               //System.out.println(g.name+":"+given_val);
               // check for existing bin
               b = g.contains_bin(given_val);
               
               if(b != null && !bin_updated){
                  // add to existing bin
                  update_cumulative_probabilites(g.name, given_val, i);
                  b.update_count(i);
                  out.println("Updated "+b.template+" bin count to "+b.total);
               } else if(b == null) {
                  // create new bin to add to
                  update_cumulative_probabilites(g.name, given_val, i);
                  b = g.add_bin(g.name, given_val);
               }
               
               //update events in bins
               Event new_event = new Event(event_val);
               Event bin_event = b.contains_event(new_event);
               if(bin_event != null){
                  bin_event.update();
                  //update bin probabilities: joint & reversed given
                  // event_type, event_value, [b_count, b+a_count, a_count, total]
                  b.update(e, event_val);
               } else {
                  //System.out.println("updating event in bin "+b.template);
                  b.add_event(new Event(event_val, e));
               }
            }
            bin_updated = true;
         }
         print_cumulative_probabilities();
      }
      out.println("\nFINISHED TRACE");
      print_cumulative_probabilities();
      //System.exit(0);
      for (int i = 0; i< givens.size(); i++){
         out.println(givens.get(i)+" \n\n");
      }
   } // end loop_through_trace()

   
   public void update_cumulative_probabilites(String type, String val, int i){
      HashMap<String, Double[]> cumulative_probability = null;
      try{
         cumulative_probability = cumulative_probabilities.get(type);
         Double[] d_array = cumulative_probability.get(val);
         d_array[0]++; d_array[1] = (double)i;
         cumulative_probability.put(val, d_array);
      } catch(NullPointerException ex){
         cumulative_probability = new HashMap<String, Double[]>();
         Double[] d_array = {1.0, (double)i};
         cumulative_probability.put(val, d_array);
      }
      cumulative_probabilities.put(type, cumulative_probability);
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
      str += "}";
      out.println( str);
   }
   
}