

public class TypedEvent<T>{
   //int trace_total;
   int num_samples = 0;
   T val = null;
   String var_name = null;
   double threshold = 0.0;
   
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
         if((this.val instanceof Integer || this.val instanceof Double) && (te.val instanceof Integer || te.val instanceof Double)){
            if (this.val instanceof Integer && te.val instanceof Integer){
               Integer a = (Integer) this.val;
               Integer b = (Integer) te.val;
               return (this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold));
            } else if (this.val instanceof Double && te.val instanceof Double) {
               Double a = (Double) this.val;
               Double b = (Double) te.val;
               return (this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold));
            } else if (this.val instanceof Double && te.val instanceof Integer){
               Double a = (Double) this.val;
               Integer b = (Integer) te.val;
               return (this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold));
            } else if (this.val instanceof Integer && te.val instanceof Double) {
               Integer a = (Integer) this.val;
               Double b = (Double) te.val;
               return (this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold));
            }
         } else {
            return (this.var_name.equals(te.var_name)) && (this.val.equals(te.val));
         }
      }
      return false;
   }
   
   @Override
   public String toString(){
      return this.val + "("+this.num_samples+")";
   }

   
}