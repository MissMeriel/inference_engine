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
   static HashMap<String, String> prior_map = new HashMap<String, String>();
   static HashMap<String, Integer> observations_map = new HashMap<String, Integer>();
   static HashMap<String, Integer> A_count_map = new HashMap<String, Integer>();
   static Pattern subject_pattern = Pattern.compile("[S][0-9]+[\\s]*");
   static String bag_pattern_string = "[.]*/sweep_for_target_[0-9-]*interpolatedtest.csv[\\s]*";
   static Pattern nonwhitespace_pattern = Pattern.compile("^\\S+");
   static Pattern double_pattern = Pattern.compile("[0-9]+.[0-9]+");
   //static Pattern whitespace_pattern = Pattern.compile("\p{IsWhite_Space}+");
   static ArrayList<String> headers = new ArrayList<String>();
   static boolean is_bag = false;
   static boolean debug = false;
   static boolean horiz_output = false;
   
   public static void main(String[] args) throws IOException{
      String filename = args[0];
      try{
         if(args[1].equals("bag")  || args[2].equals("bag")){
            is_bag = true;
         }
      } catch(Exception e){}
      try{
         if(args[1].equals("horiz") || args[2].equals("horiz")){
            horiz_output = true;
         }
      } catch(Exception e){}
      if(horiz_output){
         if(is_bag){
            subject_pattern = Pattern.compile(bag_pattern_string);
         }
         ArrayList<String> trace_names = get_trace_names(filename, subject_pattern);
         TreeSet<String> inv_names = get_inv_names(filename, subject_pattern, nonwhitespace_pattern);
         try{
            parse_file(filename, get_length(filename));
         } catch(IOException e){
            e.printStackTrace();
         }
         String csv_string = map_to_csv_horiz2(trace_names, inv_names);
         String csv_filename = filename.replace(".txt","horiz.csv");
         write_csv_to_file(csv_filename, csv_string);
      } else {
         try{
            parse_file(filename, get_length(filename));
         } catch(IOException e){
            e.printStackTrace();
         }
         String csv_string = map_to_csv();
         String csv_filename = filename.replace("txt","csv");
         write_csv_to_file(csv_filename, csv_string);
      }
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
   
  
   public static ArrayList<String> get_trace_names(String csv_file, Pattern subject_pattern) throws IOException {
      FileInputStream fis = new FileInputStream(csv_file);
      DataInputStream dis = new DataInputStream(fis);
      ArrayList<String> trace_names = new ArrayList<String>();
      String thisLine = "";
      while((thisLine = dis.readLine()) != null){
         Matcher subject_matcher = subject_pattern.matcher(thisLine);
         if(subject_matcher.find()){
            trace_names.add(thisLine);
         } 
      }
      fis.close(); dis.close();
      return trace_names;
   }
   
   
   public static TreeSet<String> get_inv_names(String csv_file, Pattern subject_pattern, Pattern nonwhitespace_pattern) throws IOException {
      FileInputStream fis = new FileInputStream(csv_file);
      DataInputStream dis = new DataInputStream(fis);
      TreeSet<String> inv_names = new TreeSet<String>();
      String thisLine = "";
      while((thisLine = dis.readLine()) != null){
         Matcher whitespace_matcher = nonwhitespace_pattern.matcher(thisLine);
         Matcher subject_matcher = subject_pattern.matcher(thisLine);
         if(!subject_matcher.matches() && whitespace_matcher.find()){
            String[] split = thisLine.split(" = ");
            inv_names.add(split[0]);
         }
      }
      fis.close(); dis.close();
      return inv_names;
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
         subject_pattern = Pattern.compile(bag_pattern_string);
      }
      while ((thisLine = dis.readLine()) != null){
         Matcher subject_matcher = subject_pattern.matcher(thisLine);
         Matcher whitespace_matcher = nonwhitespace_pattern.matcher(thisLine);
         if(subject_matcher.find()){
            //out.println("Subject: "+thisLine);
            current_subject = thisLine;
         } else if(whitespace_matcher.find()){
            //out.format("Parsing %s line %s%n", current_subject, thisLine);
            parse_line(current_subject, thisLine);
         }
         row_count++;
      }
      fis.close();
      dis.close();
      //return line_array;
   }
   
   public static void parse_line(String subject, String line) {
      String[] cols = line.split("\\) = "); //length==2
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
      String probability = "";
      String prior = "";
      String A_count = "";
      int prior_index = 1;
      //parse numerics
      try{
         String[] temp = cols[1].split(" = ");
         probability = temp[1].split(" ")[0].trim();
         temp = temp[1].split("A_count=");
         A_count = temp[1].replace(")", "");
         try{
            int i = A_count_map.get(header);
            A_count_map.put(header, new Integer(A_count) + i);
         } catch(Exception e){
            A_count_map.put(header, new Integer(A_count));
         }
         
         if(probability.equals("NaN")){
            probability = "0.0";
         }
         prior = temp[prior_index];
         temp = cols[1].split(" * ");
         prior = temp[0];
      } catch(ArrayIndexOutOfBoundsException ex){
         /*ex.printStackTrace();
         out.println("subject:"+subject+", line:"+line);
         System.exit(0);*/
      }
      String[] temp = cols[1].split("\\(");
      //out.println(Arrays.toString(temp));
      String samplesize = temp[temp.length-2].replaceAll("\\) ", "");
      //out.format("samplesize=%s\n", samplesize);
      try{
         int i = observations_map.get(header);
         observations_map.put(header, new Integer(samplesize)+i);
      } catch(Exception ex){
         observations_map.put(header, new Integer(samplesize));
      }
      if(!headers.contains(header)){
         headers.add(header);
      }
      probability = probability.split("\\(")[0];
      subject_map.put(header, probability); ///need to further split
      prior_map.put(header, prior);
      /*out.format("%s%n",line);
      out.format("prior_map.put(%s,%s)%n",header,prior);*/
      header_map.put(subject, subject_map);
      /*out.format("subject=%s\n", subject);
      out.format("subject_map[probability]=%s\n", subject_map.get(header));
      out.format("header=%s\n\n\n", header);*/
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

   
   public static String map_to_csv_horiz2(ArrayList<String> trace_names, TreeSet<String> inv_names){
      String traces_str = trace_names.toString();
      String csv_string = "Invariant,Avg,Prior,Ratio,Avg Observations,Avg A Samples,"+traces_str.substring(1, traces_str.length()-1);
      int inv_index = 0; int avg_index = 1; int prior_index = 2; int ratio_index = 3;
      for(String inv : inv_names){
         csv_string += String.format("%n%s",inv);
         //Iterator<String> iter2 = treeset2.iterator();
         String temp_string = "";
         ArrayList<Double> probs = new ArrayList<Double>();
         for(String trace : trace_names){
            String temp = header_map.get(trace).get(inv);
            if(temp == null){
               if(debug) out.format("header_map.get(%s).get(%s) == null%n",inv,trace);
               temp_string += ",0.0";
               probs.add(0.0);
            } else {
               double dd = Double.parseDouble(temp);
               if(dd == Double.NaN){
                  dd = 0.0;
                  System.exit(0);
               }
               temp_string += "," +dd;
               probs.add(dd);
            }
         }
         // compute average probability
         double avg = 0.0;
         //out.println("probs: "+probs);
         for(Double d : probs){
            avg += d;
         }
         avg = avg / probs.size();
         // compute average # observations
         double avg_observations = ((double) observations_map.get(inv)) ;/// probs.size();
         //out.format("avg_observations=%.5f\n", avg_observations);
         //out.format("probs.size()=%d\n", probs.size());
         double ratio =0.0;
         try{
            double p = Double.parseDouble(prior_map.get(inv));
            ratio = avg / p;
         } catch(NullPointerException ex){
            ex.printStackTrace();
            out.format("NullPointer on header_map.get(%s).get(prior)=%s%n", inv, prior_map.get(inv));
         }
         // compute avg_A_samples
         double avg_A_samples = ((double) A_count_map.get(inv)) ;/// probs.size();
         //out.format("avg_A_samples=%.5f\n", avg_A_samples);
         //out.format("probs.size()=%d\n", probs.size());
         double A_samples_ratio =0.0;
         try{
            double p = Double.parseDouble(prior_map.get(inv));
            A_samples_ratio = avg / p;
         } catch(NullPointerException ex){
            ex.printStackTrace();
            out.format("NullPointer on header_map.get(%s).get(prior)=%s%n", inv, prior_map.get(inv));
         }
         //csv_string += String.format(",%.5f,%s,%.5f%s", avg, prior_map.get(inv), ratio, temp_string);
         csv_string += String.format(",%.5f,%s,%.5f,%.5f,%.5f%s", avg, prior_map.get(inv), ratio, avg_observations, A_samples_ratio, temp_string);
      }
      if(false){
         out.println("\n\n\n\n");
         out.println(csv_string);
      }
      return csv_string;
   }
  
     public static String map_to_csv_horiz(ArrayList<String> trace_names, TreeSet<String> inv_names){
      out.println("\n\n\nmap_to_csv_horiz()");
      //Collections.sort(inv_names);
      String traces_str = trace_names.toString();
      out.println("trace names: "+traces_str);
      String csv_string = "Invariant,Avg,Prior,Ratio,"+traces_str.substring(1, traces_str.length()-1);
      int inv_index = 0; int avg_index = 1; int prior_index = 2; int ratio_index = 3;
      for(String inv : inv_names){
         csv_string += String.format("%n%s",inv);
         //Iterator<String> iter2 = treeset2.iterator();
         String temp_string = "";
         ArrayList<Double> probs = new ArrayList<Double>();
         for(String trace : trace_names){
            String temp = header_map.get(trace).get(inv);
            if(temp == null){
               if(debug) out.format("header_map.get(%s).get(%s) == null%n",inv,trace);
               temp_string += ",0.0";
               probs.add(0.0);
            } else {
               double dd = Double.parseDouble(temp);
               if(dd == Double.NaN){
                  dd = 0.0;
                  System.exit(0);
               }
               temp_string += "," +dd;
               probs.add(dd);
            }
         }
         double avg = 0.0;
         out.println("probs: "+probs);
         for(Double d : probs){
            avg += d;
         }
         avg = avg / probs.size();
         double ratio =0.0;
         try{
            double p = Double.parseDouble(prior_map.get(inv));
            ratio = avg / p;
         } catch(NullPointerException ex){
            ex.printStackTrace();
            out.format("NullPointer on header_map.get(%s).get(prior)=%s%n", inv, prior_map.get(inv));
         }
         //csv_string += String.format(",%.5f,%s,%.5f%s", avg, prior_map.get(inv), ratio, temp_string);
         csv_string += String.format(",%.5f,%s,%.5f%s", avg, prior_map.get(inv), ratio, temp_string);
      }
      if(true){
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
