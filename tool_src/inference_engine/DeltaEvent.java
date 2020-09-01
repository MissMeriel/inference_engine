package inference_engine;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.LinkedList;
import java.util.Set;
import java.util.function.Predicate;
import static java.lang.System.out;
import static java.lang.Math.abs;

public class DeltaEvent<T> extends BoundedEvent<T>{

   public double delta = 1.0; //delta of 1 is default; constrains queue length
   public LinkedList<T> next_values = new LinkedList<T>();
   public LinkedList<T> last_values = new LinkedList<T>();
   
   public DeltaEvent(String var_name, String id, double p_A, Predicate<Object> tester, double delta){
      super(var_name, id, p_A, tester);
      this.delta = delta;
   }
   
   /** equals checks class, varname, and delta
    *
    **/
   @Override
   public boolean equals(Object o){
      if(o instanceof DeltaEvent){
         DeltaEvent temp = (DeltaEvent) o;
         /*out.format("DeltaEvent equals: this.equals(be):%n");
         out.println("\tthis: "+this.toString());
         out.println("\ttemp: "+temp.toString());
         //out.format("this.id null? %s%n",(this.id==null));
         out.format("temp.id null? %s%n",(temp.id==null));
         out.format("temp.delta=%s this.delta=%s temp.var_name=%s this.var_name=%s temp.id=%s this.id=%s%n", temp.delta, this.delta, temp.var_name, this.var_name, temp.id, this.id);*/
         if(temp.delta == this.delta && temp.var_name.equals(this.var_name) && temp.id.equals(this.id)){
            return true;
         }
      }
      return false;
   }
   
   @Override
   public String toString(){
      String str = String.format("DeltaEvent %s:%s delta=%.2f p_A:%.2f", var_name, this.id, delta, p_A);
      str+= " pBAs: " + pBAs;
      str += " num_samples: " +num_samples;
      //str += " tester: "+tester.toString();
      return str;
   }

}