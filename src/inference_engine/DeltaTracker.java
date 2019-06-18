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
public class DeltaTracker{
   
   public String var_name = null;
   
   public double delta = 1.0; //default value of one: only looking at current value
   public double timestep = 1.0; //how many seconds a delta step is worth
   public LinkedList<Double> next_values = new LinkedList<Double>(); //goes [future values, ..., current val]
   public LinkedList<Double> last_values = new LinkedList<Double>(); //goes [current val, ..., future values]
   public boolean derivative = false;
   
   public DeltaTracker(String var_name, double delta, double timestep){
      this.var_name = var_name;
      this.delta = delta;
      if(timestep > 0){
         this.timestep = timestep;
      }
   }
   
   public void set_last_values(double[] new_last_values){
      LinkedList<Double> last_vals = new LinkedList<Double>();
      for(int i=0; i<new_last_values.length; i++){
         last_vals.add(new_last_values[i]);
      }
      this.last_values = last_vals;
      while(last_values.size() > delta){
         last_values.remove();
      }
   }
   
   public void set_next_values(double[] new_next_values){
      LinkedList<Double> next_vals = new LinkedList<Double>();
      for(int i=0; i<new_next_values.length; i++){
         next_vals.add(new_next_values[i]);
      }
      this.next_values = next_vals;
      while(next_values.size() > delta){
         next_values.remove();
      }
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
   
   
   /** @return rate of change or Double.MIN_VALUE upon error
    *
    **/
   public double compute_next_delta() throws DeltaException{
      if(next_values.size() == delta){
         if(delta == 1){
            return 0.0;
         } else if (delta == 2){
            // return slope of secant line
            out.format("compute_next_delta(): (next_values.get(1)=%.2f - next_values.get(0)=%.2f) / timestep=%.2f = ", next_values.get(1), next_values.get(0), timestep, (next_values.get(1) - next_values.get(0))/(timestep));
            return (next_values.get(1)-next_values.get(0))/(timestep);
         } else if(derivative){
            // fit polynomial function then take derivative
            WeightedObservedPoints obs = new WeightedObservedPoints();
            Iterator<Double> iter = last_values.iterator();
            int i = 0;
            while(iter.hasNext()){
               obs.add(timestep * i++, iter.next());
            }
            // Instantiate a third-degree polynomial fitter.
            final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);            
            // Retrieve fitted parameters (coefficients of the polynomial function).
            final double[] coeff = fitter.fit(obs.toList());
            double slope = coeff[2] * 2 * last_values.getLast() + coeff[1];
            return slope;
         } else if(!derivative){
            // take rate of change over first and last
            return (last_values.getFirst() - last_values.getLast()) / timestep;
         }
      } else {
         throw new DeltaException(String.format("Exception encountered computing %s next delta; delta %s; timestep %s; next_values.size()=%s; next_values:%s%n", var_name, delta, timestep, next_values.size(), next_values));
      }
      return Double.MIN_VALUE;
   }
   
   /** @return rate of change or Double.MIN_VALUE upon error
    *
    **/
   public double compute_last_delta() throws DeltaException{
      if(last_values.size() == delta){
         if(delta == 1){
            return 0.0;
         } else if (delta == 2 || var_name.equals("Trust_Human")){
            // return slope of secant line
            out.format("compute_last_delta(): (last_values.get(1)=%.2f - last_values.get(0)=%.2f) / timestep=%.2f = ", last_values.get(1), last_values.get(0), timestep, (last_values.getFirst() - last_values.getLast()) / timestep);
            return (last_values.getLast()-last_values.getFirst()) / timestep;
         } else if(derivative){
            // fit polynomial function then take derivative
            WeightedObservedPoints obs = new WeightedObservedPoints();
            Iterator<Double> iter = last_values.iterator();
            int i = 0;
            while(iter.hasNext()){
               obs.add(timestep * i++, iter.next());
            }
            // Instantiate a third-degree polynomial fitter.
            final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);            
            // Retrieve fitted parameters (coefficients of the polynomial function).
            final double[] coeff = fitter.fit(obs.toList());
            double slope = coeff[2] * 2 * last_values.getLast() + coeff[1];
            return slope;
         } else if(!derivative){
            // take rate of change over first and last
            return (last_values.getFirst() - last_values.getLast()) / (delta * timestep);
         }
      } else {
         throw new DeltaException(String.format("Exception encountered computing next delta of delta %s and timestep %s%n", delta, timestep));
      }
      return Double.MIN_VALUE;
   }

   
   @Override
   public String toString(){
      try{
         return String.format("DeltaTracker %s: delta=%.3f; last_values=%s; next_values=%s; next delta = %.2f; last delta=%.2f%n",var_name,delta, last_values,next_values,compute_next_delta(),compute_last_delta());
      } catch(DeltaException de){
         out.println(de.getMessage());
         return String.format("DeltaTracker %s: delta=%.3f; last_values=%s; next_values=%s%n",var_name,delta,last_values,next_values);
      }
   }
   
}