import java.lang.reflect.Constructor;
import java.lang.reflect.Type;
import static java.lang.System.out;
import java.io.Console;
import java.nio.charset.Charset;
import java.lang.reflect.Field;
import java.lang.reflect.InvocationTargetException;

/** uses reflection to automatically instantiate classes using config-declared type
*   See Type enum
*/

public class TypeConverter{
   
   public TypeConverter(){
   
   }
   
   public static Class get_type_class(RawType type){
      switch(type){
         case STRING:
            return String.class;
            //break;
         case INT:
            return Integer.class;
            //break;
         case DOUBLE:
            return Double.class;
            //break;
         case BOOLEAN:
            return Boolean.class;
            //break;
      }
      return null;
   }
   
   public static Object instantiate_type(RawType type, String value){
      Object o = null;
      Class cls = get_type_class(type);
      Constructor[] allConstructors = cls.getDeclaredConstructors();
      Constructor ctor = null;
      for (Constructor ctr : allConstructors) {
         Class<?>[] pType  = ctr.getParameterTypes();
         if(pType.length == 1 && pType[0].equals(String.class)){
            /*System.out.println(ctr);
            System.out.println("\tusing pType:"+pType);
            System.out.println();*/
            ctor = ctr;
            break;
         }
      }
      try {
          ctor.setAccessible(true);
          o = ctor.newInstance(value);
      // production code should handle these exceptions more gracefully
      } catch (InstantiationException x) {
          x.printStackTrace();
      } catch (InvocationTargetException x) {
          x.printStackTrace();
      } catch (IllegalAccessException x) {
          x.printStackTrace();
      }
      return o;
   }
   
   public static Double toDouble(Object o){
      return (Double) o;
   }

}
