import java.util.ArrayList;

public class Given{
   public String name;
   public ArrayList<Bin> bins = new ArrayList<Bin>();

   public Given(String name, ArrayList<Bin> bins){
      this.name = name;
      this.bins = bins;
   }
   
   public Given(String name){
      this.name = name;
   }
   
   public Bin contains_bin(String template){
      for (Bin b : bins){
         if (b.template.equals(template)){
            return b;
         }
      }
      return null;
   }
   
   public Bin add_bin(String sample){
      Bin new_bin = new Bin(sample);
      bins.add(new_bin);
      return new_bin;
   }
   
   @Override
   public String toString(){
      String str = name + ":\n";
      for (Bin b : bins){
         str += "\t"+b.toString()+"\n";
      }
      return str;
   }

}