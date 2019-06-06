package inference_engine;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.TreeSet;
import java.util.Set;
import java.util.Iterator;
import java.util.logging.Logger;
import static java.lang.System.out;
import org.apache.commons.lang3.StringEscapeUtils;
import java.util.Comparator;
import static java.lang.Math.abs;
import java.util.function.Predicate;

public class TypedBayesianEngine extends BasicEngine {
   
   HashMap<String, HashMap<String, Double>> priors = null;
   ArrayList<BayesianGiven> givens = new ArrayList<BayesianGiven>();
   ArrayList<BayesianEvent> bayesian_events = new ArrayList<BayesianEvent>();
   HashMap<String, HashMap<String, Double[]>> cumulative_probabilities = null;
   //TreeSet<String> vars_of_interest = new TreeSet<String>();
   int trace_total;
   public static boolean debug = true;
   
   public static final Logger debugBayesianEngine = Logger.getLogger("BayesianEngine");
   public TypedBayesianEngine(Object[][] csv_array){
      super(csv_array, Global.givens, Global.events);
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
   }
   
   public void setup_threshold_means(){
      for(String voi_name : Global.vars_of_interest){
         Double threshold = Global.thresholds.get(voi_name);
         /*if(threshold == null){
            continue; //threshold = Double.MAX_VALUE;
         }*/
         RawType type = Global.types.get(voi_name);
         //System.out.println(voi_name+" type == niull? "+(type == null));
         TreeSet<Double> d_set = new TreeSet<Double>();
         TreeSet<Integer> i_set = new TreeSet<Integer>();
         TreeSet<String> s_set = new TreeSet<String>();
         for(int i = 1; i< csv_array.length; i++){
            String[] row = (String[]) csv_array[i];
            switch(type){
               case DOUBLE:
                  d_set.add(Double.valueOf(row[get_var_index(voi_name)]));
                  break;
               case INT:
                  i_set.add(Integer.valueOf(row[get_var_index(voi_name)]));
                  break;
               case STRING:
                  s_set.add(row[get_var_index(voi_name)]);
                  break;
               /*case INTEXP:
                  ArrayList<Predicate<Double>> bound_vals = Global.bounds.get(voi_name);
                  // create new BoundedEvents for each bound, add to bayesian_events, zero num_samples
                  break;
               case DOUBLEEXP:
                  
                  break;*/
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
               case INTEXP:
                  break;
               case DOUBLEEXP:
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
                  System.out.format("curr=%f%n",curr);
                  Double[] d_arr = new Double[]{0.0,0.0};
                  if(curr.equals(d_set.first())){
                     System.out.format("%f.equals(d_set.first())=%s%n", curr, (curr.equals(d_set.first())));
                     cumulative_probabilities.get(voi_name).put(curr.toString(), d_arr);
                     last = curr;
                     continue;
                  } else if (threshold == null){
                     System.out.println("threshold null");
                     cumulative_probabilities.get(voi_name).put(Double.toString(Math.round(curr)), d_arr);
                  } else if(threshold != null) {
                     if(last.equals(d_set.first()) && curr >= last - threshold){
                        continue;
                     } else {
                         
                     }
                     if(abs(last-curr) == threshold*2){
                        System.out.format("threshold=%f && abs(last=%f - curr=%f) >= threshold*2 = %s%n", threshold, last, curr, (abs(last-curr) >= threshold*2));
                        cumulative_probabilities.get(voi_name).put(curr.toString(), d_arr);
                        last = curr;
                     } else if(last-threshold > curr) {
                        System.out.format("threshold=%f && abs(last=%f - curr=%f) >= threshold*2 = %s%n", threshold, last, curr, (abs(last-curr) >= threshold*2));
                        cumulative_probabilities.get(voi_name).put(Double.toString(last-threshold*2), d_arr);
                        if(last-threshold*2 > curr+threshold){
                           cumulative_probabilities.get(voi_name).put(Double.toString(curr), d_arr);
                           last = curr;
                        } else {
                           last = last-threshold*2;
                        }
                     } else if(curr.equals(d_set.last()) && abs(last-curr)>threshold){
                        System.out.format("threshold=%f && %f .equals(d_set.last()) && abs(last=%f - curr=%f) > threshold%n", threshold, curr, last, curr, (abs(last-curr) >= threshold*2));
                        cumulative_probabilities.get(voi_name).put(curr.toString(), d_arr);
                     }
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
            case INTEXP:{
               HashMap<String, Predicate<Double>> bound_id_map = Global.bound_ids.get(voi_name);
               Set<String> keys = bound_id_map.keySet();
               for(String key: keys){
                  Double[] d_arr = new Double[]{0.0,0.0};
                  cumulative_probabilities.get(voi_name).put(key, d_arr);
               }
               // create new BoundedEvents for each bound, add to bayesian_events, zero num_samples
               break;}
            case DOUBLEEXP:{
               HashMap<String, Predicate<Double>> bound_id_map = Global.bound_ids.get(voi_name);
               Set<String> keys = bound_id_map.keySet();
               for(String key: keys){
                  Double[] d_arr = new Double[]{0.0,0.0};
                  cumulative_probabilities.get(voi_name).put(key, d_arr);
               }
            break;}
         }
      }//end voi for
      out.println("Finished setup_threshold_means()");
      out.println("\tSet up cumulative probabilities:");
      print_cumulative_probabilities();
      //System.exit(0);
   }
   
   
   public void setup_prior_uniform_dist(){
      //out.println("\nEntered setup_prior_uniform_dist()");
      //print_cumulative_probabilities();
      Global.priors = new HashMap<String, HashMap<String, Double>>();
      Set<String> keys1 = cumulative_probabilities.keySet();
      for(String key1 : keys1){
         HashMap<String, Double[]> cumulative_probability = cumulative_probabilities.get(key1);
         Set<String> keys2 = cumulative_probability.keySet();
         //out.println("key1:"+key1);
         for(String key2 : keys2){
            //out.println("key2:"+key2);
            try{
               Global.priors.get(key1).put(key2, 1.0/keys2.size());
            }catch(NullPointerException ex){
               Global.priors.put(key1, new HashMap<String, Double>());
               Global.priors.get(key1).put(key2, 1.0/keys2.size());
            }
         }
      }
      out.print("setup_prior_uniform_dist(): ");
      Driver.print_priors(Global.priors);
      //System.exit(0);
   }
   
    
   public void initialize_cumulative_var_probabilities(){
      cumulative_probabilities = new HashMap<String, HashMap<String, Double[]>>();
      Iterator<String> iter = Global.vars_of_interest.iterator();
      while(iter.hasNext()){
         String voi_name = iter.next();
         cumulative_probabilities.put(voi_name, new HashMap<String, Double[]>());
      }
      out.println("Finished initialize_cumulative_var_probabilities()\nCUMULATIVE PROBABILITIES: ");
      print_cumulative_probabilities();
      out.println();
   }
   
   
   public void loop_through_trace(){
      if(debug) debugBayesianEngine.info("Begin looping through trace");
      //debugBayesianEngine.info("Length of trace: "+csv_array.length);
      //debugBayesianEngine.info("Trace variables: "+csv_array[0]));
      Object[] row;
      initialize_cumulative_var_probabilities();
      preprocess_trace();
      // iterate over csv rows
      for (int i = 1; i < csv_array.length; i++) {
         if(debug) out.println("\n\nLOOP "+i);
         trace_total = i;
         row = csv_array[i];
         int given_count = 0;
         //BayesianBin b = null;
         boolean bin_updated = false;
         //update events in vars of interest
         Iterator<String> iter = Global.vars_of_interest.iterator();
         while(iter.hasNext()){
            String voi_name = iter.next();
            RawType type_enum = Global.types.get(voi_name);
            int event_index = get_var_index(voi_name);
            //out.println("RawType: "+type_enum);
            Iterator iter2 = Global.vars_of_interest.iterator();
            BayesianEvent be_test = null;
            String event_val = (String) row[event_index];
            // update frequency count for voi
            out.format("update_cumulative_probabilites(%s, %s, %s)%n", voi_name, event_val, i);
            update_cumulative_probabilites(voi_name, event_val, i);
            if(debug) out.print("Updated cumulative probabilities:");
            print_cumulative_probabilities();
            if(debug) { out.format("Get prior for %s:%s%n", voi_name, (String)event_val); }
            //retreive prior (special retrieval for fuzzy)
            double prior = get_prior(voi_name, event_val);
            switch(type_enum){
               case INT:
                  be_test = new BayesianEvent<Integer>(voi_name, Integer.parseInt(event_val), prior, Global.vars_of_interest);
                  break;
               case DOUBLE:
                  be_test = new BayesianEvent<Double>(voi_name, Double.parseDouble(event_val), prior, Global.vars_of_interest);
                  break;
               case STRING:
                  be_test = new BayesianEvent<String>(voi_name, event_val, prior, Global.vars_of_interest);
                  break;
               case INTEXP:{
                  Predicate<Double> tester = get_tester(voi_name, Double.parseDouble(event_val));
                  String tester_id = get_tester_id(voi_name, Double.parseDouble(event_val));
                  out.format("Got tester %s%n", tester_id);
                  be_test = new BoundedEvent<Integer>(voi_name, tester_id, prior, Global.vars_of_interest, tester);
                  break;}
               case DOUBLEEXP:{
                  Predicate<Double> tester = get_tester(voi_name, Double.parseDouble(event_val));
                  String tester_id = get_tester_id(voi_name, Double.parseDouble(event_val));
                  out.format("Got tester %s%n", tester_id);
                  be_test = new BoundedEvent<Double>(voi_name, tester_id, prior, Global.vars_of_interest, tester);
                  break;}
            }
            ArrayList voi_vals = get_voi_vals((String[]) csv_array[i]);
            out.println("vars_of_interest: "+Global.vars_of_interest);
            out.println("voi_vals: "+voi_vals);
            out.println("be_test == null?"+(be_test == null));
            out.println("\nbe_test.update_conditionals("+voi_vals+", true)");
            be_test.update_conditionals(voi_vals, true);
            //check that bayesian_events does not already contain this event
            boolean found = false;
            Iterator<BayesianEvent> iter_be = bayesian_events.iterator();
            BayesianEvent be = null;
            while(iter_be.hasNext()){
               be = iter_be.next();
               out.format("be EQUALS be_test: %s%n", be.equals(be_test));
               out.println("\tbe:"+be.toString()+"\n\tbe_test:"+be_test.toString());
               if(be.equals(be_test)){
                  //update count of this event and conditioned events
                  out.println("\nbe.update_conditionals("+voi_vals+", true)");
                  be.update_conditionals(voi_vals, true);
                  out.println("Updated bayesian event: "+be.toString());
                  found = true;
                  break;
               }
            }
            out.println();
            if(!found){
               out.println("Adding new bayesian event to list: "+be_test.toString());
               this.bayesian_events.add(be_test);
            } else {
               
               be.update();
            }
            out.println("\nBAYESIAN EVENTS at loop "+i+" after updating "+voi_name);
            for(BayesianEvent bev : bayesian_events){
               out.println(bev.toString());
            }
         }  // end vars_of_interest iterator
         //if(i >= 5){System.exit(0);}
      } // end csv loop
      out.println("\nFINISHED TRACE");
      for (int i = 0; i< givens.size(); i++){
         givens.get(i).set_total(trace_total);
         givens.get(i).set_priors(Global.priors);
         givens.get(i).set_cumulative_probabilities(cumulative_probabilities);
         //out.println(givens.get(i)+" \n\n");
      }
      out.format("CUMULATIVE PROBABILITIES: "); print_cumulative_probabilities();
      out.println("\nBAYESIAN EVENTS");
      sort_bayesian_events();
      for(BayesianEvent be : bayesian_events){
         out.println(be.toString());
      }
      Driver.print_priors(Global.priors);
      out.println("\n\nGENERATE BAYESIAN PROBABILITIES:");
      generate_bayesian_probabilities();
   } // end loop_through_trace()
   
   
   public Predicate<Double> get_tester(String voi_name, Double dbl){
      ArrayList<Predicate<Double>> al = Global.bounds.get(voi_name);
      for (Predicate<Double> p : al){
         if(p.test(dbl)){
            return p;
         }
      }
      return null;
   }
   
   
   public String get_tester_id(String voi_name, Double dbl){
      HashMap<String, Predicate<Double>> id_map = Global.bound_ids.get(voi_name);
      Set<String> keys = id_map.keySet();
      for (String key : keys){
         Predicate<Double> p = id_map.get(key);
         if(p.test(dbl)){
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
         System.out.println(voi_name+" priors:");
         System.out.println(voi_priors);
         RawType type_enum = Global.types.get(voi_name);
         try{
            Set<String> keys = voi_priors.keySet();
            for(String key : keys){
               d = voi_priors.get(key);
               switch(type_enum){
                  case DOUBLE:
                     double dbl = Double.parseDouble(event_val);
                     double key_val = Double.parseDouble(key);
                     if(debug/*true*/) out.format("get_prior: threshold for %s = %f%n", voi_name, voi_threshold);
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
            switch(type_enum){
               case INTEXP:
                  ArrayList<Predicate<Double>> al = Global.bounds.get(voi_name);
                  d = 1.0 / al.size();
                  break;
               case DOUBLEEXP:
                  al = Global.bounds.get(voi_name);
                  d = 1.0 / al.size();
                  break;
            }
         }

      }
      System.out.println("get_prior(): returning "+d);
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
      for (String voi : Global.vars_of_interest){
         int index = get_var_index(voi);
         al.add(row[index]);
      }
      return al;
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
         //cumulative_probability = new HashMap<String, Double[]>();
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
               case INTEXP:{
                  Predicate<Double> tester = Global.bound_ids.get(voi_name).get(key);
                  if(tester.test(Double.valueOf(val))){
                     found = true;
                     closest_match_key = key;
                  }
                  break;}
               case DOUBLEEXP:{
                  Predicate<Double> tester = Global.bound_ids.get(voi_name).get(key);
                  if(tester.test(Double.valueOf(val))){
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
      out.format("CUMULATIVE PROBABILITIES: "); print_cumulative_probabilities();
      out.format("end of TypedBayesianEngine.update_cumulative_probabilities()%n%n");
   } //end update_cumulative_probabilities()
   
   
   public String generate_bayesian_probabilities(){
      calculate_total_probabilities();
      String str = "";
      Iterator<BayesianEvent> iter = bayesian_events.iterator();
      while(iter.hasNext()){
         BayesianEvent be = iter.next();
         switch(be.prior_attribution){
            case PROB_A:
               String temp = be.generate_bayesian_probability(cumulative_probabilities);
               out.println(temp);
               str += temp;
               break;
         } // end switch
      } // end while
      return str;
   }
   
   
   public void calculate_total_probabilities(){
      out.println("inside calculate_total_probabilities()");
      out.println("Global vars of interest: "+Global.vars_of_interest);
      for(String voi : Global.vars_of_interest){
         ArrayList<BayesianEvent> events = new ArrayList<BayesianEvent>();
         for(BayesianEvent be : bayesian_events){
            if(be.var_name.equals(voi)){
               events.add(be);
            }
         }
         //use cumulative_probabilities keys so as not to miss bins
         Set<String> keys1 = cumulative_probabilities.keySet();
         for(String key1 : keys1){
            if(!key1.equals(voi)){
               Set<String> keys2 = cumulative_probabilities.get(key1).keySet();
               for(String key2 : keys2){
                  double total_probability = 0.0;
                  for(BayesianEvent be : events){
                     try{
                        HashMap<String, Double> pBA_val_map = (HashMap<String, Double>) be.pBAs.get(key1);
                        total_probability += pBA_val_map.get(key2) / be.num_samples;
                     } catch(NullPointerException ex){
                        total_probability += 0.0;
                     }
                  } // end for voi-specific events
                  for(BayesianEvent be : events){
                     HashMap<String, Double> total_probability_map = (HashMap<String, Double>) be.total_probabilities.get(key1);
                     total_probability_map.put(key2, total_probability);
                  } // end for voi-specific events
                  out.format("total probability for %s:%s = %.3f%n", key1, key2, total_probability);
               } // end for keys2
            }
         } // end for keys1
      }
      out.println("end calculate_total_probabilities()\n");
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
   
}
