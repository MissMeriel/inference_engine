

public class TypedEvent<T>{
   int num_samples = 0;
   T val;
   String var_name = "";
   
   public TypedEvent(T val){
      this.val = val;
      update();
   }

   public TypedEvent(String var_name, T val){
      this.val = val;
      this.var_name = var_name;
      update();
   }
   
   public int update(){
      return ++num_samples;
   }
   
   @Override
   public boolean equals(Object o){
      if(o instanceof TypedEvent){
         TypedEvent te = (TypedEvent) o;
         return (this.var_name.equals(te.var_name)) && (this.val.equals(te.val));
      }
      return false;
   }
   
   @Override
   public String toString(){
      return this.val + "("+this.num_samples+")";
   }

   
}