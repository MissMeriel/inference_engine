import java.util.HashMap;
import java.util.ArrayList;

public class BayesianEvent<T> extends TypedEvent{
   
   Prior prior_attribution = Prior.PROB_A;
   HashMap<String, Event> pBAs = null;   
   double p_A;
   T val = null;
   String var_name = null;
   
   /**
    * default constructor: p_A from .priors file
    **/
   public BayesianEvent(String var_name, T val, double p_A){
      super(var_name, val);
      this.p_A = p_A;
   }
   
   /**
    * configuration constructor: p_A from .priors file
    **/
   public BayesianEvent(String var_name, T val, Prior prior_attribution){
      super(var_name, val);
      this.prior_attribution = prior_attribution;
   }
   
   public void update_conditionals(Event ev){
      Event e;
      try{
         e = pBAs.get(ev.var_name);
         e.update();
         pBAs.put(ev.var_name, e);
      } catch(NullPointerException ex){
         pBAs.put(var_name, ev);
      }
   }
   
}