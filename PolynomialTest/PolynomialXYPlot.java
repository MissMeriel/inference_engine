import javax.swing.JFrame;
import javax.swing.JPanel;
import java.awt.BorderLayout;
import org.jfree.chart.ChartFactory;
import org.jfree.chart.ChartPanel;
import org.jfree.chart.JFreeChart;
//import org.jfree.chart.plot.
import org.jfree.data.xy.XYSeriesCollection;
import org.jfree.data.xy.XYSeries;
import org.jfree.chart.plot.PlotOrientation;
import org.jfree.chart.plot.Plot;
import org.jfree.chart.plot.XYPlot;
import org.jfree.chart.renderer.xy.XYItemRenderer;
import org.jfree.chart.renderer.xy.XYLineAndShapeRenderer;


public class PolynomialXYPlot extends JFrame{
   
   double[] x;
   double[] y;
   double[] coeff;
   XYSeriesCollection dataset = new XYSeriesCollection();
   JFreeChart chart;
   
   public PolynomialXYPlot(String appTitle, double[] x, double[] y, double[] coeff){
      super(appTitle);
      this.x = x;
      this.y = y;
      this.coeff = coeff;
      JPanel panel = new JPanel();
      panel.setLayout(new BorderLayout(0,0));
      //XYLineAndShapeRenderer renderer = (XYLineAndShapeRenderer) chart.get();
      make_scatter();
      make_line();
      make_tangent_line();
      chart = ChartFactory.createXYLineChart("Line Fitting Test", "x", "y", dataset, PlotOrientation.VERTICAL, true, true, false);
      ChartPanel cp = new ChartPanel(chart);
      panel.add(cp, BorderLayout.CENTER);
      setContentPane(cp);
      /*System.out.println(chart.getRenderingHints().toString());
      XYPlot plot = (XYPlot) chart.getPlot();
      System.out.println("plot==null"+(plot==null));
      System.out.println(plot.getClass());
      XYItemRenderer renderer = plot.getRenderer();
      System.out.println("renderer==null"+(renderer==null));
      System.out.println(renderer.getClass());*/
   }
   
   
   public void make_scatter(){
      XYSeries series1 = new XYSeries("scatterplot");
      for(int i = 0; i < x.length; i++){
         series1.add(x[i],y[i]);
      }
      dataset.addSeries(series1);
   }
   
   public void make_line(){
      double[] x2 = new double[(int)Math.round(Math.ceil((x[x.length-1] - x[0]) / 0.1))+1];
      double[] y2 = new double[(int)Math.round(Math.ceil((int)(x[x.length-1] - x[0]) / 0.1))+1];
      int i = 0;
      for(double d = x[0]; d < x[x.length-1]; d+=0.1){
         x2[i] = d;
         y2[i] = coeff[2] * d * d + coeff[1] * d + coeff[0];
         System.out.println("i="+i+" d="+d);
         i++;
      }
      XYSeries series2 = new XYSeries("polynomial");
      //System.out.println("series2");
      for(i = 0; i < x2.length; i++){
         series2.add(x2[i],y2[i]);
         //System.out.println(x2[i]+","+y2[i]);
      }
      dataset.addSeries(series2);
   }
   
   public void make_tangent_line(){
      double[] x3 = new double[(int)Math.round(Math.ceil((x[x.length-1] - x[0]) / 0.1))+1];
      double[] y3 = new double[(int)Math.round(Math.ceil((int)(x[x.length-1] - x[0]) / 0.1))+1];
      int i = 0;
      double slope = coeff[2] * 2 * x[x.length-1] + coeff[1];
      double b = y[y.length-1] - slope * x[x.length-1];
      System.out.format("tangent line: y = %.3f *x + %.3f%n", slope, b);
      for(double d = x[0]; d < x[x.length-1]; d+=0.1){
         x3[i] = d;
         y3[i] = slope * d + b;
         //System.out.println("i="+i+" d="+d);
         i++;
      }
      XYSeries series3 = new XYSeries("tangent line");
      //System.out.println("series3");
      for(i = 0; i < x3.length; i++){
         series3.add(x3[i],y3[i]);
         //System.out.println(x3[i]+","+y3[i]);
      }
      dataset.addSeries(series3);
   }
   
}