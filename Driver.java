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

   static String csv_file;
   static String config_file;
   static BasicEngine engine;
   static Object[][] csv_array;
   static ArrayList<String> givens = new ArrayList<String>();
   static ArrayList<String> events = new ArrayList<String>();
   static ArrayList<String> vars_of_interest = new ArrayList<String>();
   static HashMap<String, RawType> types = null;
   static HashMap<String, HashMap<String, Double>> priors = null;
   
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
            priors = new HashMap<String, HashMap<String, Double>>();
         } else if (s.equals("-h")){
            print_help();
            System.exit(0);
         }
      }
      try{
         csv_array = parse_csv_file(csv_file, get_csv_dimensions(csv_file));
         if(types != null){
            parse_typed_config_file(config_file);
         } else if (priors != null) {
            parse_bayesian_config_file(config_file);
            //out.println("priors: " + priors);
         } else {
            parse_config_file(config_file);
         }
      } catch(IOException e){
         e.printStackTrace();
      }
      build_inference_engine(csv_array, givens, events, priors);
   } // end main
   
   
   public static void build_inference_engine(Object[][] csv_array,
                           ArrayList<String> givens, ArrayList<String> events, HashMap<String, HashMap<String, Double>> priors){
      if(types != null){
         engine = new TypedEngine(csv_array, givens, events, types);
      } else if (priors != null) {
         //out.println("priors: " + priors);
         engine = new BayesianEngine(csv_array, givens, events, priors);
      }else {
         engine = new BasicEngine(csv_array, givens, events);
      }
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
            if(types.get(splitLine[0]) == null) types.put(splitLine[0], RawType.valueOf(splitLine[1]));
         } else if(e && thisLine != "\n") {
            String[] splitLine = thisLine.split(",");
            events.add(splitLine[0]);
            if(types.get(splitLine[0]) == null) types.put(splitLine[0], RawType.valueOf(splitLine[1]));
         } else {
            vars_of_interest.add(thisLine);
         }
         //System.out.println(StringEscapeUtils.escapeJava(thisLine));
         /*Set<String> keys = types.keySet();
         for(String str : keys){
            out.format("%s : %s%n", str, types.get(str));
         }*/
      }
      fis.close();
      dis.close();
      System.out.format("GIVENS:%s%n", givens);
      System.out.format("EVENTS:%s%n", events);
   }
   
   
   public static void parse_bayesian_config_file(String config_file) throws IOException {
      FileInputStream fis = new FileInputStream(config_file);
      DataInputStream dis = new DataInputStream(fis);
      String thisLine;
      // rows x columns
      int row_count = 0;
      boolean g = false;
      boolean e = false;
      //HashMap<String, Double> val = new HashMap<String, Double>();
      while ((thisLine = dis.readLine()) != null){
         if(thisLine.contains("GIVENS")){
            g = true; e = false; continue;
         } else if(thisLine.contains("EVENTS")) {
            e = true; g = false; continue;
         }
         if (g && thisLine != "\n") {
            String[] splitLine = thisLine.split(",");
            givens.add(splitLine[0]);
            HashMap<String, Double>  val = new HashMap<String, Double>();
            for(int i = 1; i < splitLine.length; i++){
               String[] distSplit = splitLine[i].split("=");
               //out.format("key:%s value:%s%n", distSplit[0],distSplit[1]);
               val.put(distSplit[0],  new Double(distSplit[1]));
               //out.format("put %s into key %s%n", distSplit[1], distSplit[0]);
            }
            priors.put(splitLine[0], val);
            //out.format("put %s into key %s%n", val, splitLine[0]);
            //System.exit(0);
            /*if(types.get(splitLine[0]) == null) {
               types.put(splitLine[0], RawType.valueOf(splitLine[1]));
            }*/
         } else if(e && thisLine != "\n") {
            String[] splitLine = thisLine.split(",");
            events.add(splitLine[0]);
            HashMap<String, Double>  val = new HashMap<String, Double>();
            for(int i = 1; i < splitLine.length; i++){
               String[] distSplit = splitLine[i].split("=");
               //out.format("key:%s value:%s%n", distSplit[0],distSplit[1]);
               val.put(distSplit[0],  new Double(distSplit[1]));
               //out.format("put %s into key %s%n", distSplit[1], distSplit[0]);
            }
            priors.put(splitLine[0], val);
            /*if(types.get(splitLine[0]) == null) types.put(splitLine[0], RawType.valueOf(splitLine[1]));*/
         } else {
            vars_of_interest.add(thisLine);
         }
         //System.out.println(StringEscapeUtils.escapeJava(thisLine));

      }
      Set<String> keys = priors.keySet();
      for(String str : keys){
         if(BayesianEngine.debug) out.format("%s : %s%n", str, priors.get(str));
         HashMap<String, Double> blah = priors.get(str);
         Set<String> keys2 = blah.keySet();
         for (String str2 : keys2){
            out.format("%-20s : %f%n", str2, blah.get(str2));
         }
      }
      fis.close();
      dis.close();
      System.out.format("GIVENS:%s%n", givens);
      System.out.format("EVENTS:%s%n", events);      
   }
   
   public static Object[][] parse_csv_file(String csv_file, int[] dims) throws IOException {
      FileInputStream fis = new FileInputStream(csv_file);
      DataInputStream dis = new DataInputStream(fis);
      String thisLine;
      // rows x columns
      Object[][] csv_array = new Object [dims[0]][dims[1]];
      int row_count = 0;
      while ((thisLine = dis.readLine()) != null){
         String [] cols = thisLine.split(",");
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
   
   public static void print_help(){
      System.out.println("Provide args: "
                         +"\n\t.csv file of trace"
                         +"\n\t.config file with no empty lines containing GIVENS and EVENTS");
      System.out.println("\nDon't forget to include commons.lang3 .jar (in ./commons-lang3-3.9/* on Meriel's computer).");
   }
   
} // end Driver