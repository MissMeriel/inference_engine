package mjparser;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.function.Predicate;

public class BoundList{
   public String var_name;
   public String[] var_names;
   //public String id;
   public HashMap<String, Predicate<Object>> id_map = new HashMap<String, Predicate<Object>>();
   //public Predicate<Double> tester;
   public ArrayList<Predicate<Object>> tester_list = new ArrayList<Predicate<Object>>();
   
   public BoundList(HashMap<String, Predicate<Object>> id_map, ArrayList<Predicate<Object>> tester_list){
      this.id_map = id_map;
      this.tester_list = tester_list;
   }
   
   public BoundList(HashMap<String, Predicate<Object>> id_map, ArrayList<Predicate<Object>> tester_list, String[] var_names){
      this.id_map = id_map;
      this.tester_list = tester_list;
      this.var_names = var_names;
   }
   
   public BoundList(){
      
   }
}