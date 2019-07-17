import java.io.FileInputStream;
import java.io.DataInputStream;
import java.io.IOException;
import java.io.BufferedReader;
import java.io.FileReader;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Set;
import java.util.Arrays;
import org.apache.commons.lang3.StringUtils;
import static java.lang.System.out;
import java.io.FileWriter;


public class Csv_Counter{

   static String[][] csv_array;
   static HashMap<String, HashMap<String,Double>> count_map = new HashMap<String, HashMap<String, Double>>();

   public static void main(String[] args) throws IOException{
      out.println(Arrays.toString(args));
      String csv_file = args[0]; 
      try{
            out.println("parse_csv_file()");
            csv_array = parse_csv_file(csv_file, get_csv_dimensions(csv_file));
         } catch(IOException e){
            e.printStackTrace();
         }
      //ArrayList<String> columns = new ArrayList<String>(csv_array[0]);
      out.println("csv_array[0]:"+Arrays.toString(csv_array[0]));
      ArrayList<String> columns = new ArrayList<String>(Arrays.asList(csv_array[0]));
      int negative_count = 0;
      int positive_count = 0;
      int zero_count = 0;
      
      initialize_hashmap(columns);
      int[] dims = get_csv_dimensions(csv_file);
      for(int i = 1; i < dims[0]; i++){
         //out.print(i+" ");
         for(String var_name : columns){
            int index = columns.indexOf(var_name);
            if(var_name.equals("TrustDelta")){
               //out.println(csv_array[i][index]);
               if(Double.parseDouble(csv_array[i][index]) < 0){
                  try{
                     double d = count_map.get(var_name).get("negative_count");
                     count_map.get(var_name).put("negative_count", ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put("negative_count", 1.0);
                  }
                  negative_count++;
               } else if(Double.parseDouble(csv_array[i][index]) > 0){
                  try{
                     double d = count_map.get(var_name).get("positive_count");
                     count_map.get(var_name).put("positive_count", ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put("positive_count", 1.0);
                  }
                  negative_count++;
               } else {
                  try{
                     double d = count_map.get(var_name).get("zero_count");
                     count_map.get(var_name).put("zero_count", ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put("zero_count", 1.0);
                  }
                  zero_count++;
               }
            } else if(var_name.equals("Speed_Machine")){
               //Speed_Machine<5,Speed_Machine>=5&&Speed_Machine<10,Speed_Machine>=10&&Speed_Machine<15,Speed_Machine>=15
               if(Double.parseDouble(csv_array[i][index]) < 5.0){
                  try{
                     double d = count_map.get(var_name).get("Speed_Machine<5");
                     count_map.get(var_name).put("Speed_Machine<5", ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put("Speed_Machine<5", 1.0);
                  }
               } else if(Double.parseDouble(csv_array[i][index]) >= 5.0 && Double.parseDouble(csv_array[i][index]) < 10.0){
                  try{
                     double d = count_map.get(var_name).get("Speed_Machine>=5&&Speed_Machine<10");
                     count_map.get(var_name).put("Speed_Machine>=5&&Speed_Machine<10", ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put("Speed_Machine>=5&&Speed_Machine<10", 1.0);
                  }
               } else if (Double.parseDouble(csv_array[i][index]) >= 10.0 && Double.parseDouble(csv_array[i][index]) < 15.0){
                  try{
                     double d = count_map.get(var_name).get("Speed_Machine>=10&&Speed_Machine<15");
                     count_map.get(var_name).put("Speed_Machine>=10&&Speed_Machine<15", ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put("Speed_Machine>=10&&Speed_Machine<15", 1.0);
                  }
               } else {
                  try{
                     double d = count_map.get(var_name).get("Speed_Machine>=15");
                     count_map.get(var_name).put("Speed_Machine>=15", ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put("Speed_Machine>=15", 1.0);
                  }
               }
            } else if(var_name.equals("CurrentWheel_MachineDelta")){
               //CurrentWheel_Machine,DOUBLEDELTA,delta:=10,CurrentWheel_Machine<2&&CurrentWheel_Machine>-2,CurrentWheel_Machine>=2||CurrentWheel_Machine<=-2
               //CurrentWheel_Machine,DOUBLEDELTA,delta:=10,CurrentWheel_Machine<2.5&&CurrentWheel_Machine>-2.5,CurrentWheel_Machine>=2.5||CurrentWheel_Machine<=-2.5
               //CurrentWheel_Machine,DOUBLEDELTA,delta:=10,CurrentWheel_Machine<5&&CurrentWheel_Machine>-5,CurrentWheel_Machine>=5||CurrentWheel_Machine<=-5
               //CurrentWheel_Machine<15&&CurrentWheel_Machine>-15,CurrentWheel_Machine>=15||CurrentWheel_Machine<=-15
               //CurrentWheel_Machine<20&&CurrentWheel_Machine>-20,CurrentWheel_Machine>=20||CurrentWheel_Machine<=-20
               //CurrentWheel_Machine<25&&CurrentWheel_Machine>-25","CurrentWheel_Machine>=25||CurrentWheel_Machine<=-25"
               double dd = Double.parseDouble(csv_array[i][index]);
               String key1 = "CurrentWheel_Machine<30&&CurrentWheel_Machine>-30";
               String key2 = "CurrentWheel_Machine>=30||CurrentWheel_Machine<=-30";
               double threshold = 30.0; //rate of change
               if(dd < threshold && dd > -threshold){
                  try{
                     double d = count_map.get(var_name).get(key1);
                     count_map.get(var_name).put(key1, ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put(key1, 1.0);
                  }
               } else if(dd >= threshold || dd <= -threshold){
                  try{
                     double d = count_map.get(var_name).get(key2);
                     count_map.get(var_name).put(key2, ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put(key2, 1.0);
                  }
               }
            } else if (var_name.equals("PupilChange_Human")) {
               double dd = Double.parseDouble(csv_array[i][index]);
               String key1 = "PupilChange_Human>0";
               String key2 = "PupilChange_Human<0";
               String key3 = "PupilChange_Human==0";
               double th1 = 0;
               double th2 = 1;
               if(dd > th1){
                  try{
                     double d = count_map.get(var_name).get(key1);
                     count_map.get(var_name).put(key1, ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put(key1, 1.0);
                  }
               } else if(dd < th1){
                  try{
                     double d = count_map.get(var_name).get(key2);
                     count_map.get(var_name).put(key2, ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put(key2, 1.0);
                  }
               } else {
                  try{
                     double d = count_map.get(var_name).get(key3);
                     count_map.get(var_name).put(key3, ++d);
                  } catch(NullPointerException ex){
                     count_map.get(var_name).put(key3, 1.0);
                  }
               }
               
            } else {
               try{
                  String[] temp_row = csv_array[i];
                  //out.println("Getting index "+index+" from row "+i+":"+Arrays.toString(temp_row));
                  String str = temp_row[index];
                  double d = count_map.get(var_name).get(str);
                  count_map.get(var_name).put(csv_array[i][index], ++d);
               } catch(NullPointerException ex){
                  count_map.get(var_name).put(csv_array[i][index], 1.0);
               } catch (ArrayIndexOutOfBoundsException ex){
                  ex.printStackTrace();
                  out.println(ex.getMessage());
                  out.println("i: "+i);
                  out.println("index: "+index);
                  //System.exit(0);
               }
            }
         }
      }
      out.format("positive_count: %s%nnegative_count: %s%nzero_count: %s%n", positive_count, negative_count, zero_count);
      out.println(count_map);
      print_count_map();
      print_percentages(dims[0]-1);
      write_to_file(arg[0].replace(".csv","_count_map.txt"),count_map_toString());
   }
   
   public static void initialize_hashmap(ArrayList<String> columns){
      for (String s : columns){
         count_map.put(s, new HashMap<String,Double>());
      }
   }
   
   public static void print_count_map(){
      out.println("COUNT MAP:");
      Set<String> keys1 = count_map.keySet();
      for(String key1 : keys1){
         Set<String> keys2 = count_map.get(key1).keySet();
         for (String key2 : keys2){
            out.println(key1+":"+key2+":=" +count_map.get(key1).get(key2));
         }
      }
   }
   
   public static void write_to_file(String filename, String contents){
      try{
         FileWriter fw = new FileWriter(filename);
         fw.write(contents);
         fw.close();
      } catch(Exception e){
         e.printStackTrace();
      }
      out.format("Counts written to %s%n",filename);
   }
   
   public static String count_map_toString(){
      String str = "COUNT MAP:\n";
      Set<String> keys1 = count_map.keySet();
      for(String key1 : keys1){
         Set<String> keys2 = count_map.get(key1).keySet();
         for (String key2 : keys2){
            str += key1+":"+key2+":=" +count_map.get(key1).get(key2)+"\n";
         }
      }
      return str;
   }
   
   public static void print_percentages(int i){
      out.println("\n\n\n\n\n\n\n\n\nPERCENTAGES:");
      Set<String> keys1 = count_map.keySet();
      for(String key1 : keys1){
         if(!key1.contains("GSR") || !key1.contains("Manual")  || !key1.contains("Auto")  || !key1.contains("Pupil") ){
            Set<String> keys2 = count_map.get(key1).keySet();
            for (String key2 : keys2){
               out.format("%s:%s:=%.3f%n",key1,key2,count_map.get(key1).get(key2)/i);
               //out.println(key1+":"+key2+"=" +count_map.get(key1).get(key2));
            }
         }
      }
   }
   
   
   public static int[] get_csv_dimensions(String csv_file) throws IOException {
      FileReader fis = new FileReader(csv_file);
      BufferedReader dis = new BufferedReader(fis);
      String thisLine;
      // rows x columns
      int[] dims = {0, 0};
      while((thisLine = dis.readLine()) != null){
         dims[0] += 1;
         dims[1] = StringUtils.splitPreserveAllTokens(thisLine, ",");
      }
      return dims;
   }
   
   
   public static String[][] parse_csv_file(String csv_file, int[] dims) throws IOException {
      FileInputStream fis = new FileInputStream(csv_file);
      DataInputStream dis = new DataInputStream(fis);
      String thisLine;
      // rows x columns
      String[][] csv_array = new String [dims[0]][dims[1]];
      int row_count = 0;
      while ((thisLine = dis.readLine()) != null){
         String[] cols = new String[]{};
         try{
            cols = StringUtils.splitPreserveAllTokens(thisLine, ",");
         } catch(OutOfMemoryError ex){
            cols = thisLine.split(",");
         }
         
         csv_array[row_count]= cols;
         row_count++;
      }
      fis.close();
      dis.close();
      return csv_array;
   }
}