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
   public ArrayList<Object> vals = new ArrayList<Object>();
   public ArrayList<String> given_var_names = null;
   public String A_var_name = "";
   public Object A_val = null;
   public double threshold = 0.0;
   public boolean debug = false;
   public int A_count = 0;
   public int not_A_count = 0;
   public int givens_count = 0;
   public int givens_count_not_A = 0;
   public int all_count = 0;
   public int event_space = 0;
   public double A_prior = 0.0;
   
   public CompoundEvent(String A_var_name, ArrayList<String> given_var_names){
      this.given_var_names = given_var_names;
      this.A_var_name = A_var_name;
   }
   
   public CompoundEvent(String A_var_name, String[] given_var_names){
      this.given_var_names = new ArrayList<String>(Arrays.asList(given_var_names));
      this.A_var_name = A_var_name;
      for(String s : given_var_names){
         this.vals.add(null);
      }
   }
   
   public void addVal(String var_name, Object val){
      //int i = 
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
      CompoundEvent ce = new CompoundEvent(A_var_name, (ArrayList<String>)given_var_names.clone());
      ce.vals = (ArrayList<Object>) this.vals.clone();
      ce.A_val = this.A_val;
      ce.A_prior = this.A_prior;
      return ce;
   }
   
   public void setGivensVal(String var_name, Object val){
      if(given_var_names.contains(var_name)){
         int i = given_var_names.indexOf(var_name);
         vals.set(i, val);
      } else {
         given_var_names.add(var_name);
         vals.add(val);
      }
   }
   
   public void setAVal(Object val){
      this.A_val = val;
   }
   
   public boolean containsVal(Object event_val){
      if(A_val.equals(event_val)){
         return true;
      } else if (vals.contains(event_val)){
         return true;
      }
      return false;
   }
   
   public void update_A_count(){
      this.A_count++;
   }
   
   public void update_givens_count(){
      this.givens_count++;
   }
   
   public void update_all_count(){
      this.all_count++;
   }
   
   public void update_givens_count_not_A(){
      this.givens_count_not_A++;
   }
   
   public void update_not_A_count(){
      this.not_A_count++;
   }
   
   public void calculate_bayesian_probabilities(){
      String s = this.toString()+"=";
      double prob = (all_count / (double)A_count) * A_prior;
      prob /= (((all_count / (double) A_count)*A_prior) + ((givens_count_not_A / (double) not_A_count)*(1-A_prior)));
      if(Double.isNaN(prob) && not_A_count == 0){
         prob = (all_count / (double)A_count) * A_prior;
         prob /= (((all_count / (double) A_count)*A_prior) + (0*(1-A_prior)));
      } else if(Double.isNaN(prob) && A_count == 0){
         prob = 0.0;
      }
      /*System.out.println("("+all_count +"/" +A_count+") * "+A_prior+") / ((("+all_count+" / "+A_count+") * "+A_prior +
                         ") + (("+ givens_count_not_A+"/"+not_A_count+")*"+(1-A_prior)+"))");
      */
      s += prob;
      System.out.println(s);
   }
   
   public void calculate_bayesian_probabilities_with_computation_and_samplesize(){
      String s = this.toString()+" = ";
      double prob = (all_count / (double)A_count) * A_prior;
      prob /= (((all_count / (double) A_count)*A_prior) + ((givens_count_not_A / (double) not_A_count)*(1-A_prior)));
      if(Double.isNaN(prob) && not_A_count == 0){
         prob = (all_count / (double)A_count) * A_prior;
         prob /= (((all_count / (double) A_count)*A_prior) + (0*(1-A_prior)));
      } else if(Double.isNaN(prob) && A_count == 0){
         prob = 0.0;
      } else if (Double.isNaN(prob)){
         prob = 0.0;
      }
      s += String.format("%.5f * %.5f / %.5f = %.5f (%d) (A_count=%d)", A_prior, (all_count / (double)A_count), (((all_count / (double) A_count)*A_prior) + ((givens_count_not_A / (double) not_A_count)*(1-A_prior))), prob, all_count, A_count);
      //s += prob;
      System.out.println(s);
   }
   
   public void calculate_bayesian_probabilities_with_computation(){
      String s = this.toString()+" = ";
      double prob = (all_count / (double)A_count) * A_prior;
      prob /= (((all_count / (double) A_count)*A_prior) + ((givens_count_not_A / (double) not_A_count)*(1-A_prior)));
      if(Double.isNaN(prob) && not_A_count == 0){
         prob = (all_count / (double)A_count) * A_prior;
         prob /= (((all_count / (double) A_count)*A_prior) + (0*(1-A_prior)));
      } else if(Double.isNaN(prob) && A_count == 0){
         prob = 0.0;
      } else if (Double.isNaN(prob)){
         prob = 0.0;
      }
      s += String.format("%.5f * %.5f / %.5f = %.5f", A_prior, (all_count / (double)A_count), (((all_count / (double) A_count)*A_prior) + ((givens_count_not_A / (double) not_A_count)*(1-A_prior))), prob);
      //s += prob;
      System.out.println(s);
   }
   
   public String toString_with_counts(){
      String s = "P("+A_var_name+"="+A_val+" | ";
      for(int i = 0; i < given_var_names.size(); i++){
         //s += given_var_names.get(i)+"="+vals.get(i);
         s += given_var_names.get(i);
         try{
            s += "="+vals.get(i)+" ";
         } catch(Exception e){
            s += " ";
         }
      }
      s += ")";
      s += " all_found="+all_count;
      s += " givens_count="+givens_count;
      return s;
   }
   
   //TODO: override equals()
   /*@Override
   public boolean equals(Object o){
      if(o instanceof CompoundEvent){
         CompoundEvent ce = (CompoundEvent) o;
         if(ce.A_var_name.equals(this.A_var_name) && ce.given_var_names.equals(this.given_var_names)
            && ce.vals.equals(this.vals) && ce.A_val.equals(this.A_val) && ce.A_prior == this.A_prior){
            return true;
         }
      }
      return false;
   }*/
   
   @Override
   public String toString(){
      String s = "P("+A_var_name+"="+A_val+" | ";
      for(int i = 0; i < given_var_names.size(); i++){
         //s += given_var_names.get(i)+"="+vals.get(i);
         s += given_var_names.get(i);
         try{
            s += "="+vals.get(i)+" ";
         } catch(Exception e){
            s += " ";
         }
      }
      s += ")";
      return s;
   }

}