import java.util.ArrayList;

public class Bin{
   //public T t;
   String template;
   ArrayList<Event> bin_events = new ArrayList<Event>();
   int num_samples = 0;
   String type = "";
   
   public Bin/*<T>*/(String template){
      this.template = template;
      
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
   
   @Override
   public String toString(){
      String str = this.type+" "+this.template+" ("+this.num_samples+")";
      for (Event e : bin_events){
         str += "\n\t\t"+e.toString();
      }
      return str;
   }

}