package inference_engine;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;
import static java.lang.System.out;
import static java.lang.Math.abs;

public class ConstraintEvent<T> extends BayesianEvent<T>{

   public HashMap<String, Predicate<Object>> event_testers = new HashMap<String, Predicate<Object>>();
   public HashMap<String,Predicate<Object>> given_testers = new HashMap<String, Predicate<Object>>();
   String id = "";
   
   public ConstraintEvent(String var_name, String id, double p_A, HashMap<String,Predicate<Object>> event_testers,
                          HashMap<String,Predicate<Object>> given_testers/*, ArrayList<String> vois*/){
      super(var_name, null, p_A);
      this.event_testers = event_testers;
      this.given_testers = given_testers;
      this.id = id;
      
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