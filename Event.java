

public class Event{
   int num_samples = 0;
   String sample;
   String type = "";
   
   public Event(String sample){
      this.sample = sample;
      update();
   }
   
   public Event(String type, String sample){
      this.sample = sample;
      this.type = type;
      update();
   }
   
   public int update(){
      return ++num_samples;
   }
   
   @Override
   public String toString(){
      return this.sample + "("+this.num_samples+")";
   }

}