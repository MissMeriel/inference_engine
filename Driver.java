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
   
   public static void main(String[] args) throws ClassNotFoundException {
      //Class<?> cls = Class.forName("Integer");
      Class ss_cls = TypeConverter.get_type_class(RawType.STRING);
      Class<Double> d_cls = TypeConverter.get_type_class(RawType.DOUBLE);
      Object d = TypeConverter.instantiate_type(RawType.DOUBLE, "10");
      Object ss = TypeConverter.instantiate_type(RawType.STRING, "stringvalue");
      /*out.format("New double: %s (%s)%n", d, d.getClass());
      out.format("New string: %s%n", ss);
      out.println(d_cls);
      out.println(d_cls.cast(d)+10.0);*/
      //cls = boolean.class;
      //System.exit(0);
      for(String s : args){
         if(s.contains(".csv")){
            csv_file = s;
         } else if (s.contains(".config")) {
            config_file = s;
         } else if (s.contains(".typedconfig")) {
            config_file = s;
            types = new HashMap<String, RawType>();
         } else if (s.equals("-h")){
            print_help();
            System.exit(0);
         }
      }
      try{
         csv_array = parse_csv_file(csv_file, get_csv_dimensions(csv_file));
         if(types != null){
            parse_typed_config_file(config_file);
         } else {
            parse_config_file(config_file);
         }
      }
      catch(IOException e){
         e.printStackTrace();
      }
      build_inference_engine(csv_array, givens, events);
   } // end main
   
   
   public static void build_inference_engine(Object[][] csv_array,
                           ArrayList<String> givens, ArrayList<String> events){
      if(types != null){
         engine = new TypedEngine(csv_array, givens, events, types);
         engine.loop_through_trace();
      } else {
         engine = new BasicEngine(csv_array, givens, events);
         engine.loop_through_trace();
      }
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
   
   
   public static Object[][] parse_csv_file(String csv_file, int[] dims) throws IOException {
      FileInputStream fis = new FileInputStream(csv_file);
      DataInputStream dis = new DataInputStream(fis);
      String thisLine;
      // rows x columns
      Object[][] csv_array = new Object [dims[0]][dims[1]];
      //System.out.println(dis.readLine());
      int row_count = 0;
      while ((thisLine = dis.readLine()) != null){
         //System.out.println(thisLine);
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