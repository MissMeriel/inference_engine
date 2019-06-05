package mjparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class Bound{
   public String var_name;
   public String id;
   //public HashMap<String, Predicate<Double>> = new HashMap<String, Predicate<Double>>();
   public Predicate<Double> tester;
   //public ArrayList<Predicate<Double> = new ArrayList<Predicate<Double>>();
   
   public Bound(String id, Predicate<Double> tester){
      this.id = id;
      this.tester = tester;
   }
}