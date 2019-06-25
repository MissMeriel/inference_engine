package mjparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;
import java.util.Arrays;

public class Bound{
   public String var_name;
   public String[] var_names;
   public String id;
   public HashMap<String, Predicate<Object>> testers = new HashMap<String, Predicate<Object>>();
   public Predicate<Object> tester;
   //public ArrayList<Predicate<Double> = new ArrayList<Predicate<Double>>();
   
   public Bound(String var_name, String id, Predicate<Object> tester){
      this.var_name = var_name;
      this.id = id;
      this.tester = tester;
   }
   
   public Bound(String id, HashMap<String, Predicate<Object>> testers, String[] var_names){
      this.id = id;
      this.tester = tester;
      this.var_names = var_names;
   }
   
   @Override
   public String toString(){
      return String.format("Bound %s %s %s", var_name, Arrays.toString(var_names), id);
   }
}