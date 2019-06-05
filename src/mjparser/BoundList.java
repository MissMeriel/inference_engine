package mjparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class BoundList{
   public String var_name;
   //public String id;
   public HashMap<String, Predicate<Double>> id_map = new HashMap<String, Predicate<Double>>();
   //public Predicate<Double> tester;
   public ArrayList<Predicate<Double>> tester_list = new ArrayList<Predicate<Double>>();
   
   public BoundList(HashMap<String, Predicate<Double>> id_map, ArrayList<Predicate<Double>> tester_list){
      this.id_map = id_map;
      this.tester_list = tester_list;
   }
   
   public BoundList(){
      
   }
}