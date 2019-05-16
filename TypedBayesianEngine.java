import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Set;
import java.util.Iterator;
import java.util.logging.Logger;
import static java.lang.System.out;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.Comparator;

public class TypedBayesianEngine extends BasicEngine {
   
   HashMap<String, HashMap<String, Double>> priors = null;
   ArrayList<BayesianGiven> givens = new ArrayList<BayesianGiven>();
   ArrayList<BayesianEvent> bayesian_events = new ArrayList<BayesianEvent>();
   HashMap<String, HashMap<String, Double[]>> cumulative_probabilities = null;
   TreeSet<String> vars_of_interest = new TreeSet<String>();
   HashMap<String, RawType> types = null;
   int trace_total;
   public static boolean debug = true;
   
   public static final Logger debugBayesianEngine = Logger.getLogger("BayesianEngine");
   public TypedBayesianEngine(Object[][] csv_array, ArrayList<String> givens, ArrayList<String> events,
                      HashMap<String, HashMap<String, Double>> priors, HashMap<String, RawType> types){
      super(csv_array, givens, events);
      this.priors = priors;
      this.types = types;
   }
   
   public void build_vars_of_interest(){
      vars_of_interest.addAll(givens_vars);
      vars_of_interest.addAll(events_vars);
      if(debug) out.format("vars_of_interest:%s%n",vars_of_interest);
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
      //build_givens();
      initialize_cumulative_var_probabilities();
      build_vars_of_interest();
      // iterate over csv rows
      for (int i = 1; i < csv_array.length; i++) {
         out.println("\n\nLoop "+i);
         trace_total = i;
         row = csv_array[i];
         int given_count = 0;
         BayesianBin b = null;
         boolean bin_updated = false;

         //update events in vars of interest
         Iterator<String> iter = vars_of_interest.iterator();
         while(iter.hasNext()){
            String voi_name = iter.next();
            RawType type_enum = types.get(voi_name);
            int event_index = get_var_index(voi_name);
            //out.println("RawType: "+type_enum);
            Iterator iter2 = vars_of_interest.iterator();
            BayesianEvent be_test = null;
            String event_val = (String) row[event_index];
            // update frequency count for all vars
            update_cumulative_probabilites(voi_name, event_val, i);
            out.print("Updated cumulative probabilities:");
            print_cumulative_probabilities();
            if(debug){
               out.format("Get prior for %s:%s%n", voi_name, (String)event_val);
               out.println("event_val == empty string:"+(event_val.equals("")));
               out.println("event_val == space:"+(event_val.equals(" ")));
               out.println(event_val+"; isempty:"+event_val.isEmpty()+"; length:"+event_val.length());
               //out.println(StringEscapeUtils.escapeJava(event_val.isEmpty()));
               out.println(priors.get(voi_name));
               out.println(priors.get(voi_name).get(""));
               out.println(priors.get(voi_name).get(" "));
               //Driver.print_priors();*/
            }
            double prior = priors.get(voi_name).get(event_val);
            switch(type_enum){
               case INT:
                  be_test = new BayesianEvent<Integer>(voi_name, Integer.parseInt(event_val), prior, vars_of_interest);
                  break;
               case DOUBLE:
                  be_test = new BayesianEvent<Double>(voi_name, Double.parseDouble(event_val), prior, vars_of_interest);
                  break;
               case STRING:
                  be_test = new BayesianEvent<String>(voi_name, event_val, prior, vars_of_interest);
                  break;
            }
            ArrayList voi_vals = get_voi_vals((String[]) csv_array[i]);
            out.println("vars_of_interest: "+vars_of_interest);
            out.println("voi_vals: "+voi_vals);
            be_test.update_conditionals(voi_vals);
            //check that bayesian_events does not already contain this event
            boolean found = false;
            Iterator<BayesianEvent> iter_be = bayesian_events.iterator();
            while(iter_be.hasNext()){
               BayesianEvent be = iter_be.next();
               if(be.equals(be_test)){
                  //update count of this event and conditioned events
                  be.update_conditionals(voi_vals);
                  out.println("Updating bayesian event: "+be.toString());
                  found = true;
                  break;
               }
            }
            if(!found){
               out.println("Adding new bayesian event to list: "+be_test.toString());
               this.bayesian_events.add(be_test);
            }
            out.println("\nBAYESIAN EVENTS at loop "+i+" after updating "+voi_name);
            for(BayesianEvent be : bayesian_events){
               out.println(be.toString());
            }
         } // end vars_of_interest iterator
         /*if(i == 2){
            System.exit(0);
         }*/
      } // end csv loop
      out.println("\nFINISHED TRACE");
      //out.println("cumulative_probabilities: ");
      //print_cumulative_probabilities();
      for (int i = 0; i< givens.size(); i++){
         givens.get(i).set_total(trace_total);
         givens.get(i).set_priors(priors);
         givens.get(i).set_cumulative_probabilities(cumulative_probabilities);
         out.println(givens.get(i)+" \n\n");
      }
      out.println("\nBAYESIAN EVENTS");
      sort_bayesian_events();
      for(BayesianEvent be : bayesian_events){
         out.println(be.toString());
      }
      out.println("\n\nGENERATE BAYESIAN PROBABILITIES:");
      generate_bayesian_probabilities();
   } // end loop_through_trace()

   public ArrayList<BayesianEvent> sort_bayesian_events(){
      bayesian_events.sort(new Comparator<BayesianEvent>(){
         public int compare(BayesianEvent a, BayesianEvent b){
            return a.var_name.compareTo(b.var_name) ;
         }
         });
      return bayesian_events;
   }
   
   public ArrayList get_voi_vals(String[] row){
      ArrayList<String> al = new ArrayList<String>();
      for (String voi : vars_of_interest){
         int index = get_var_index(voi);
         al.add(row[index]);
      }
      return al;
   }
   
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
   
   public String generate_bayesian_probabilities(){
      //HashMap<String, HashMap<String >>
      String str = "";
      Iterator<BayesianEvent> iter = bayesian_events.iterator();
      while(iter.hasNext()){
         BayesianEvent be = iter.next();
         switch(be.prior_attribution){
            case PROB_A:
               //Double[] A_arr = cumulative_probabilites.
               /*HashMap<String, HashMap<String, Double[]>> cumulative_probabilities
               Set<String< keys1 = cumulative_probabilities.keySet();
               for(String key1 : keys1){
                  HashMap<String, Double[]> val_map = cumulative_probabilities.get(key1);
                  Set<String> keys2 = val_map.keySet();
                  for(String key2 : keys2){
                     
                  }
               }*/
               //double pA = / (double) trace_total;
               //double pB = / (double) trace_total;
               String temp = be.generate_bayesian_probability(cumulative_probabilities);
               out.println(temp);
               str += temp;
               break;
         } // end switch
         
      } // end while
      return str;
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