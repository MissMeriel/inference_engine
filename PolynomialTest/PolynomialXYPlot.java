import javax.swing.JFrame;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.plot.PolynomialFunction2D;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;

public class PolynomialXYPlot extends JFrame{
   
   ArrayList<Double> x1 = new ArrayList<Double>();
   ArrayList<Double> y1 = new ArrayList<Double>();
   double[] coeff;
   
   public PolynomialXYPlot(String appTitle, String chartTitle, ArrayList<Double> x1, ArrayList<Double> y1, double[] coeff){
      super(appTitle);
      this.x1 = x1;
      this.y1 = y1;
      this.coeff = coeff;
   }
   
   public void createChart(){
      JPanel jpanel = new JPanel();
      jpanel.setLayout(new BorderLayout(0,0));
      XYSeriesCollection dataset1 = new XYSeriesCollection();
      XYSeries series1 = new XYSeries("series1");
      for(int i = 0; i < x1.size(); i++){
         series1.add(x1.get(i), y1.get(i));
      }
   }

}