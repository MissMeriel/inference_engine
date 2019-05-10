import java.util.ArrayList;
import java.util.logging.Logger;
import org.apache.commons.lang3.ArrayUtils;
import org.apache.commons.lang3.ArrayUtils.*;


public class BasicEngine{
   
   public static final Logger debugEngine = Logger.getLogger("BasicEngine");
   
   Object[][] csv_array;
   ArrayList<String> givens_vars;
   ArrayList<String> events_vars;
   ArrayList<Given> givens = new ArrayList<Given>();
   
   public BasicEngine(Object[][] csv_array){
      this.csv_array = csv_array;
      build_types();
   }
   
   public BasicEngine(Object[][] csv_array, ArrayList<String> givens, ArrayList<String> events){
      this.csv_array = csv_array;
      this.givens_vars = givens;
      this.events_vars = events;
   }

   public void build_types(){
      for(Object s : csv_array[0]) {
         System.out.println(s.toString());
      }
   }
   
   public void build_givens(ArrayList<String> givens_vars){
      for(String s : givens_vars) {
         //System.out.println(s.toString());
         givens.add(new Given(s.toString()));
      }
   }
   
   public void loop_through_trace(){
      build_givens(givens_vars);
      //System.out.println("\nBegin looping through trace");
      //System.out.println(csv_array[0].toString());
      Object[] row;
      //System.out.println(csv_array.length);
      //for(Object[] row : csv_array){
      for (int i = 1; i < csv_array.length; i++) {
         row = csv_array[i];
         int given_count = 0;
         Bin b = null;
         boolean bin_updated = false;
         for (String e: events_vars){
            //get only event vars
            int event_index = get_var_index(e);
            //System.out.println(e+":"+row[event_index]);
            String event_value = (String) row[event_index];

            for (Given g: givens){
               // update givens values
               int given_index = get_var_index(g.name);
               //System.out.println("Given "+g.name+" index in csv:"+given_index);
               String given_val = (String) row[given_index];
               //System.out.println(g.name+":"+given_val);
               // check for existing bin
               b = g.contains_bin(given_val);
               if(b != null && !bin_updated){
                  // add to existing bin
                  b.update();
               } else if(b == null) {
                  // create new bin to add to
                  b = g.add_bin(given_val);
               }
               //update events in bins
               if(!e.equals(g.name)){
               Event new_event = new Event(event_value);
               Event bin_event = b.contains_event(new_event);
               if(bin_event != null){
                  bin_event.update();   
               } else {
                  b.add_event(new Event(event_value, e));
               }
               }
            }
            bin_updated = true;
         }
      }
      for (int i = 0; i< givens.size(); i++){
         System.out.println(givens.get(i)+" \n\n");
      }
   } // end loop_through_trace()
   
   public int get_var_index(String var){
      return ArrayUtils.indexOf((Object[]) csv_array[0], (Object)var);
   }

   /*public ArrayList<Given> sort_givens(){
      //Comparator<Given> cmp = 
      return givens;
   }*/
   
}