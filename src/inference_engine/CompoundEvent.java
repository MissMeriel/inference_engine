package inference_engine;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Set;
import java.util.function.Predicate;
import static java.lang.System.out;
import static java.lang.Math.abs;

public class CompoundEvent{
   
   public int num_samples = 0;
   public ArrayList<Object> vals = null;
   public ArrayList<String> given_var_names = null;
   public String A_var_name = "";
   public double threshold = 0.0;
   public boolean debug = false;
   
   public CompoundEvent(String A_var_name, ArrayList<String> given_var_names){
      this.given_var_names = given_var_names;
      this.A_var_name = A_var_name;
   }
   
   public CompoundEvent(String A_var_name, String[] given_var_names){
      this.given_var_names = new ArrayList<String>(Arrays.asList(given_var_names));
      this.A_var_name = A_var_name;
   }
   
   public void addVal(String var_name, Object val){
      //int i = 
   }
   
   @Override
   public String toString(){
      String s = "P("+A_var_name+"|";
      for(int i = 0; i < given_var_names.size(); i++){
         //s += given_var_names.get(i)+"="+vals.get(i);
         s += given_var_names.get(i)+ " ";
      }
      s += ")";
      return s;
   }
   
   public boolean contains(String varname){
      if(A_var_name.equals(varname)){
         return true;
      } else if (given_var_names.contains(varname)){
         return true;
      }
      return false;
   }
   
   public boolean givensContain(String varname){
      if(given_var_names.contains(varname)){
         return true;
      }
      return false;
   }
   
   public CompoundEvent clone(){
      CompoundEvent ce = new CompoundEvent(A_var_name, given_var_names);
      ce.vals = this.vals;
      return ce;
   }

}