public class Fuzzy{

   /**
    * absolute value of difference <= threshold
    * use of <= to account for zero difference
    **/
   public static boolean eq(double a, double b, double threshold){
      return Math.abs(a-b) <= threshold;
   }

   public static boolean eq(int a, int b, int threshold){
      return Math.abs(a-b) <= threshold;
   }
   
   public static boolean eq(String a, String b, double threshold){
      if(threshold > 0){
         return a.contains(b);
      } else {
         return a.equals(b);
      }
      
   }
}