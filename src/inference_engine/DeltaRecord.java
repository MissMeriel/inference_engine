package inference_engine;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;
import static java.lang.System.out;
import static java.lang.Math.abs;

/** Container for all invariants holding for a particular variable at a given time
 *
 */
public class DeltaRecord{
    
    public String var_name = null;
    // list of event invariants & invariant ids for invariants that hold at this moment in time
    //public HashMap<String, Predicate<Double>> next_invariants = new HashMap<String, Predicate<Double>>();
    public ArrayList<String> next_invariants = new ArrayList<String>();
    // list of given invariants & invariant ids for invariants that hold at this moment in time
    public ArrayList<String> last_invariants = new ArrayList<String>();
   
   public DeltaRecord(String var_name){
      this.var_name = var_name;
   }
   
   public void update_next_invariants(String new_id){
      this.next_invariants.add(new_id);
   }

   public void update_last_invariants(String new_id){
      this.last_invariants.add(new_id);
   }
   
}