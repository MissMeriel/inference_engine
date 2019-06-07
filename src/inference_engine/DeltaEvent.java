package inference_engine;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;
import static java.lang.System.out;
import static java.lang.Math.abs;

public class DeltaEvent<T> extends BoundedEvent<T>{

   public double delta = 1.0; //delta of 1 is default; constrains queue length
   public LinkedList<T> next_values = new LinkedList<T>();
   public LinkedList<T> last_values = new LinkedList<T>();
   
   public DeltaEvent(String var_name, String id, double p_A, Predicate<Double> tester, double delta){
      super(var_name, id, p_A, tester);
      this.delta = delta;
   }
   


}