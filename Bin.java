import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;

public class Bin{
   //public T t;
   String template;
   ArrayList<Event> bin_events = new ArrayList<Event>();
   int num_samples = 0;
   String type = "";
   
   public Bin/*<T>*/(String template){
      this.template = template;
      update();
   }
   
   public int update(){
      return ++num_samples;
   }
   
   public void add_event(Event event){
      // if bin already contains event with same value, update num_samples
      Event e;
      if((e = contains_event(event)) != null){
         e.update();
      } else {
         // else make new event
         bin_events.add(event);
      }
   }
   
   public Event contains_event(Event event){
      //return bin_events.contains(event);
      if(event != null){
         for(Event e : bin_events){
            if(e.sample.equals(event.sample)) {
               return e;
            }
         }
      }
      return null;
   }
      
   public ArrayList<Event> get_bin_events(){
         return bin_events;
   }
   
   public double get_event_probability(Event e){
      return e.num_samples / (double) this.num_samples;
      
   }
   
   @Override
   public String toString(){
      String str = this.type+" "+this.template+" ("+this.num_samples+")";
      for (Event e : bin_events){
         str += "\n\t\t"+e.toString() +" "+ String.format("%.00f%%",get_event_probability(e)*100);
      }
      return str;
   }
   
    public String toString(String probability){
      String str = this.type+" "+this.template+" ("+this.num_samples+") "+probability;
      HashMap<String, String> event_groups = new HashMap<String,String>();
      for (Event e : bin_events){
         if(event_groups.get(e.type) == null){
            event_groups.put(e.type, "\n\t\t"+e.type +" ");
         }
      }
      for (Event e : bin_events){
         String event_group_string = event_groups.get(e.type);
         event_group_string += "\n\t\t\t"+e.toString() +" "+ String.format("%.00f%%",get_event_probability(e)*100);
         event_groups.put(e.type, event_group_string);
      }
      for (String k : event_groups.keySet()){
         str += event_groups.get(k);
      }
      return str;
   }

}