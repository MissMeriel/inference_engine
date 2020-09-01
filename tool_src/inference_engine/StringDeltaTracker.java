package inference_engine;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;
import java.util.Iterator;
import static java.lang.System.out;
import static java.lang.Math.abs;
import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;

/** Used by TypedBayesianEngine
 * Deltatracker encapsulates methods used to determine rate of change
 **/
public class StringDeltaTracker{
   
   public String var_name = null;

   public LinkedList<String> next_values = new LinkedList<String>(); //opposite of this -->//goes [future values, ..., current val]
   public LinkedList<String> last_values = new LinkedList<String>(); //goes [current val, ..., future values]
   public boolean derivative = false;
   public double timestep = 0;
   public double delta = 0.0;
   
   public StringDeltaTracker(String var_name, double delta, double timestep){
      this.var_name = var_name;
      this.delta = delta;
      if(timestep > 0){
         this.timestep = timestep;
      }
   }
   
   public void set_last_values(String[] new_last_values){
      LinkedList<String> last_vals = new LinkedList<String>();
      for(int i=0; i<new_last_values.length; i++){
         last_vals.add(new_last_values[i]);
      }
      this.last_values = last_vals;
      while(last_values.size() > delta){
         last_values.remove();
      }
   }
   
   public void set_next_values(String[] new_next_values){
      LinkedList<String> next_vals = new LinkedList<String>();
      for(int i=0; i<new_next_values.length; i++){
         next_vals.add(new_next_values[i]);
      }
      this.next_values = next_vals;
      while(next_values.size() > delta){
         next_values.remove();
      }
   }
   
   public void update_last_values(String new_val){
      last_values.add(new_val); //add to end of list
      if(last_values.size() > delta){
         last_values.remove();   //remove first element in list
      }
   }
   
   public void update_next_values(String new_val){
      next_values.add(new_val); //add to end of list
      if(next_values.size() > delta){
         next_values.remove();   //remove first element in list
      }
   }
   
   /** 
    * @return list of invariants that hold at this moment in time
    * @return list of invariant ids for invariants that hold at this moment in time
    * @return whether those invariants hold for next_values or last_values or both
    **/
   public DeltaRecord check_invariants(){
      HashMap<String, Predicate<Object>> predicate_map = Global.bound_ids.get(var_name);
      DeltaRecord rec = new DeltaRecord(var_name);
      Set<String> keys = predicate_map.keySet();
      try{
         String next_delta = compute_next_delta();
         String last_delta = compute_last_delta();
      }catch(DeltaException ex){
         out.println(ex.getMessage());
      }
      for(String key: keys){
         
      }
      return rec;
   }
   
   
   /** @return rate of change or Double.MIN_VALUE upon error
    * 0 for no change
    * 1 for change
    **/
   public String compute_next_delta() throws DeltaException{
      if(next_values.size() == delta){
         if(delta == 1){
            return next_values.getFirst() +"->"+ next_values.getFirst();
         } else {
            // take rate of change over first and last
            return next_values.getLast() +"->"+ next_values.getFirst(); // i think this should be switched around (last - first)
         }
      } else {
         throw new DeltaException(String.format("Exception encountered computing %s next delta; delta %s; timestep %s; next_values.size()=%s; next_values:%s%n", var_name, delta, timestep, next_values.size(), next_values));
      }
      //return next_values.getFirst() +"->"+ next_values.getFirst();
   }
   
   /** @return rate of change or Double.MIN_VALUE upon error
    * last values = [value_farthest in the past, ..., most_recent_value (current val)]
    **/
   public String compute_last_delta() throws DeltaException{
      if(last_values.size() == delta){
         if(delta == 1){
            return next_values.getFirst() +"->"+ next_values.getFirst();
         } else {
            return last_values.getLast() +"->"+ last_values.getFirst();
         }
      } else {
         throw new DeltaException(String.format("Exception encountered computing next delta of delta %s and timestep %s%n", delta, timestep));
      }
      //return next_values.getFirst() +"->"+ next_values.getFirst();
   }

   
   @Override
   public String toString(){
      try{
         return String.format("DeltaTracker %s: delta=%.3f; last_values=%s; next_values=%s; next delta=%s; last delta=%s%n", var_name, delta, last_values,next_values,compute_next_delta(),compute_last_delta());
      } catch(DeltaException de){
         out.println(de.getMessage());
         return String.format("DeltaTracker %s: delta=%.3f; last_values=%s; next_values=%s%n", var_name,delta, last_values, next_values);
      }
   }
   
}