import org.apache.commons.math3.fitting.WeightedObservedPoints;
import org.apache.commons.math3.fitting.PolynomialCurveFitter;

public class PolynomialDriver{
   
   public static void main(String[] args){
      // Collect data.
      final WeightedObservedPoints obs = new WeightedObservedPoints();
      /*obs.add(-1.00, 2.021170021833143);
      obs.add(-0.99, 2.221135431136975);
      obs.add(-0.98, 2.09985277659314);
      obs.add(-0.97, 2.0211192647627025);
      // ... Lots of lines omitted ...
      obs.add(0.99, -2.4345814727089854);*/
      obs.add(1.0,0.0);
      obs.add(2.0,1.1);
      obs.add(3.0,3.0);
      obs.add(4.0,4.3);
      obs.add(5.0,4.3);
      // Instantiate a third-degree polynomial fitter.
      final PolynomialCurveFitter fitter = PolynomialCurveFitter.create(2);
      
      // Retrieve fitted parameters (coefficients of the polynomial function).
      final double[] coeff = fitter.fit(obs.toList());
      for(int i = 0; i < coeff.length; i++){
         System.out.println(coeff[i]);
      }
      
   }

}