//package inference_engine;

import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import org.w3c.dom.*;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import org.apache.commons.lang3.StringEscapeUtils;
import static java.lang.System.out;

/**
 * Parses command line args and constructs BasicEngine
 */
public class Driver {

   static String csv_file, config_file, priors_file;
   static BasicEngine engine;
   static Object[][] csv_array;
   static ArrayList<String> givens = new ArrayList<String>();
   static ArrayList<String> events = new ArrayList<String>();
   static ArrayList<String> vars_of_interest = new ArrayList<String>();
   static HashMap<String, RawType> types = null;
   static HashMap<String, HashMap<String, Double>> priors = null;
   static boolean debug = true;
   
   public static void main(String[] args) throws ClassNotFoundException {
      Class ss_cls = TypeConverter.get_type_class(RawType.STRING);
      Class<Double> d_cls = TypeConverter.get_type_class(RawType.DOUBLE);
      Object d = TypeConverter.instantiate_type(RawType.DOUBLE, "10");
      Object ss = TypeConverter.instantiate_type(RawType.STRING, "stringvalue");
      for(String s : args){
         if(s.contains(".csv")){
            csv_file = s;
         } else if (s.contains(".config")) {
            config_file = s;
         } else if (s.contains(".typedconfig")) {
            config_file = s;
            types = new HashMap<String, RawType>();
         } else if (s.contains(".bayesianconfig")) {
            config_file = s;
            types = new HashMap<String, RawType>();
         } else if (s.contains(".priors")) {
            out.format("%s.contains(.priors)%n",s,s.contains(".priors"));
            priors_file = s;
            priors = new HashMap<String, HashMap<String, Double>>();
         } else if (s.equals("-h")){
            print_help();
            System.exit(0);
         }
      }
      try{
         csv_array = parse_csv_file(csv_file, get_csv_dimensions(csv_file));
         out.println("Types null:"+(types == null));
         out.println("Priors null:"+(priors == null));
         out.println("config file:"+config_file);
         if(types != null){ 
            parse_typed_config_file(config_file);
         } else if (priors != null) {
            parse_priors_file(priors_file);
            //out.println("priors: " + priors);
         } else {
            parse_config_file(config_file);
         }
      } catch(IOException e){
         e.printStackTrace();
      }
      build_inference_engine(csv_array, givens, events, priors);
      run_inference_engine();
   } // end main
   
   
   public static void build_inference_engine(Object[][] csv_array,
                           ArrayList<String> givens, ArrayList<String> events, HashMap<String, HashMap<String, Double>> priors){
      if(config_file.contains(".bayesianconfig") && types != null){
         //print_types();
         //pass in null priors --> build uniform dist in preprocess_trace()
         out.format("build_inference_engine: priors == null? %s%n", (priors == null));
         engine = new TypedBayesianEngine(csv_array, givens, events, priors, types);
      } else if (types != null) {
         engine = new TypedEngine(csv_array, givens, events, types);
      } else if (priors != null) {
         engine = new BayesianEngine(csv_array, givens, events, priors);
      } else {
         engine = new BasicEngine(csv_array, givens, events);
      }
   }
   
   
   public static void run_inference_engine(){
      engine.loop_through_trace();
   }
   
   
   public static void parse_config_file(String config_file) throws IOException {
      FileInputStream fis = new FileInputStream(config_file);
      DataInputStream dis = new DataInputStream(fis);
      String thisLine;
      // rows x columns
      int row_count = 0;
      boolean g = false;
      boolean e = false;
      while ((thisLine = dis.readLine()) != null){
         if(thisLine.contains("GIVENS")){
            //System.out.println("parsing givens");
            g = true; e = false; continue;
         } else if(thisLine.contains("EVENTS")) {
            //System.out.println("parsing events");
            e = true; g = false; continue;
         }
         if (g && thisLine != "\n") {
            givens.add(thisLine);
         } else if(e && thisLine != "\n") {
            events.add(thisLine);
         } else {
            vars_of_interest.add(thisLine);
         }
         System.out.println(StringEscapeUtils.escapeJava(thisLine));
      }
      fis.close();
      dis.close();
      System.out.println("GIVENS:"+givens);
      System.out.println("EVENTS:"+events+"\n");
   }
   
   
   public static void parse_typed_config_file(String config_file) throws IOException {
      FileInputStream fis = new FileInputStream(config_file);
      DataInputStream dis = new DataInputStream(fis);
      String thisLine;
      // rows x columns
      int row_count = 0;
      boolean g = false;
      boolean e = false;
      while ((thisLine = dis.readLine()) != null){
         if(thisLine.contains("GIVENS")){
            g = true; e = false; continue;
         } else if(thisLine.contains("EVENTS")) {
            e = true; g = false; continue;
         }
         if (g && thisLine != "\n") {
            String[] splitLine = thisLine.split(",");
            givens.add(splitLine[0]);
            //if(TypedBayesianEngine.debug) out.format("Added %s to givens%n",splitLine[0]);
            if(types.get(splitLine[0]) == null) types.put(splitLine[0], RawType.valueOf(splitLine[1]));
            //out.println("splitLine[splitLine.length-1]:"+splitLine[splitLine.length-1]);
            if(splitLine[splitLine.length-1].contains("threshold")){
               double threshold = Double.parseDouble(splitLine[splitLine.length-1].split("=")[1]);
               //out.format("Got %s threshold %f from file%n", splitLine[0], threshold);
               Global.thresholds.put(splitLine[0], threshold);
            }
         } else if(e && thisLine != "\n") {
            String[] splitLine = thisLine.split(",");
            events.add(splitLine[0]);
            //if(TypedBayesianEngine.debug) out.format("Added %s to events%n",splitLine[0]);
            if(types.get(splitLine[0]) == null) { types.put(splitLine[0], RawType.valueOf(splitLine[1]));}
            //out.println("splitLine[splitLine.length-1]:"+splitLine[splitLine.length-1]);
            if(splitLine[splitLine.length-1].contains("threshold")){
               double threshold = Double.parseDouble(splitLine[splitLine.length-1].split("=")[1]);
               //out.format("Got %s threshold %f from file%n", splitLine[0], threshold);
               Global.thresholds.put(splitLine[0], threshold);
            }
         } else {
            vars_of_interest.add(thisLine);
         }
         //System.out.println(StringEscapeUtils.escapeJava(thisLine));
      }
      Global.types = types;
      fis.close();
      dis.close();
      if(debug){
         out.format("GIVENS:%s%n", givens);
         out.format("EVENTS:%s%n", events);
         print_thresholds();
         print_types();
      }
      out.println("Finished parse_typed_config_file()");
      //System.exit(0);
   }
   
   public static void parse_priors_file(String config_file) throws IOException {
      FileInputStream fis = new FileInputStream(config_file);
      DataInputStream dis = new DataInputStream(fis);
      String thisLine;
      // rows x columns
      int row_count = 0;
      //HashMap<String, Double> val = new HashMap<String, Double>();
      while ((thisLine = dis.readLine()) != null){
         if (thisLine != "\n") {
            String[] splitLine = thisLine.split(",");
            HashMap<String, Double>  val = new HashMap<String, Double>();
            if(BayesianEngine.debug) print_types();
            for(int i = 1; i < splitLine.length; i++){
               String[] distSplit = splitLine[i].split("=");
               //out.format("key:%s value:%s%n", distSplit[0],distSplit[1]);
               if(types == null){
                  val.put(distSplit[0],  new Double(distSplit[1]));
               } else {
                  try{
                     RawType raw_type = types.get(splitLine[0]);
                     //out.println("types.get("+splitLine[0]+")"+raw_type);
                     switch(raw_type){
                        case DOUBLE:
                           Double val_double = new Double(distSplit[0]);
                           val.put(val_double.toString(),  new Double(distSplit[1]));
                           break;
                        case INT:
                           val.put(distSplit[0],  new Double(distSplit[1]));
                           break;
                        case STRING:
                           val.put(distSplit[0],  new Double(distSplit[1]));
                           break;
                        case INTEXP:
                           val.put(distSplit[0],  new Double(distSplit[1]));
                           break;
                        case DOUBLEEXP:
                           val.put(distSplit[0],  new Double(distSplit[1]));
                           break;
                     }
                  } catch(NullPointerException ex){}
               }
               //out.format("put %s into key %s%n", distSplit[1], distSplit[0]);
            }
            priors.put(splitLine[0], val);
            //out.format("parse_priors_file: put %s into key %s%n", val, splitLine[0]);
            //System.exit(0);
            /*if(types.get(splitLine[0]) == null) {
               types.put(splitLine[0], RawType.valueOf(splitLine[1]));
            }*/
         } 
         //System.out.println(StringEscapeUtils.escapeJava(thisLine));
      }
      print_priors();
      Global.priors = priors;
      fis.close();
      dis.close();    
   }
   
   public static Object[][] parse_csv_file(String csv_file, int[] dims) throws IOException {
      FileInputStream fis = new FileInputStream(csv_file);
      DataInputStream dis = new DataInputStream(fis);
      String thisLine;
      // rows x columns
      Object[][] csv_array = new Object [dims[0]][dims[1]];
      int row_count = 0;
      while ((thisLine = dis.readLine()) != null){
         String[] cols = thisLine.split(",");
         csv_array[row_count]= cols;
         row_count++;
      }
      fis.close();
      dis.close();
      return csv_array;
   }
   
   public static int[] get_csv_dimensions(String csv_file) throws IOException {
      FileReader fis = new FileReader(csv_file);
      BufferedReader dis = new BufferedReader(fis);
      String thisLine;
      // rows x columns
      int[] dims = {0, 0};
      while((thisLine = dis.readLine()) != null){
         dims[0] += 1;
         dims[1] = (thisLine.split(",").length);
      }
      return dims;
   }
   
   public static String print_priors(){
      String priors_string = "";
      Set<String> keys = priors.keySet();
      out.format("%nPRIORS:%n");
      for(String str : keys){
         out.format("%s : %s%n", str, priors.get(str));
         priors_string += String.format("%s : %s%n", str, priors.get(str));
         HashMap<String, Double> blah = priors.get(str);
         Set<String> keys2 = blah.keySet();
         /*for (String str2 : keys2){
            out.format("%-20s : %f%n", str2, blah.get(str2));
            priors_string += String.format("%-20s : %f%n", str2, blah.get(str2));
         }*/
      }
      return priors_string;
   }

   public static String print_priors(HashMap<String, HashMap<String, Double>> priors){
      String priors_string = "";
      Set<String> keys = priors.keySet();
      out.format("%nPRIORS:%n");
      for(String str : keys){
         out.format("%s : %s%n", str, priors.get(str));
         priors_string += String.format("%s : %s%n", str, priors.get(str));
         HashMap<String, Double> blah = priors.get(str);
         Set<String> keys2 = blah.keySet();
         for (String str2 : keys2){
            out.format("%-20s : %f%n", str2, blah.get(str2));
            priors_string += String.format("%-20s : %f%n", str2, blah.get(str2));
         }
      }
      return priors_string;
   }
   
   public static String print_thresholds(){
      String return_string = "";
      Set<String> keys = Global.thresholds.keySet();
      out.format("%nTHRESHOLDS:%n");
      for(String str : keys){
         out.format("%s : %s%n", str, Global.thresholds.get(str));
         return_string += String.format("%s : %s%n", str, Global.thresholds.get(str));
      }
      return return_string;
   }
   
   public static String priors_toString(){
      String priors_string = String.format("%nPRIORS:%n");
      Set<String> keys = priors.keySet();
      for(String str : keys){
         //if(BayesianEngine.debug) out.format("%s : %s%n", str, priors.get(str));
         priors_string += String.format("%s : %s%n", str, priors.get(str));
         HashMap<String, Double> blah = priors.get(str);
         Set<String> keys2 = blah.keySet();
         for (String str2 : keys2){
            //out.format("%-20s : %f%n", str2, blah.get(str2));
            priors_string += String.format("%-20s : %f%n", str2, blah.get(str2));
         }
      }
      return priors_string;
   }
   
   public static String print_types(){
      String types_string = "";
      Set<String> keys = types.keySet();
      out.format("%nTYPES:%n");
      for(String str : keys){
         out.format("%-20s : %s%n", str, types.get(str));
         types_string += String.format("%-20s : %s%n", str, types.get(str));
      }
      return types_string;
   }
   
   public static String types_toString(){
      String types_string = String.format("%nTYPES:%n");
      Set<String> keys = types.keySet();
      for(String str : keys){
         types_string += String.format("%-20s : %s%n", str, types.get(str));
         //types_string += String.format("%s : %s%n", str, types.get(str));
      }
      return types_string;
   }
   
   public static void print_help(){
      System.out.println("Provide args: "
                         +"\n\t.csv file of trace"
                         +"\n\t.config file with no empty lines containing GIVENS and EVENTS");
      System.out.println("\nDon't forget to include commons.lang3 .jar (in ./commons-lang3-3.9/* on Meriel's computer).");
   }
   
} // end Driver