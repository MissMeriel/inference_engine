import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.TreeSet;
import java.util.Arrays;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.io.FileWriter;
import static java.lang.System.out;
import java.util.Collections;

class Probability_To_Csv{

   //static String[][] csv_array;
   static HashMap<String, HashMap<String,String>> header_map = new HashMap<String, HashMap<String, String>>();
   static Pattern subject_pattern = Pattern.compile("[S][0-9]+[\\s]*");
   static Pattern bag_pattern = Pattern.compile("[sweep_for_target_interpolated.csv][\\s]*");
   static Pattern whitespace_pattern = Pattern.compile("^\\S+");
   static Pattern double_pattern = Pattern.compile("[0-9]+.[0-9]+");
   //static Pattern whitespace_pattern = Pattern.compile("\p{IsWhite_Space}+");
   static ArrayList<String> headers = new ArrayList<String>();
   static boolean is_bag = false;
   static boolean debug = false;
   
   public static void main(String[] args) throws IOException{
      out.println(Arrays.toString(args));
      String filename = args[0];
      try{
         if(args[1].equals("bag")){
            is_bag = true;
         }
      } catch(Exception e){}
      
      try{
         parse_file(filename, get_length(filename));
      } catch(IOException e){
         e.printStackTrace();
      }
      //ArrayList<String> columns = new ArrayList<String>(csv_array[0]);
      //out.println(csv_array[0]);
      //out.println(Arrays.toString(csv_array[0]));
      print_header_map();
      String csv_string = map_to_csv();
      String csv_filename = filename.replace("txt","csv");
      out.println(csv_filename);
      write_csv_to_file(csv_filename, csv_string);
   }
   
   public static void write_csv_to_file(String filename, String contents){
      try{
         FileWriter fw = new FileWriter(filename);
         fw.write(contents);
         fw.close();
      } catch(Exception e){
         e.printStackTrace();
      }
      out.format("Csv written to %s%n",filename);
   }
   
   public static void parse_file(String csv_file, int length) throws IOException {
      FileInputStream fis = new FileInputStream(csv_file);
      DataInputStream dis = new DataInputStream(fis);
      String thisLine;
      // rows x columns
      String[] line_array = new String [length];
      int row_count = 0;
      String current_subject = "";
      if(is_bag){
         subject_pattern = Pattern.compile("./sweep/sweep_for_target_[0-9-]*interpolated.csv[\\s]*");
      }
      while ((thisLine = dis.readLine()) != null){
         Matcher subject_matcher = subject_pattern.matcher(thisLine);
         Matcher whitespace_matcher = whitespace_pattern.matcher(thisLine);
         
         if(subject_matcher.matches()){
            if(debug) out.println("Subject: "+thisLine);
            current_subject = thisLine;
            //continue;
         } else if(whitespace_matcher.find()){
            if(debug) out.format("Parsing %s line %s%n", current_subject, thisLine);
            parse_line(current_subject, thisLine);
            out.println();
         }
         row_count++;
      }
      fis.close();
      dis.close();
      //return line_array;
   }
   
   public static void parse_line(String subject, String line) {
      String[] cols = line.split("\\) ="); //length==2
      HashMap<String,String> subject_map = new HashMap<String,String>();
      
      try{
         HashMap<String,String> temp = header_map.get(subject);
         subject_map = temp;
         if(temp == null){
            subject_map = new HashMap<String,String>();
            header_map.put(subject, subject_map);
         }
      } catch(NullPointerException ex) {
         ex.printStackTrace();
         subject_map = new HashMap<String,String>();
         header_map.put(subject, subject_map);
      }
      String header = String.format("%s)",cols[0]);
      String[] temp = cols[1].split(" = ");
      String probability = temp[temp.length-1];
      /*out.format("subject_map.put(%s, %s)%n",header,probability);
      out.format("subject_map == null? %s%n",(subject_map == null));*/
      if(!headers.contains(header)){
         headers.add(header);
      }
      subject_map.put(header, probability);
      header_map.put(subject, subject_map);
   }
   
   public static void print_header_map(){
      if(debug) out.println("\n\n\nHEADER MAP:");
      Set<String> keys1 = header_map.keySet();
      TreeSet<String> treeset1 = new TreeSet(keys1);
      for(String key1 : treeset1){
         Set<String> keys2 = header_map.get(key1).keySet();
         TreeSet<String> treeset2 = new TreeSet(keys2);
         for (String key2 : treeset2){
         
            out.println(key1+":"+key2+":=" +header_map.get(key1).get(key2));
         }
      }
      out.println();
      out.println(headers);
   }
   
   public static String map_to_csv(){
      out.println("\n\n\nmap_to_csv()");
      Collections.sort(headers);
      String headers_str = headers.toString();
      String csv_string = "Subject,"+headers_str.substring(1, headers_str.length()-1);
      Set<String> keys1 = header_map.keySet();
      TreeSet<String> treeset1 = new TreeSet(keys1);
      for(String key1 : treeset1){
         Set<String> keys2 = header_map.get(key1).keySet();
         TreeSet<String> treeset2 = new TreeSet(keys2);
         csv_string += String.format("%n%s",key1);
         //Iterator<String> iter2 = treeset2.iterator();
         for(String header : headers){
            String temp = header_map.get(key1).get(header);
            if(temp == null){
               if(debug) out.format("header_map.get(%s).get(%s) == null%n",key1,header);
               csv_string += ",0.0";
            } else {
               csv_string += "," +temp;
            }
            
         }
      }
      if(debug){
         out.println("\n\n\n\n");
         out.println(csv_string);
      }
      return csv_string;
   }

   public static int get_length(String file) throws IOException {
      FileReader fis = new FileReader(file);
      BufferedReader dis = new BufferedReader(fis);
      String thisLine;
      // rows x columns
      int len = 0;
      while((thisLine = dis.readLine()) != null){
         len += 1;
      }
      return len;
   }

}