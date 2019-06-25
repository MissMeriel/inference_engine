package inference_engine;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.Iterator;
import java.util.function.Predicate;
import static java.lang.System.out;
import static java.lang.Math.abs;

public class ConstraintEvent<T> extends BayesianEvent<T>{

   public HashMap<String, Predicate<Object>> event_testers = new HashMap<String, Predicate<Object>>();
   public HashMap<String,Predicate<Object>> given_testers = new HashMap<String, Predicate<Object>>();
   String id = "";
   public TreeSet<String> vois = new TreeSet<String>();
   double pBA = 0.0;
   
   public ConstraintEvent(String var_name, String id, double p_A, HashMap<String,Predicate<Object>> event_testers,
                          HashMap<String,Predicate<Object>> given_testers){
      super(var_name, null, p_A);
      this.event_testers = event_testers;
      this.given_testers = given_testers;
      this.id = id;
      vois.addAll(event_testers.keySet());
      vois.addAll(given_testers.keySet());
      out.println("ConstraintEvent "+ var_name + " vois: "+vois.toString());
   }
   
   public void update_conditionals(ArrayList event_values){
      Double d;
      boolean temp_found = false;
      //this.debug = debug;
      //if event_values satisfy event, update num_samples
      Set<String> event_keys = event_testers.keySet();
      //Iterator<Object> iter = event_values.iterator();
      boolean event_result = true;
      int i = 0;
      //for(String key : event_keys){
      for(String voi : vois){
         if(event_keys.contains(voi)){
            Predicate<Object> tester = event_testers.get(voi);
            boolean result = tester.test(event_values.get(i));
            event_result = event_result && result;
         }
         i++;
      }
      if(event_result){
         num_samples++;
      } else {
         //event_values do not satisfy event
         return;
      }
      //if event_values also satisfy given, update pBAs
      Set<String> given_keys = given_testers.keySet();
      //iter = event_values.iterator();
      i = 0;
      boolean given_result = true;
      for(String voi : vois){
         if(event_keys.contains(voi)){
            Predicate<Object> tester = given_testers.get(voi);
            boolean result = tester.test(event_values.get(i));
            event_result = event_result && result;
         }
         i++;
      }
      if(given_result){
         pBA++;
      } else {
         //event_values do not satisfy given
         return;
      }
      //TODO: handling total probability with compound predicate(s)
      if(debug){
         //out.println("Updated conditionals in "+var_name+": "+pBAs);
         out.format("end of ConstraintEvent.update_conditionals(%s, %s)%n%n", event_values, debug);
      }
   }
   
   
   @Override
   public boolean equals(Object o){
      if(o instanceof ConstraintEvent){
         ConstraintEvent ce = (ConstraintEvent) o;
         //compare hashmaps?
         if(var_name.equals(ce.var_name) && id.equals(ce.id)){
            return true;
         }
      }
      return false;
   }
   
   @Override
   public String toString(){
      return String.format("ConstraintEvent:%s",var_name);
   }
}