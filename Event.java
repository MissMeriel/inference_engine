

public class Event{
   int num_samples = 0;
   String val;
   String var_name = "";
   
   public Event(String val){
      this.val = val;
      update();
   }

   public Event(String var_name, String val){
      this.val = val;
      this.var_name = var_name;
      update();
   }
   
   public int update(){
      return ++num_samples;
   }
   
   @Override
   public String toString(){
      return this.val + "("+this.num_samples+")";
   }

}