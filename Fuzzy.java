public class Fuzzy{

   /**
    * absolute value of difference <= threshold
    * use of <= to account for zero difference
    **/
   public static boolean eq(double a, double b, double threshold){
      return Math.abs(a-b) <= threshold;
   }

}