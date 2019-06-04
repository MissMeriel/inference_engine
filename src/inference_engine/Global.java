package inference_engine;

import java.util.HashMap;
import java.util.ArrayList;
import java.util.function.Predicate;

public class Global{
   public static HashMap<String, HashMap<String, Double>> priors = new HashMap<String, HashMap<String, Double>>();
   public static final HashMap<String, Double>  thresholds = new HashMap<String, Double>(); //HashMap of thresholds for different VOIs
   public static HashMap<String, RawType> types = new HashMap<String, RawType>();
   public static ArrayList<String> givens = new ArrayList<String>();
   public static ArrayList<String> events = new ArrayList<String>();
   public static ArrayList<String> vars_of_interest = new ArrayList<String>();
   public static HashMap<String, ArrayList<Predicate<Double>>> bounds = new HashMap<String, ArrayList<Predicate<Double>>>(); //for vars with bounds e.g. x<3 x>=10 etc.
   public static HashMap<String, HashMap<String, Predicate<Double>>> bound_ids = new HashMap<String, HashMap<String, Predicate<Double>>>();
   public static HashMap<String, Double> deltas = new HashMap<String, Double>();
}
