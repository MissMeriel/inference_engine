import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Comparator;
import static java.lang.System.out;

public class Bin{

   String template;
   protected ArrayList<Event> bin_events = new ArrayList<Event>();
   int num_samples = 0;
   String type = "";
   
   public Bin/*<T>*/(String template){
      this.template = template;
      update();
   }
   
      public Bin/*<T>*/(String type, String template){
      this.template = template;
      this.type = type;
      update();
   }
   
   public int update(){
      return ++num_samples;
   }
   
   public void add_event(Event event){
      // if bin already contains event with same value, update num_samples
      Event e;
      //System.out.println("adding event "+event.sample+" to bin "+template);
      if((e = contains_event(event)) != null){
         e.update();
      } else {
         // else make new event
         this.bin_events.add(event);
      }
   }
   
   public Event contains_event(Event event){
      if(event != null){
         //out.format("bin events:%d%n",this.bin_events.size());
         for(Event e : this.bin_events){
            //System.out.format("e.sample:%s event.sample:%s %n",e.sample,event.sample);
            if(e.val.equals(event.val)) {
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
   
   public ArrayList<Event> sort_events(){
      bin_events.sort(new Comparator<Event>(){
         public int compare(Event a, Event b){
            return a.val.compareTo(b.val);
         }
         });
      return bin_events;
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
      sort_events();
      String str = this.type+" "+this.template+" ("+this.num_samples+") "+probability;
      HashMap<String, String> event_groups = new HashMap<String,String>();
      for (Event e : bin_events){
         if(event_groups.get(e.var_name) == null){
            event_groups.put(e.var_name, "\n\t\t"+e.var_name +" ");
         }
      }
      for (Event e : bin_events){
         String event_group_string = event_groups.get(e.var_name);
         event_group_string += "\n\t\t\t"+e.toString() +" "+ String.format("%.00f%%",get_event_probability(e)*100);
         event_groups.put(e.var_name, event_group_string);
      }
      for (String k : event_groups.keySet()){
         str += event_groups.get(k);
      }
      return str;
   }

}