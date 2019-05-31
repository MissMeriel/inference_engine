import java.util.HashMap;
public class Global{
   public static HashMap<String, HashMap<String, Double>> priors = new HashMap<String, HashMap<String, Double>>();
   public static final HashMap<String, Double>  thresholds = new HashMap<String, Double>(); //HashMap of thresholds for different VOIs
   public static HashMap<String, RawType> types = new HashMap<String, RawType>();
   static HashMap<String, String[]> bounds = new HashMap<String, String[]>(); //for vars with bounds e.g. x<3 x>=10 etc.
}