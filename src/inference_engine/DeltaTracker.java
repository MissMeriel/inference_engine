package inference_engine;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;
import static java.lang.System.out;
import static java.lang.Math.abs;


/**
 * 
 **/
public class DeltaTracker{
   
   public String var_name = null;
   
   public double delta = 1.0; //default value of one: only looking at current value
   public double timestep = 0.0; //how many seconds a delta step is worth
   public LinkedList<Double> next_values = new LinkedList<Double>();
   public LinkedList<Double> last_values = new LinkedList<Double>();
   public boolean derivative = false;
   
   public DeltaTracker(String var_name, double delta, double timestep){
      this.var_name = var_name;
      this.delta = delta;
      this.timestep = timestep;
   }
   
   
   public void update_last_values(Double new_val){
      last_values.add(new_val);
      if(last_values.size() > delta){
         last_values.remove();   
      }
   }
   
   public void update_next_values(Double new_val){
      next_values.add(new_val);
      if(next_values.size() > delta){
         next_values.remove();   
      }
   }
   
   /** 
    * @return list of invariants that hold at this moment in time
    * @return list of invariant ids for invariants that hold at this moment in time
    * @return whether those invariants hold for next_values or last_values or both
    **/
   public DeltaRecord check_invariants(){
      HashMap<String, Predicate<Double>> predicate_map = Global.bound_ids.get(var_name);
      DeltaRecord rec = new DeltaRecord(var_name);
      Set<String> keys = predicate_map.keySet();
      try{
         double next_delta = compute_next_delta();
         double last_delta = compute_last_delta();
      }catch(DeltaException ex){
         out.println(ex.getMessage());
      }
      for(String key: keys){
         
      }
      return rec;
   }
   
   public double compute_next_delta() throws DeltaException{
      if(delta == 1 && next_values.size() == delta){
         return 0.0;
      } else if (delta == 2 && next_values.size() == delta){
         // return slope of secant line
         return (next_values.get(1) - next_values.get(0))/(delta * timestep);
      } else if(next_values.size() == delta && derivative){
         // fit polynomial function then take derivative
         return 0.0;
      } else if(last_values.size() == delta && !derivative){
         // 
         return 0.0;
      } else {
         // TODO: throw exception
         throw new DeltaException(String.format("Exception encountered with computing next delta of delta %s and timestep %s%n", delta, timestep));
      }
   }
   
   public double compute_last_delta() throws DeltaException{
      if(delta == 1 && last_values.size() == delta){
         return 0.0;
      } else if (delta == 2 && last_values.size() == delta){
         // return slope of secant line
         return (last_values.get(1) - last_values.get(0))/(delta * timestep);
      } else if(last_values.size() == delta && derivative){
         // fit polynomial function then take derivative
         return 0.0;
      } else if(last_values.size() == delta && !derivative){
         // 
         return 0.0;
      } else {
         // TODO: throw exception
         throw new DeltaException(String.format("Exception encountered with computing next delta of delta %s and timestep %s%n", delta, timestep));
      }
   }
}