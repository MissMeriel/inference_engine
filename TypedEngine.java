import java.util.HashMap;
import java.util.ArrayList;

public class TypedEngine extends BasicEngine {

   
   public HashMap<String,RawType> types;
   
   public TypedEngine(Object[][] csv_array, ArrayList<String> givens,
                      ArrayList<String> events, HashMap<String, RawType> types){
      super(csv_array, givens, events);
      this.types = types;
   }
   
   public void loop_through_trace(){
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
               Event new_event = new Event(event_value);
               Event bin_event = b.contains_event(new_event);
               if(bin_event != null){
                  bin_event.update();   
               } else {
                  b.add_event(new Event(event_value, e));
               }
            }
            bin_updated = true;
         }
      }
      for (int i = 0; i< givens.size(); i++){
         System.out.println(givens.get(i)+" \n\n");
      }
   } // end loop_through_trace()

}