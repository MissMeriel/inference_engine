import static java.lang.System.out;

public class TypedEvent<T>{
   //int trace_total;
   int num_samples = 0;
   T val = null;
   String var_name = null;
   double threshold = 0.0;
   boolean debug = false;
   
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
   
   public int zero_samples(){
      num_samples = 0;
      return num_samples;
   }
   
   @Override
   public boolean equals(Object o){
      if(o instanceof TypedEvent){
         TypedEvent te = (TypedEvent) o;
         double threshold = 0.0;
         try{
            threshold = Global.thresholds.get(this.var_name);
         } catch(NullPointerException ex){
            threshold = Double.MAX_VALUE; //round all doubles
         }
         
         if((this.val instanceof Integer || this.val instanceof Double) && (te.val instanceof Integer || te.val instanceof Double)){
            if (this.val instanceof Integer && te.val instanceof Integer){
               if(debug) out.format("equals: %s instanceof Integer && %s instanceof Integer%n", this.val, te.val);
               Integer a = (Integer) this.val;
               Integer b = (Integer) te.val;
               if(threshold == Double.MAX_VALUE) { threshold = 0; }
               if(debug) out.format("equals: Returning %s%n",(this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold)));
               return (this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold));
            } else if (this.val instanceof Double && te.val instanceof Double) {
               if(debug) out.format("equals: %s instanceof Double && %s instanceof Double%n", this.val, te.val);
               Double a = (Double) this.val;
               Double b = (Double) te.val;
               if(threshold == Double.MAX_VALUE){
                  if(debug) out.format("equals: Returning %s%n",(this.var_name.equals(te.var_name)) && (Math.round(a.doubleValue()) == Math.round(b.doubleValue())));
                  return (this.var_name.equals(te.var_name) && Math.round(a.doubleValue())==Math.round(b.doubleValue()));
               } else {
                  if(debug) out.format("equals: Returning %s%n",(this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold)));
                  return (this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold));
               }
            } else if (this.val instanceof Double && te.val instanceof Integer){
               if(debug) out.format("equals: %s instanceof Double && %s instanceof Integer%n", this.val, te.val);
               Double a = (Double) this.val;
               Integer b = (Integer) te.val;
               if(threshold == Double.MAX_VALUE){
                  long c = Math.round(a.doubleValue());
                  if(debug) out.format("equals: Returning %s%n",(this.var_name.equals(te.var_name)) && (c==b.doubleValue()));
                  return (this.var_name.equals(te.var_name)) && (c==b.doubleValue());
               } else {
                  if(debug) out.format("equals: Returning %s%n",(this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold)));
                  return (this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold));
               }
            } else if (this.val instanceof Integer && te.val instanceof Double) {
               if(debug) out.format("equals: %s instanceof Integer && %s instanceof Double%n", this.val, te.val);
               Integer a = (Integer) this.val;
               Double b = (Double) te.val;
               if(threshold == Double.MAX_VALUE){
                  long c = Math.round(b.doubleValue());
                  if(debug) out.format("equals: Returning %s%n", (this.var_name.equals(te.var_name) && (a.doubleValue()==c)));
                  return (this.var_name.equals(te.var_name) && (a.doubleValue()==c));
               } else {
                  if(debug) out.format("equals: Returning %s%n",(this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold)));
                  return (this.var_name.equals(te.var_name)) && (Fuzzy.eq(a.doubleValue(), b.doubleValue(), threshold));
               }
            }
         } else {
            if(debug) out.format("equals: Returning %s%n",(this.var_name.equals(te.var_name)) && (this.val.equals(te.val)));
            return (this.var_name.equals(te.var_name)) && (this.val.equals(te.val));
         }
      }
      if(debug) out.format("equals: Returning %s%n",false);
      return false;
   }
   
   
   @Override
   public String toString(){
      return this.val + "("+this.num_samples+")";
   }

   
}