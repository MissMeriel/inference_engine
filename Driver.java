//package inference_engine;

import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import org.w3c.dom.*;
import java.util.ArrayList;
import org.apache.commons.lang3.StringEscapeUtils;

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
   
   public static void main(String[] args) throws ClassNotFoundException {
      //Class<?> cls = Class.forName("Integer");
      for(String s : args){
         if(s.contains(".csv")){
            csv_file = s;
            System.out.println("Parsed csv file");
            //break;
         } else if (s.contains(".config")) {
            config_file = s;
            System.out.println("Parsed config file");
         } else if (s.equals("-h")){
            print_help();
            System.exit(0);
         }
      }
      System.out.println(csv_file);
      try{
         int[] dims = get_csv_dimensions(csv_file);
         System.out.println(dims[0]+" "+dims[1]);
         csv_array = parse_csv_file(csv_file, dims);
         parse_config_file(config_file);
      }
      catch(IOException e){
         e.printStackTrace();
      }
      build_inference_engine(csv_array, givens, events);
   } // end main
   
   
   public static void build_inference_engine(Object[][] csv_array,
                           ArrayList<String> givens, ArrayList<String> events){
      engine = new BasicEngine(csv_array, givens, events);
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
            System.out.println("parsing givens");
            g = true; e = false; continue;
         } else if(thisLine.contains("EVENTS")) {
            System.out.println("parsing events");
            e = true; g = false; continue;
         }
         
         if (g && thisLine != "\n") {
            givens.add(thisLine);
         } else if(e && thisLine != "\n"){
            events.add(thisLine);
         } else {
            vars_of_interest.add(thisLine);
         }
         //System.out.println(StringEscapeUtils.escapeJava(thisLine));
      }
      fis.close();
      dis.close();
      System.out.println("GIVENS:"+givens);
      System.out.println("EVENTS:"+events);
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