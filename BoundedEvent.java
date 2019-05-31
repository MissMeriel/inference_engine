import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Set;
import static java.lang.System.out;
import static java.lang.Math.abs;
import java.util.function.Predicate;

public class BoundedEvent<T> extends BayesianEvent{
   double lower_bound = Double.MIN_VALUE;
   double upper_bound = Double.MAX_VALUE;
   Predicate<Double> tester = (Double x) -> {return x > this.lower_bound && x < this.upper_bound;};
   
   public BoundedEvent(String var_name, T val, double p_A, TreeSet<String> vars_of_interest, double lower_bound, double upper_bound){
      super(var_name, val, p_A, vars_of_interest);
      this.lower_bound = lower_bound;
      this.upper_bound = upper_bound;
   }
   
   public BoundedEvent(String var_name, T val, double p_A, TreeSet<String> vars_of_interest, double bound, Bound bound_type){
      super(var_name, val, p_A, vars_of_interest);
      switch(bound_type){
         case UPPER:
            upper_bound = bound;
            break;
         case LOWER:
            lower_bound = bound;
            break;
      }
   }
   
   public BoundedEvent(String var_name, T val, double p_A, TreeSet<String> vars_of_interest, Predicate<Double> tester){
      super(var_name, val, p_A, vars_of_interest);
      this.tester = tester;
   }
   
   public boolean check_bounds(double x){
      return tester.test(Double.valueOf(x));
   }
   
   @Override
   public boolean equals(Object o){
      if(o instanceof BoundedEvent){
         BoundedEvent be = (BoundedEvent) o;
         out.format("BoundedEvent equals: this.tester.equals(be.tester)=%s%n", this.tester.equals(be.tester));
         return (this.var_name.equals(be.var_name)) && lower_bound == be.lower_bound && upper_bound == be.upper_bound ;
      } else {
         return false;
      }
   }
}