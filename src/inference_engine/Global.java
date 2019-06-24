package inference_engine;

import java.util.HashMap;
import java.util.TreeSet;
import java.util.ArrayList;
import java.util.function.Predicate;

public class Global{
   public static HashMap<String, HashMap<String, Double>> priors = new HashMap<String, HashMap<String, Double>>();
   public static final HashMap<String, Double>  thresholds = new HashMap<String, Double>(); //HashMap of thresholds for different VOIs
   public static HashMap<String, RawType> types = new HashMap<String, RawType>();
   public static ArrayList<String> givens = new ArrayList<String>();
   public static ArrayList<String> events = new ArrayList<String>();
   public static TreeSet<String> vars_of_interest = new TreeSet<String>();
   public static HashMap<String, ArrayList<Predicate<Object>>> bounds = new HashMap<String, ArrayList<Predicate<Object>>>(); //for vars with bounds e.g. x<3 x>=10 etc.
   public static HashMap<String, HashMap<String, Predicate<Object>>> bound_ids = new HashMap<String, HashMap<String, Predicate<Object>>>();
   public static HashMap<String, Double> deltas = new HashMap<String, Double>();
   public static HashMap<String, String[]> constraint_ids = new HashMap<String, String[]>(); //map id to array containing variable ids
}
