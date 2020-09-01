package inference_engine;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.TreeSet;
import java.util.Set;
import java.util.Iterator;
import java.util.logging.Logger;
import static java.lang.System.out;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.Comparator;
import static java.lang.Math.abs;
import java.util.function.Predicate;
import java.util.List;
import com.google.common.collect.Sets;

public class TypedBayesianEngine extends BasicEngine {
   
   HashMap<String, HashMap<String, Double>> priors = null;
   ArrayList<BayesianGiven> givens = new ArrayList<BayesianGiven>();
   ArrayList<BayesianEvent> bayesian_events = new ArrayList<BayesianEvent>();
   HashMap<String, HashMap<String, Double[]>> cumulative_probabilities = null;
   int trace_total;
   public static boolean debug = false;
   public static boolean filter_by_average = false;
   HashMap<String, DeltaTracker> delta_trackers = null;
   public Set<CompoundEvent> compound_events = null;
   
   public static final Logger debugBayesianEngine = Logger.getLogger("BayesianEngine");
   public TypedBayesianEngine(Object[][] csv_array){
      super(csv_array, Global.givens, Global.events);
   }
   
       
   public void initialize_cumulative_var_probabilities(){
      cumulative_probabilities = new HashMap<String, HashMap<String, Double[]>>();
      Iterator<String> iter = Global.vars_of_interest.iterator();
      while(iter.hasNext()){
         String voi_name = iter.next();
         cumulative_probabilities.put(voi_name, new HashMap<String, Double[]>());
      }
   }
   
   
   public void initialize_delta_trackers(){
      //out.println("begin initialize_delta_trackers()");
      delta_trackers = new HashMap<String, DeltaTracker>();
      Iterator<String> iter = Global.vars_of_interest.iterator();
      while(iter.hasNext()){
         String voi_name = iter.next();
         RawType rawtype = Global.types.get(voi_name);
         switch(rawtype){
            case INTDELTA:
            case DOUBLEDELTA: {
               //out.println("initialize_delta_trackers(): var_name: "+voi_name);
               double delta = Global.deltas.get(voi_name);
               //out.println("initialize_delta_trackers(): delta: "+delta);
               //out.println("initialize_delta_trackers(): # keys: "+keys.size());
               DeltaTracker dt = new DeltaTracker(voi_name, delta, 0.0);
               delta_trackers.put(voi_name, dt);
               //initialize next_values with the next delta values
               int index = get_var_index(voi_name);
               double[] next_values = new double[(int) Math.round(delta)];
               //out.println("delta="+delta);
               for(int i = 1; i <= delta; i++){
                  next_values[i-1] = Double.parseDouble((String)csv_array[i][index]);
                  //if(debug) out.format("next_values[%s] = %.2f;%n",i-1,Double.parseDouble((String)csv_array[i][index]));
               }
               //initialize last_values with delta # iterations of the first value
               double first_value = Double.parseDouble((String)csv_array[1][index]);
               double[] last_values = new double[(int) Math.round(delta)];
               for(int i = 1; i <= delta; i++){
                  last_values[i-1] = first_value;
               }
               dt.set_next_values(next_values);
               dt.set_last_values(last_values);
               break;}
            /*case STRINGDELTA: {
               double delta = Global.deltas.get(voi_name);
               StringDeltaTracker dt = new StringDeltaTracker(voi_name, delta, 0.0);
               delta_trackers.put(voi_name, dt);
               //initialize next_values with the next delta values
               
               break;}*/
         }
      }
   }
   
   
   /**
    * Setup threshold means
    * Setup uniform prior distribution if none given
    */
   public void preprocess_trace(){
      setup_threshold_means();
      if(Global.priors.keySet().isEmpty()){
         setup_prior_uniform_dist();
      }
      this.compound_events = generateCompoundEvents();
   }
   
   public void prune_vars_of_interest(){
      //prune vars of interest to only those present in trace
      Iterator<String> iter = Global.vars_of_interest.iterator();
      Object[] first_row = csv_array[0];
      List<Object> headers = Arrays.asList(first_row);
      while(iter.hasNext()){
         if(!headers.contains(iter.next())){
            iter.remove();
         }
      }
      //TODO: do same for Global.givens and Global.events
   }
   
   /**
    * Initialize cumulative_probabilities values to optimize thresholds
    * and pick values that prevent overlap
    **/
   public void setup_threshold_means(){
      for(String voi_name : Global.vars_of_interest){
         Double threshold = Global.thresholds.get(voi_name);
         RawType type = Global.types.get(voi_name);
         TreeSet<Double> d_set = new TreeSet<Double>();
         TreeSet<Integer> i_set = new TreeSet<Integer>();
         TreeSet<String> s_set = new TreeSet<String>();
         for(int i = 1; i< csv_array.length; i++){
            String[] row = (String[]) csv_array[i];
            try{
               switch(type){
                  case DOUBLE:
                     try{
                        double val = Double.valueOf(row[get_var_index(voi_name)]);
                        d_set.add(val);
                     } catch(NumberFormatException ex){
                        d_set.add(Double.MIN_VALUE);
                     }
                     break;
                  case INT:
                     try{
                        int val =Integer.valueOf(row[get_var_index(voi_name)]);
                        i_set.add(val);
                     } catch(NumberFormatException ex){
                        i_set.add(Integer.MIN_VALUE);
                     }
                     break;
                  case STRING:
                     s_set.add(row[get_var_index(voi_name)]);
                     break;
               }
            }catch(ArrayIndexOutOfBoundsException ex){
               /*ex.printStackTrace();
               out.format("csv_array size: %s,%s%n",csv_array.length,csv_array[0].length);
               out.format("get_var_index(%s)=%s%n",voi_name,get_var_index(voi_name));
               out.format("Row #%s: %s%n",i,Arrays.toString(row));
               out.format("Row #%s size: %s%n",i,row.length);*/
            }
         }
         if(debug){
            switch(type){
               case DOUBLE:
                  out.println(voi_name+" d_set:"+d_set.toString());
                  break;
               case INT:
                  out.println(voi_name+" i_set:"+i_set.toString());
                  break;
               case STRING:
                  out.println(voi_name+" s_set:"+s_set.toString());
                  break;
            }
         }
         //initalize cumulative_probabilities keys
         switch(type){
            case DOUBLE:{
               Double last = null;
               TreeSet<Double> slice = new TreeSet<Double>();
               Iterator<Double> d_iter = d_set.iterator();
               while(d_iter.hasNext()){
                  Double curr = d_iter.next();
                  //System.out.format("curr=%.2f last= %.2f%n",curr,last);
                  Double[] d_arr = new Double[]{0.0,0.0};
                  //always add first value(?)
                  if(curr.equals(d_set.first())){
                     //System.out.format("%.2f.equals(d_set.first())=%s%n", curr, (curr.equals(d_set.first())));
                     cumulative_probabilities.get(voi_name).put(curr.toString(), d_arr);
                     last = curr;
                     continue;
                  } else if (threshold == null){
                     //System.out.println("threshold null; adding rounded unique value "+ Double.toString(Math.round(curr)));
                     cumulative_probabilities.get(voi_name).put(Double.toString(Math.round(curr)), d_arr);
                  } else if(threshold != null) {
                     if(last == d_set.first() && abs(last-curr) >= threshold){
                        //out.format("last == d_set.first() && abs(last-curr) >= threshold%n");
                        cumulative_probabilities.get(voi_name).put(Double.toString(curr), d_arr);
                        last = curr;
                     } else if(abs(last-curr) == threshold*2){
                        //System.out.format("threshold=%f && abs(last=%f - curr=%f) >= threshold*2 = %s%n", threshold, last, curr, (abs(last-curr) >= threshold*2));
                        cumulative_probabilities.get(voi_name).put(curr.toString(), d_arr);
                        last = curr;
                     } else if(last-curr < threshold) {
                        //out.format("slice: %s%n", slice);
                        if(slice.isEmpty()){
                           slice.add(curr);
                        } else if(abs(curr-slice.first()) >= threshold*2){
                           //out.format("abs(curr-slice.first()) >= threshold*2%n");
                           Double mid = (slice.last() + slice.first())/2.0;
                           cumulative_probabilities.get(voi_name).put(Double.toString(mid), d_arr);
                           last = mid+threshold;
                           if(curr > last){
                              cumulative_probabilities.get(voi_name).put(Double.toString(curr), d_arr);
                              last = curr;
                           }
                           slice = new TreeSet<Double>();
                        } else {
                           slice.add(curr);
                        }
                     } else if(curr.equals(d_set.last()) && abs(last-curr)>threshold){
                        //System.out.format("threshold=%f && %f .equals(d_set.last()) && abs(last=%f - curr=%f) > threshold%n", threshold, curr, last, curr, (abs(last-curr) >= threshold*2));
                        cumulative_probabilities.get(voi_name).put(curr.toString(), d_arr);
                     }
                     //print_cumulative_probabilities(); out.println();
                  }
               }
               break;}
            case INT:{
              Iterator<Integer> i_iter = i_set.iterator();
               Integer last = null;
               while(i_iter.hasNext()){
                  Integer curr = i_iter.next();
                  Double[] d_arr = new Double[]{0.0,0.0};                  
                  if(curr.equals(i_set.first())){
                     cumulative_probabilities.get(voi_name).put(curr.toString(), d_arr);
                     last = curr;
                     continue;
                  } else if (threshold == null){
                     cumulative_probabilities.get(voi_name).put(Integer.toString(curr), d_arr);
                  } else if(abs(last-curr) >= threshold*2){
                     cumulative_probabilities.get(voi_name).put(curr.toString(), d_arr);
                     last = curr;
                  }else if(curr.equals(i_set.last()) && abs(last-curr)>threshold){
                     cumulative_probabilities.get(voi_name).put(curr.toString(), d_arr);
                  }
               }
               break;}
            case STRING:{
               Iterator<String> iter = s_set.iterator();
               while(iter.hasNext()){
                  Double[] d_arr = new Double[]{0.0,0.0};
                  cumulative_probabilities.get(voi_name).put(iter.next(), d_arr);
               }
               break;}
            case INTEXP:
            case DOUBLEEXP:
            case STRINGEXP:
            case INTDELTA:
            case DOUBLEDELTA:
            case STRINGDELTA: {
               HashMap<String, Predicate<Object>> bound_id_map = Global.bound_ids.get(voi_name);
               Set<String> keys = bound_id_map.keySet();
               for(String key: keys){
                  Double[] d_arr = new Double[]{0.0,0.0};
                  cumulative_probabilities.get(voi_name).put(key, d_arr);
               }
               // create new BoundedEvents for each bound, add to bayesian_events, zero num_samples
               break;}
         }
      }//end voi for
      //out.println("Finished setup_threshold_means(); set up cumulative probabilities:");
      //print_cumulative_probabilities();
   }
   
   
   public void setup_prior_uniform_dist(){
      //out.println("\nEntered setup_prior_uniform_dist()");
      //print_cumulative_probabilities();
      Global.priors = new HashMap<String, HashMap<String, Double>>();
      Set<String> keys1 = cumulative_probabilities.keySet();
      for(String key1 : keys1){
         HashMap<String, Double[]> cumulative_probability = cumulative_probabilities.get(key1);
         Set<String> keys2 = cumulative_probability.keySet();
         for(String key2 : keys2){
            try{
               Global.priors.get(key1).put(key2, 1.0/keys2.size());
            }catch(NullPointerException ex){
               Global.priors.put(key1, new HashMap<String, Double>());
               Global.priors.get(key1).put(key2, 1.0/keys2.size());
            }
         }
      }
      //out.print("Finished setup_prior_uniform_dist(): ");
      //Driver.print_priors(Global.priors);
   }
   

   public void loop_through_trace(){
      if(debug) debugBayesianEngine.info("Begin looping through trace");
      //debugBayesianEngine.info("Length of trace: "+csv_array.length);
      //debugBayesianEngine.info("Trace variables: "+csv_array[0]));
      Object[] row;
      prune_vars_of_interest();
      initialize_cumulative_var_probabilities();
      preprocess_trace();
      initialize_delta_trackers();
      // iterate over csv rows
      for (int i = 1; i < csv_array.length; i++) {
         if(debug) out.println("\n\nBEGIN LOOP "+i);
         trace_total = i;
         row = csv_array[i];
         boolean bin_updated = false;
         //update delta_trackers
         update_delta_trackers((String[]) csv_array[i], i);
         //update events in vars of interest
         Iterator<String> iter = Global.vars_of_interest.iterator();
         ArrayList<Object> parsed_row = new ArrayList();
         while(iter.hasNext()){
            String voi_name = iter.next();
            RawType type_enum = Global.types.get(voi_name);
            BayesianEvent be_test = null;
            String event_val = (String) row[get_var_index(voi_name)];
            //if(debug) { debugBayesianEngine.info(String.format("Get prior for %s:%s%n", voi_name, (String)event_val)); }
            double prior = get_prior(voi_name, event_val);
            switch(type_enum){
               case INT:
                  try{
                     parsed_row.add(Integer.parseInt(event_val));
                     be_test = new BayesianEvent<Integer>(voi_name, Integer.parseInt(event_val), prior);
                  } catch(NumberFormatException ex){
                     be_test = new BayesianEvent<Integer>(voi_name, Integer.MIN_VALUE, prior);
                  }
                  break;
               case DOUBLE:
                  try{
                     parsed_row.add(Double.parseDouble(event_val));
                     be_test = new BayesianEvent<Double>(voi_name, Double.parseDouble(event_val), prior);
                  } catch(NumberFormatException ex){
                     be_test = new BayesianEvent<Double>(voi_name, Double.MIN_VALUE, prior);
                  }
                  break;
               case STRING:
                  parsed_row.add(event_val);
                  be_test = new BayesianEvent<String>(voi_name, event_val, prior);
                  break;
               case INTEXP:{
                  Predicate<Object> tester = get_tester(voi_name, Double.parseDouble(event_val));
                  String tester_id = get_tester_id(voi_name, Double.parseDouble(event_val));
                  parsed_row.add(tester_id);
                  be_test = new BoundedEvent<Integer>(voi_name, tester_id, prior, tester);
                  break;}
               case DOUBLEEXP:{
                  //debugBayesianEngine.info(String.format("get_tester(%s, %s) fro row %s%n",voi_name,event_val,i));
                  Predicate<Object> tester = null;
                  try{
                     tester = get_tester(voi_name, Double.parseDouble(event_val));
                  } catch(NumberFormatException ex){
                     out.format("get_tester(%s, %s) fro row %s%n",voi_name,event_val,i);
                     ex.printStackTrace();
                     System.exit(0);
                  }
                  String tester_id = get_tester_id(voi_name, Double.parseDouble(event_val));
                  parsed_row.add(tester_id);
                  be_test = new BoundedEvent<Double>(voi_name, tester_id, prior, tester);
                  break;}
               case STRINGEXP: {
                  Predicate<Object> tester = get_tester(voi_name, event_val);
                  String tester_id = get_tester_id(voi_name, event_val);
                  parsed_row.add(tester_id);
                  be_test = new BoundedEvent<String>(voi_name, tester_id, prior, tester);
                  break;}
               case INTDELTA:
               case DOUBLEDELTA:{
                  DeltaTracker dt = delta_trackers.get(voi_name);
                  try{
                     event_val = new Double(dt.compute_next_delta()).toString();
                  } catch(DeltaException de){
                     //out.println(de.getMessage());
                     event_val = "0.0";
                  }
                  //make new DeltaEvent
                  //out.format("voi_name=%s event_val=%s%n",voi_name,event_val);
                  Predicate<Object> tester = get_tester(voi_name, Double.parseDouble(event_val));
                  //out.format("get_tester_id(%s, %s);%n",voi_name,Double.parseDouble(event_val));
                  String tester_id = get_tester_id(voi_name, Double.parseDouble(event_val));
                  parsed_row.add(tester_id);
                  double delta = Global.deltas.get(voi_name);
                  be_test = new DeltaEvent<Double>(voi_name, tester_id, prior, tester, delta);
                  break;}
               case STRINGDELTA:{
                  DeltaTracker dt = delta_trackers.get(voi_name);
                  try{
                     event_val = new Double(dt.compute_next_delta()).toString();
                  } catch(DeltaException de){
                     //out.println(de.getMessage());
                     event_val = "0.0";
                  }
                  //make new DeltaEvent
                  Predicate<Object> tester = get_tester(voi_name, Double.parseDouble(event_val));
                  String tester_id = get_tester_id(voi_name, Double.parseDouble(event_val));
                  parsed_row.add(tester_id);
                  double delta = Global.deltas.get(voi_name);
                  be_test = new DeltaEvent<Double>(voi_name, tester_id, prior, tester, delta);
                  break;}
            }
            // update frequency count for voi
            if(debug) debugBayesianEngine.info(String.format("update_cumulative_probabilites(%s, %s, %s)%n", voi_name, event_val, i));
            update_cumulative_probabilites(voi_name, event_val, i);
            if(debug) {
               debugBayesianEngine.info("Updated cumulative probabilities:");
               print_cumulative_probabilities();
            }
            ArrayList voi_vals = get_voi_vals((String[]) csv_array[i]);
            if(debug) {
               debugBayesianEngine.info("vars_of_interest: "+Global.vars_of_interest
                                        + "\nvoi_vals: "+voi_vals
                                        + "\nbe_test == null?"+(be_test == null)
                                        + "\n\nbe_test.update_conditionals("+voi_vals+", true)");
            }
            be_test.update_conditionals(voi_vals, false);
            //check that bayesian_events does not already contain this event
            boolean found = false;
            Iterator<BayesianEvent> iter_be = bayesian_events.iterator();
            BayesianEvent be = null;
            while(iter_be.hasNext()){
               be = iter_be.next();
               if(debug) {
                  debugBayesianEngine.info(String.format("be null? %s%nbe_test null? %s%nbe EQUALS be_test: %s%n",
                                                         (be == null),(be_test == null),be.equals(be_test)));
                  debugBayesianEngine.info("\tbe:"+be.toString()+"\n\tbe_test:"+be_test.toString());
               }
               if(be.equals(be_test)){
                  //update count of this event and conditioned events
                  if(debug) debugBayesianEngine.info("\nbe.update_conditionals("+voi_vals+", true)");
                  be.update_conditionals(voi_vals, false);
                  if(debug) debugBayesianEngine.info("Updated bayesian event: "+be.toString());
                  found = true;
                  break;
               }
            }
            if(!found){
               if(debug) debugBayesianEngine.info("Adding new bayesian event to list: "+be_test.toString());
               this.bayesian_events.add(be_test);
            } else {
               be.update();
            }
         }  // end vars_of_interest iterator
         if(debug){
            String s = "";
            for(BayesianEvent bev : bayesian_events){
               s +="\n\t"+bev.toString();
            }
            debugBayesianEngine.info(String.format("%nBAYESIAN EVENTS at loop %s%s%n",i,s));
         }
         //UPDATE COMPOUND EVENTS
         update_compound_events(parsed_row);
         //UPDATE CONSTRAINT EVENTS
         for(ConstraintEvent ce : Global.constraint_events){
            TreeSet<String> ce_vois = ce.vois;
            ArrayList<String> al = get_voi_vals((String[])row, ce_vois);
            //if(debug) out.println("\nUpdating ce conditionals with values "+ce_vois+" "+al);
            ce.update_conditionals(al);
         }
         if(debug){
            out.println("\nCONSTRAINT EVENTS at loop "+i);
            for(ConstraintEvent ce : Global.constraint_events){
               out.println("\t"+ce.toString());
            }
         }
         //if(i >= 10){System.exit(0);}
      } // end csv loop
      sort_bayesian_events();
      debug = false;
      if(debug){
         out.println("\nFINISHED TRACE");
         for (int i = 0; i< givens.size(); i++){
            givens.get(i).set_total(trace_total);
            givens.get(i).set_priors(Global.priors);
            givens.get(i).set_cumulative_probabilities(cumulative_probabilities);
         }
         out.format("CUMULATIVE PROBABILITIES: "); print_cumulative_probabilities();
         out.println("\nBAYESIAN EVENTS");
         for(BayesianEvent be : bayesian_events){
            out.println(be.toString());
         }
         Driver.print_priors(Global.priors);
      }
      //System.out.println("\nfinished CompoundEvents:");
      for (CompoundEvent ce : this.compound_events){
         ce.event_space = trace_total;
         //System.out.println("\n"+ce.toString_with_counts());
         //ce.calculate_bayesian_probabilities_with_computation();
         ce.calculate_bayesian_probabilities_with_computation_and_samplesize();
      }
      
      //debugBayesianEngine.info("\n\nGENERATE BAYESIAN PROBABILITIES:");
      //out.println(generate_bayesian_probabilities());
   } // end loop_through_trace()
   
   
   // Update P(B, C | A) == P(B, C, A) / P(A)
   // Update P(B, C) == P(B, C) / event_space
   public void update_compound_events(ArrayList<Object> row){
      for(CompoundEvent ce : this.compound_events){
         boolean all_found = true;
         boolean givens_found = true;
         boolean givens_not_A_found = true;
         for(String var_name: ce.given_var_names){
            int i = get_voi_index(var_name);
            Object event_val = row.get(i);
            //System.out.println("ce:"+ce.toString_with_counts()+" var_name:"+var_name+" i:"+i);
            /*System.out.println("\nrow: "+row);
            System.out.println("ce: "+ce.toString());
            System.out.println("ce containsVal "+event_val+"? "+ce.containsVal(event_val));
            System.out.println(ce.A_val+" equals "+event_val+"? "+ce.A_val.equals(event_val));
            System.out.println("ce.vals "+ce.vals+" contains "+var_name+"? "+ce.vals.contains(event_val));*/
            if(ce.vals.contains(event_val)){
               // 
            } else {
               all_found = false;
               givens_found = false;
               //break;
            }
         }
         // Check for A variable
         int i = get_voi_index(ce.A_var_name);
         Object event_val = row.get(i);
         if(ce.A_val.equals(event_val)){
            // Update P(A)
            ce.update_A_count();
            givens_not_A_found = false;
         } else {
            ce.update_not_A_count();
            all_found = false;
            if(givens_found){
               //givens_not_A_found = true;
               ce.update_givens_count_not_A();
            }
         }
         // Update P(B, C, A)
         if(all_found){
            // update count for ce
            ce.update_all_count();
         }
         // Update P(B, C)
         if (givens_found){
            ce.update_givens_count();
         }
      }
   }
   
   
   public Predicate<Object> get_tester(String voi_name, Double dbl){
      //out.format("get_tester(%s,%.3f)%n",voi_name, dbl);
      ArrayList<Predicate<Object>> al = Global.bounds.get(voi_name);
      HashMap<String,Predicate<Object>> ids = Global.bound_ids.get(voi_name);
      //out.format("ids: %s%n",Arrays.toString(ids.keySet().toArray()));
      //out.format("size of array:%s%n",al.size());
      for (String key : ids.keySet()){
         Predicate<Object> p = ids.get(key);
         //out.format("key:%s, tester null? %s%n", key, (p==null));
         if(p.test((Object) dbl)){
            return p;
         }
      }
      return null;
   }
   
   
   public String get_tester_id(String voi_name, Double dbl){
      HashMap<String, Predicate<Object>> id_map = Global.bound_ids.get(voi_name);
      Set<String> keys = id_map.keySet();
      for (String key : keys){
         Predicate<Object> p = id_map.get(key);
         if(p.test((Object) dbl)){
            return key;
         }
      }
      return null;
   }
   
   
   public Predicate<Object> get_tester(String voi_name, String dbl){
      ArrayList<Predicate<Object>> al = Global.bounds.get(voi_name);
      for (Predicate<Object> p : al){
         if(p.test((Object) dbl)){
            return p;
         }
      }
      return null;
   }
   
   
   public String get_tester_id(String voi_name, String dbl){
      HashMap<String, Predicate<Object>> id_map = Global.bound_ids.get(voi_name);
      Set<String> keys = id_map.keySet();
      for (String key : keys){
         Predicate<Object> p = id_map.get(key);
         if(p.test((Object) dbl)){
            return key;
         }
      }
      return null;
   }
   
   
   public double get_prior(String voi_name, String event_val){
      double d = 0;
      Double voi_threshold = 0.0;
      try{
         voi_threshold = Global.thresholds.get(voi_name);
         d = Global.priors.get(voi_name).get(event_val);
      } catch(NullPointerException ex) {
         //iterate over priors looking for closest one
         HashMap<String, Double> voi_priors = Global.priors.get(voi_name);
         //System.out.println(voi_name+" priors:");
         //System.out.println(voi_priors);
         RawType type_enum = Global.types.get(voi_name);
         try{
            Set<String> keys = voi_priors.keySet();
            for(String key : keys){
               d = voi_priors.get(key);
               switch(type_enum){
                  case DOUBLE:
                     double dbl = Double.parseDouble(event_val);
                     double key_val = Double.parseDouble(key);
                     //if(debug/*true*/) out.format("get_prior: threshold for %s = %f%n", voi_name, voi_threshold);
                     if(voi_threshold == null && Math.round(dbl) == Math.round(key_val)){
                        return d;
                     }else if(voi_threshold != null && Fuzzy.eq(dbl, key_val, voi_threshold.doubleValue())){
                        return d;
                     }
                     break;
                  case INT:
                     int i = Integer.parseInt(event_val);
                     int key_val_int = Integer.parseInt(key);
                     if(Fuzzy.eq(i, key_val_int, voi_threshold.doubleValue())){
                        return d;
                     }
                     break;
                  case STRING: break;
               }
            }
         } catch(NullPointerException ex2){
            //TODO: change to search through prior keys
            switch(type_enum){
               case INTEXP:
               case DOUBLEEXP:
               case STRINGEXP: {
                  ArrayList al = Global.bounds.get(voi_name);
                  d = 1.0 / al.size();
                  break;}
               case INTDELTA:
               case DOUBLEDELTA: {
                  ArrayList al = Global.bounds.get(voi_name);
                  d = 1.0 / al.size();
                  break;}
            }
         } catch(NumberFormatException ex3){
            switch(type_enum){
               case DOUBLE:
                  break; 
               case INT:
                  break;
            }
         }

      }
      return d;
   }
   
   
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
      Iterator<String> iter = Global.vars_of_interest.iterator();
      while(iter.hasNext()){
         String voi = iter.next();
         //change voi_vals to delta value
         RawType rawtype = Global.types.get(voi);
         double threshold = Double.MAX_VALUE;
         try{
            threshold = Global.thresholds.get(voi);
         } catch(Exception ex){threshold = Double.MAX_VALUE;}
         if(debug) out.println("get_var_index("+voi+");");
         int index = get_var_index(voi);
         switch(rawtype){
            case INT:
            case DOUBLE: {
               if(threshold == Double.MAX_VALUE && rawtype != RawType.DOUBLE){
                  al.add(row[index]);
               } else if (threshold == Double.MAX_VALUE && rawtype == RawType.DOUBLE) {
                  // get closest cumulative_probabilities key
                  // prevents progpogation of different values for same entry/data sample
                  threshold = 1.0; //TODO: why do thresholds 0 and 0.5 break it?
                  String val = String.valueOf(Math.round(Double.parseDouble(row[index])));
                  String closest_key = get_closest_cumulative_probabilites_key(voi, val, threshold, rawtype);
                  if(debug) out.println("get_voi_vals(): closest_key="+closest_key);
                  al.add(closest_key);
               } else {
                  String closest_key = get_closest_cumulative_probabilites_key(voi, row[index], threshold, rawtype);
                  al.add(closest_key);
               }
               break;}
            case STRING:
            case INTEXP:
            case DOUBLEEXP:
            case STRINGEXP:{
               al.add(row[index]);
               break;}
            case INTDELTA:
            case DOUBLEDELTA:{
               Double d = Double.parseDouble(row[index]);
               Set<String> keys = Global.bound_ids.get(voi).keySet();
               DeltaTracker dt = delta_trackers.get(voi);
               double delta_value = 0.0;
               try{
                  delta_value = dt.compute_last_delta();
                  //out.println("get_voi_vals(): "+voi+" delta_value: "+delta_value);
               } catch(DeltaException de){
                  //out.println("get_voi_vals(): "+de.getMessage());
               }
               al.add(Double.toString(delta_value));
               break;}
         }
      }
      return al;
   }
   
   // constraint-specific method
   public ArrayList<String> get_voi_vals(String[] row, TreeSet<String> vois){
      ArrayList<String> al = new ArrayList<String>();
      out.println(vois);
      for (String voi : vois){
         //change voi_vals to delta value
         RawType rawtype = Global.types.get(voi);
         out.println("get_voi_vals():"+voi+" "+rawtype);
         int index = get_var_index(voi);
         switch(rawtype){
            case INT:
            case DOUBLE:
            case STRING:
            case INTEXP:
            case DOUBLEEXP:
            case STRINGEXP:{
               al.add(row[index]);
               break;}
            case INTDELTA:
            case DOUBLEDELTA:{
               Double d = Double.parseDouble(row[index]);
               Set<String> keys = Global.bound_ids.get(voi).keySet();
               DeltaTracker dt = delta_trackers.get(voi);
               double delta_value = 0.0;
               try{
                  delta_value = dt.compute_last_delta();
                  out.println("get_voi_vals(): "+voi+" delta_value: "+delta_value);
               } catch(DeltaException de){
                  out.println("get_voi_vals(): "+de.getMessage());
               }
               al.add(Double.toString(delta_value));
               break;}
         }
      }
      return al;
   }

   
   public String get_closest_cumulative_probabilites_key(String voi, String value, double threshold, RawType rawtype){
      //debug = true;
      if(debug) out.format("get_closest_cumulative_probabilites_key(%s,%s,%s,%s)%n",voi, value, threshold, rawtype);
      HashMap<String, Double[]> cumulative_probability = null;
      cumulative_probability = cumulative_probabilities.get(voi);
      Set<String> keys = cumulative_probability.keySet();
      for(String key : keys){
         switch(rawtype){
            case INT:
            case DOUBLE: {
               double keyval = Double.parseDouble(key);
               double newval = Double.parseDouble(value);
               if(Fuzzy.eq(keyval, newval, threshold)){
                  if(debug) out.format("get_closest_cumulative_probabilites_key(): return %s%n", key);
                  //debug = false;
                  return key;
               }
            break;}
            case STRING:
            case DOUBLEDELTA:
               break;
         }
      }
      if(debug) out.format("get_closest_cumulative_probabilites_key(): return null%n");
      //debug = false;
      print_cumulative_probabilities();
      return null;
   }
   
   
   public void update_delta_trackers(String[] row, int row_index){
      for (String voi : Global.vars_of_interest){
         //change voi_vals to delta value
         RawType rawtype = Global.types.get(voi);
         int index = get_var_index(voi);
         switch(rawtype){
            case INTDELTA:
            case DOUBLEDELTA:{
               Double d = Double.parseDouble(row[index]);
               DeltaTracker dt = delta_trackers.get(voi);
               //update next_values
               try{
                  dt.update_next_values(Double.parseDouble((String)csv_array[row_index-1+((int)Math.round(dt.delta))][index]));
               }catch(ArrayIndexOutOfBoundsException e){
                  dt.update_next_values(d);
               }
               //update last_values
               dt.update_last_values(d);
               break;}
         }
      }
      ArrayList<String> al = new ArrayList<String>(Arrays.asList(row));
      //print_delta_trackers();
   }
      
   
   public void update_cumulative_probabilites(String voi_name, String val, int i){
      HashMap<String, Double[]> cumulative_probability = null;
      cumulative_probability = cumulative_probabilities.get(voi_name);
      try{
         if(debug) out.println("update_cumulative_probabilites: Retrieved " +cumulative_probability_toString(cumulative_probability)+" from key "+voi_name);
         Double[] d_array = cumulative_probability.get(val);
         d_array[0] = ++d_array[0]; d_array[1] = (double)i;
         cumulative_probability.put(val, d_array);
      } catch(NullPointerException exc){
         if(debug) out.format("update_cumulative_probabilites: caught null pointer on cumulative_probability.get(%s)%n", val);
         boolean found = false;
         Set<String> keys = cumulative_probability.keySet();
         RawType raw_type = Global.types.get(voi_name);
         double threshold = 0;
         Double[] d_array = new Double[2];
         try{
            threshold =  Global.thresholds.get(voi_name);
         } catch(NullPointerException ex){
            threshold = Double.MAX_VALUE; /* round doubles*/
         }
         String closest_match_key = null;
         for(String key : keys){
            switch(raw_type){
               case DOUBLE:
                  double key_val_dbl = Double.valueOf(key);
                  double dbl = Double.valueOf(val);
                  if(Fuzzy.eq(key_val_dbl,dbl,threshold) && threshold != Double.MAX_VALUE){
                     found = true;
                     if(closest_match_key != null){
                        double closest_match_key_double = Double.parseDouble(closest_match_key);
                        if(debug) out.format("get_cumulative_probability: abs(%.2f-%.2f) < abs(%.2f-%.2f) = %s%n",dbl,closest_match_key_double,dbl,key_val_dbl,(abs(dbl-closest_match_key_double) < abs(dbl-key_val_dbl)));
                        if(abs(dbl-closest_match_key_double) > abs(dbl-key_val_dbl)){
                           closest_match_key = key;
                        }
                     } else {
                        closest_match_key = key;
                     }
                  } else if(Math.round(Double.valueOf(key))==Math.round(Double.valueOf(val))){
                     found = true;
                     d_array = cumulative_probability.get(key);
                     d_array[0] = ++d_array[0]; d_array[1] = (double)i;
                     cumulative_probability.put(key, d_array);
                     return;
                  }
                  break;
               case INT:
                  //out.format("update_cumulative_probabilites: case INT: threshold=%f%n", threshold);
                  int int_val = Integer.valueOf(val);
                  int key_val = Integer.valueOf(key);
                  if(Fuzzy.eq(key_val,int_val,threshold) && threshold != Double.MAX_VALUE){
                     found = true;
                     if(closest_match_key != null){
                        double closest_match_key_int = Integer.parseInt(closest_match_key);
                        if(debug) out.format("get_cumulative_probability: abs(%.2f-%.2f) < abs(%.2f-%.2f) = %s%n",int_val,closest_match_key_int,int_val,key_val,(abs(int_val-closest_match_key_int) < abs(int_val-key_val)));
                        if(abs(int_val-closest_match_key_int) > abs(int_val-key_val)){
                           closest_match_key = key;
                        }
                     } else {
                        closest_match_key = key;
                     }
                  } else if(Integer.valueOf(key)==Integer.valueOf(val)) {
                     found = true;
                     d_array = cumulative_probability.get(key);
                     d_array[0] = ++d_array[0]; d_array[1] = (double)i;
                     cumulative_probability.put(key, d_array);
                  }
                  break;
               case STRING: //no comparative eq for strings
                  break;
               case INTEXP:
               case DOUBLEEXP:
               case STRINGEXP:{
		  Predicate<Object> tester = null;
		  try{
            tester = Global.bound_ids.get(voi_name).get(key);
            //debugBayesianEngine.info("Getting tester for "+voi_name+" "+key);
            //debugBayesianEngine.info("val == null?"+(val == null)); 
		  } catch(Exception e) {
			System.exit(0);
		  }
                  if(tester.test((Object)val)){
                     found = true;
                     closest_match_key = key;
                  }
                  break;}
               case INTDELTA:
               case DOUBLEDELTA: {
                  Predicate<Object> tester = Global.bound_ids.get(voi_name).get(key);
                  if(tester.test((Object)val)){
                     found = true;
                     closest_match_key = key;
                  }
                  break;}
            }
         }
         if(!found){
            d_array = new Double[]{1.0, (double)i};
            cumulative_probability.put(val, new Double[]{1.0, (double)i});
            if(debug) out.format("update_cumulative_probabilites: cumulative_probability.put(%s, [%s,%s])%n", val, d_array[0],d_array[1]);
         } else {
            if(debug) out.format("update_cumulative_probabilites: closest_match_key:%s%n", closest_match_key);
            d_array = cumulative_probability.get(closest_match_key);
            d_array[0] = ++d_array[0]; d_array[1] = (double)i;
            cumulative_probability.put(closest_match_key, d_array);
            if(debug) out.format("update_cumulative_probabilites: cumulative_probability.put(%s, [%s,%s])%n", val, d_array[0],d_array[1]);
         }
      }
      //if(debug) out.format("update_cumulative_probabilites: cumulative_probabilities.put(%s, %s)%n", voi_name, cumulative_probability);
      cumulative_probabilities.put(voi_name, cumulative_probability);
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
      //out.format("CUMULATIVE PROBABILITIES: "); print_cumulative_probabilities();
      //out.format("end of TypedBayesianEngine.update_cumulative_probabilities()%n%n");
   } //end update_cumulative_probabilities()
   
   
   public String generate_bayesian_probabilities(){
      calculate_total_probabilities();
      String str = "";
      Iterator<BayesianEvent> iter = bayesian_events.iterator();
      while(iter.hasNext()){
         BayesianEvent be = iter.next();
         switch(be.prior_attribution){
            case PROB_A:
               String temp = be.generate_bayesian_probability(cumulative_probabilities, filter_by_average);
               str += temp + "\n\n";
               break;
         } // end switch
      } // end while
      return regroup_probabilities_by_given(str);
   }
   
   
   public String regroup_probabilities_by_given(String raw_string){
      //out.println("regroup_probabilities_by_given(): ENTER");
      String return_str = "";
      HashMap<String, String> given_map = new HashMap<String, String>();
      //separate by non-empty newline
      String[] line_array = raw_string.split("\n");
      for(String s : line_array){
         if(!s.equals("")){
            //get conditions -- string between | and )
            String[] temp = s.split("\\|");
            temp = temp[1].split("\\)");
            String condition = temp[0];
            //append to string stored in hashmap by key
            try{
               String value = given_map.get(condition);
               if(value.equals("")){
                  given_map.put(condition, s);
               } else {
                  value += "\n"+s;
                  given_map.put(condition, value);
               }
            } catch(Exception e){
               given_map.put(condition, s);
            }
         }
      }
      //iterate through keys, appending to return string
      Set<String> keys = given_map.keySet();
      TreeSet<String> treeset = new TreeSet(keys);
      for(String key : treeset){
         return_str += given_map.get(key)+"\n\n";
      }
      return return_str;
   }
   
   
   public void calculate_total_probabilities(){
      if(debug) out.println("inside calculate_total_probabilities()");
      for(String voi : Global.vars_of_interest){
         //collect all events with same var
         ArrayList<BayesianEvent> events = new ArrayList<BayesianEvent>();
         for(BayesianEvent be : bayesian_events){
            if(be.var_name.equals(voi)){
               events.add(be);
            }
         }
         //use cumulative_probabilities keys so as not to miss bins
         /*out.print("calculate_total_probabilities(): cumulative_probabilities="); print_cumulative_probabilities();*/
         Set<String> keys1 = cumulative_probabilities.keySet();
         for(String key1 : keys1){
            if(!key1.equals(voi)){
               if(debug){
                  out.println("\n\ncalculate_total_probabilities(): calculating total probability for "+key1);
                  out.println("calculate_total_probabilities(): events="+events.toString());
                  out.println(key1+" priors:");
                  out.println(Global.priors.get(key1));
               }
               Set<String> keys2 = cumulative_probabilities.get(key1).keySet();
               for(String key2 : keys2){
                  double total_probability = 0.0;
                  for(BayesianEvent be : events){
                     try{
                        HashMap<String, Double> pBA_val_map = (HashMap<String, Double>) be.pBAs.get(key1);
                        double prior = 0.0;
                        switch(Global.types.get(voi)){
                           case INT:
                           case DOUBLE:
                           case STRING:{
                              prior =  get_prior(be.var_name, be.val.toString());
                              if(debug) out.format("calculate_total_probabilities(): get_prior(%s,%s) = %.5f%n", be.var_name, be.val.toString(),prior);
                           break;}
                           case INTEXP:
                           case DOUBLEEXP:
                           case STRINGEXP:
                           case INTDELTA:
                           case DOUBLEDELTA:{
                              BoundedEvent boundev = (BoundedEvent) be;
                              prior =  get_prior(boundev.var_name, boundev.id);
                              //out.format("calculate_total_probabilities(): get_prior(%s,%s) = %.3f%n",boundev.var_name, boundev.id,prior);
                           break;}
                        }
                        //double prior =  get_prior(key1, key2); //get_prior(be.var_name, key2); //get_prior(key1, key2);
                        //double prior =  get_prior(be.var_name, be.id);
                        /*out.format("calculate_total_probabilities(): get_prior(key1=%s,key2=%s) = %.3f%n",key1,key2,prior);
                        out.format("calculate_total_probabilities(): alternative get_prior(be.var_name=%s,key2=%s) = %.3f%n",be.var_name,key2,get_prior(be.var_name, key2));
                        out.format("calculate_total_probabilities(): pulling pBA count from %s%n",be);
                        out.format("calculate_total_probabilities(): be.pBAs.get.(%s).get(%s)=",key1,key2,pBA_val_map.get(key2));*/
                        if(debug) {
                           out.format("key1 null? %s%n", (key1 == null));
                           out.format("key2 null? %s%n", (key2 == null));
                           out.format("trying pBA_val_map.get(%s)...%n", key2); //getting the val we're calculating total prob for...
                           out.format("does this work? be.get_pBA(%s, %s, false)%n", key1, key2);
                           out.format("be:",be.toString());
                        }
                        Object[] temp_pBA = be.get_pBA(key1, key2, false);
                        if(debug){
                           out.format("yay it worked! result: %s%n", be.temp_toStr(temp_pBA));
                           //out.format("pBA_val_map.get(key2) null? %s%n", (pBA_val_map.get(key2)  == null));
                        }
                        //total_probability += ((pBA_val_map.get(key2) / be.num_samples) * prior); //multiply by prior
                        total_probability += ((Double.parseDouble(temp_pBA[0].toString()) / be.num_samples) * prior); //multiply by prior
                        if(debug) {
                           out.format("calculate_total_probabilities(): total_probability += ((closest_match_key=%s)=%.5f / be.num_samples=%s) * prior=%.5f) = %.5f%n",
                                      temp_pBA[1],Double.parseDouble(temp_pBA[0].toString()),be.num_samples, prior, total_probability);
                           out.format("calculate_total_probabilities(): total_probability += %.5f = %.5f%n",((Double.parseDouble(temp_pBA[0].toString()) / be.num_samples) * prior), total_probability);
                        }
                        //key2 = temp_pBA[1].toString();
                     } catch(NullPointerException ex){
                        if(debug) ex.printStackTrace();
                        total_probability += 0.0;
                     }
                  } // end for voi-specific events
                  // add total probabilities to BayesianEvents
                  //out.format("events null? %s%n", (events == null));
                  for(BayesianEvent be : events){
                     //out.format("BayesianEvent be null? %s%n", (be == null));
                     //out.format("calculate_total_probabilities(): total_probability_map.get(%s)%n", key1);
                     HashMap<String, Double> total_probability_map = (HashMap<String, Double>) be.total_probabilities.get(key1);
                     //total_probability_map.put(key2, total_probability);
                     /*out.format("calculate_total_probabilities(): total_probability_map.put(%s, %s);%n", key2, total_probability);
                     out.println("be: "+be.toString());
                     out.format("total_probability_map null? %s%n", (total_probability_map == null));
                     out.format("vois: %s%n",Global.vars_of_interest);*/
                     total_probability_map.put(key2, total_probability);
                     //out.println(be.toString());
                  } // end for voi-specific events
                  if(debug) out.format("total probability for %s:%s = %.7f%n%n", key1, key2, total_probability);
               } // end for keys2
            }
         } // end for keys1
      }
      if(debug) out.println("end calculate_total_probabilities()\n\n");
   }
   
   
   public void print_cumulative_probabilities(){
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
               try{
                  str += dd.doubleValue() + " ";
               } catch(NullPointerException ex){
                  str += "null ";
               }
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
   
   
   public void print_delta_trackers(){
      out.println("DELTA TRACKERS:");
      Set<String> keys1 = delta_trackers.keySet();
      for(String key1 : keys1){
         out.println(key1+":"+delta_trackers.get(key1)+" ");
      }
      out.println();
   }
   
   
   public void print_bound_ids(){
      Set<String> keys1 = Global.bound_ids.keySet();
      out.println("BOUND IDS: "+keys1);
      for(String key1 : keys1){
         out.println(key1+":"+Global.bound_ids.get(key1)+" ");
      }
      out.println();
   }
   
   
   // TODO finish for smaller givens sets
   public Set<CompoundEvent> generateCompoundEvents(){
      Set<CompoundEvent> ces = new HashSet();
      Set set = Sets.newHashSet(Global.vars_of_interest);
      Set<Set> powerSet = Sets.powerSet(set);
      HashSet<Set<String>> prunedPowerSet = new HashSet();
      Iterator<Set> iter = powerSet.iterator();
      while(iter.hasNext()){
         Set<String> s = iter.next();
         if(s.size() > 2){
            prunedPowerSet.add(s);
         }
      }
      for(Set<String> s : prunedPowerSet){
         for(String A_var : s){
            HashSet<String> clone = new HashSet<String>(s);
            clone.remove(A_var);
            // make CompoundEvent with each var as an A-var
            String[] givens = new String[clone.size()];
            Iterator<String> iter2 = clone.iterator();
            int i = 0;
            while(iter2.hasNext()){
               String o = iter2.next();
               givens[i] = o.toString();
               i++;
            }
            CompoundEvent ce = new CompoundEvent(A_var, givens);
            ces.add(ce);
         }
         System.out.println();
      }
      /*System.out.println("\nCompoundEvents:");
      for(CompoundEvent ce : ces){
         System.out.println(ce.toString());
      }*/
      // correct up to here.
      // initialize with values
      //System.out.println("\nInitialize CompoundEvents values");
      Set<CompoundEvent> initialized_ces = new HashSet<CompoundEvent>();
      initialized_ces.addAll(ces);
      for(String var_name : cumulative_probabilities.keySet()){
         Set<CompoundEvent> temp1 = new HashSet<CompoundEvent>();
         for(String val : cumulative_probabilities.get(var_name).keySet()){
            Set<CompoundEvent> temp2 = new HashSet<CompoundEvent>();
            Iterator<CompoundEvent> iter2 = initialized_ces.iterator();
            while(iter2.hasNext()){
               CompoundEvent clone = iter2.next().clone();
               if(clone.givensContain(var_name)){
                  // initialize givens with value
                  clone.setGivensVal(var_name, val);
                  temp2.add(clone);
                  //System.out.println("var_name:"+var_name+" val:"+val+"; ce:"+clone.toString());
               } else if (clone.A_var_name.equals(var_name)) {
                  //set up A_var value
                  clone.setAVal(val);
                  clone.A_prior = get_prior(var_name, val);
                  temp2.add(clone);
                  //System.out.println("var_name:"+var_name+" val:"+val+"; ce:"+clone.toString());
               }
            }
            temp1.addAll(temp2);
            //printArr("temp1 using "+var_name+"="+val, temp1);
            //System.exit(0);
         }
         initialized_ces = temp1;
         //System.out.println("Finished with vals for "+var_name+"\n");
         //printArr("temp1 after exhausting values from "+var_name, temp1);
         //System.exit(0);
      }
      //printArr("initialized_ces", initialized_ces);
      //System.exit(0);
      return initialized_ces;
   }
   
   public void printArr(String title, Set arr){
      System.out.println(title+": ");
      for (Object o : arr){
         System.out.println(o.toString());
      }
   }
   
}
