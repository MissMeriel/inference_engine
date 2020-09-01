package inference_engine;

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
   
   public double get_bin_probability(Bin bin){
      System.out.println("Given get_bin_probability");
      double total = 0.0;
      for (Bin b : bins){
         total += b.num_samples;
      }
      return bin.num_samples/total;
   }
   
   /*public ArrayList<Bin> sort_bins(){
      return bins.sort(new Comparator<Bin>(){
         public int compare(Bin a, Bin b){
            return a.
         }
         });
   }*/
   
   @Override
   public String toString(){
      System.out.println("Given toString");
      String str = name + ":\n";
      //System.out.println(bins.size());
      for (Bin b : bins){
         String prob = String.format("%.0f%%", get_bin_probability(b)*100);
         str += "\t"+b.toString(prob)+" "+"\n";
         System.out.println(prob);
      }
      return str;
   }

}
