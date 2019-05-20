import java.util.HashMap;
public class Global{
   public static HashMap<String, HashMap<String, Double>> priors = new HashMap<String, HashMap<String, Double>>();
   public static final HashMap<String, Double>  thresholds = new HashMap<String, Double>(); //HashMap of thresholds for different VOIs
   public static HashMap<String, RawType> types = new HashMap<String, RawType>();
}