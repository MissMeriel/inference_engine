

public class Event{
   int num_samples = 0;
   String sample;
   String type = "";
   
   public Event(String sample){
      this.sample = sample;
      update();
   }
   
   public Event(String sample, String type){
      this.sample = sample;
      this.type = type;
      update();
   }
   
   public int update(){
      return ++num_samples;
   }
   
   @Override
   public String toString(){
      return this.type + " "+this.sample + "("+this.num_samples+")";
   }

}