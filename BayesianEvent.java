import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import static java.lang.System.out;

public class BayesianEvent<T> extends TypedEvent{
   
   Prior prior_attribution = Prior.PROB_A;
   HashMap<String, HashMap<String, Double>> pBAs = null;   
   double p_A;
   TreeSet<String> vars_of_interest = new TreeSet<String>();
   boolean debug = true;
   
   /**
    * default constructor: p_A from .priors file
    **/
   public BayesianEvent(String var_name, T val, double p_A, TreeSet<String> vars_of_interest){
      super(var_name, val);
      this.p_A = p_A;
      this.vars_of_interest = vars_of_interest; //consider removing this.var_name
      initialize_pBAs();
   }
   
   /**
    * configuration constructor: p_A from .priors file
    **/
   public BayesianEvent(String var_name, T val, Prior prior_attribution){
      super(var_name, val);
      this.prior_attribution = prior_attribution;
   }
   
   public void initialize_pBAs(){
      pBAs = new HashMap<String, HashMap<String, Double>>();
      for(String voi : vars_of_interest){
         if(!voi.equals(this.var_name)){
            pBAs.put(voi, new HashMap<String, Double>());
         }
      }
   }
   
   /*public void update_conditionals(Event ev){
      Event e;
      try{
         e = pBAs.get(ev.var_name);
         e.update();
         pBAs.put(ev.var_name, e);
      } catch(NullPointerException ex){
         pBAs.put(var_name, ev);
      }
   }*/
   
   public void update_conditionals(ArrayList event_values){
      int i = 0;
      Double d;
      for(String voi: vars_of_interest){
         if(!voi.equals(this.var_name)){
            try{
               d = pBAs.get(voi).get(event_values.get(i));
               d++;
               pBAs.get(voi).put(event_values.get(i).toString(), d);
            } catch(NullPointerException ex){
               pBAs.get(voi).put(event_values.get(i).toString(), 1.0);
            }
         }
         i++;
      }
      if(debug) out.println("Updated conditionals in "+var_name+"="+val+": "+pBAs);
   }
   
   @Override
   public String toString(){
      String str = String.format("BayesianEvent %s:%s p_A:%.2f", var_name, val.toString(), p_A);
      str+= " pBAs: " + pBAs;
      return str;
   }
   
}