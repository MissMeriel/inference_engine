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


public class Csv_Counter2{

   static String[][] csv_array;
   static HashMap<String, HashMap<String,Double>> count_map = new HashMap<String, HashMap<String, Double>>();

   public static void main(String[] args) throws IOException{
      out.println(Arrays.toString(args));
      String csv_file = args[0]; 
      
      int[] dims = get_csv_dimensions(csv_file);
      
      FileInputStream fis = new FileInputStream(csv_file);
      DataInputStream dis = new DataInputStream(fis);
      String thisLine = dis.readLine();
      String[] temp_cols = StringUtils.splitPreserveAllTokens(thisLine, ",");
      ArrayList<String> columns = new ArrayList<String>(Arrays.asList(temp_cols));
      int negative_count = 0;
      int positive_count = 0;
      int zero_count = 0;
      out.format("%s rows, %s columns%n",dims[0],dims[1]);
      initialize_hashmap(columns);
      for(int i = 1; i < dims[0]; i++){
         out.println(i+" out of "+ dims[0]);
         if ((thisLine = dis.readLine()) != null){
            String[] cols = new String[]{};
            String[] row = StringUtils.splitPreserveAllTokens(thisLine, ",");
            for(String var_name : columns){
               try{
                  int index = columns.indexOf(var_name);
                  if(var_name.equals("TrustDelta")){
                     //out.println(row[index]);
                     if(Double.parseDouble(row[index]) < 0){
                        try{
                           double d = count_map.get(var_name).get("negative_count");
                           count_map.get(var_name).put("negative_count", ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put("negative_count", 1.0);
                        }
                        negative_count++;
                     } else if(Double.parseDouble(row[index]) > 0){
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
                     if(Double.parseDouble(row[index]) < 5.0){
                        try{
                           double d = count_map.get(var_name).get("Speed_Machine<5");
                           count_map.get(var_name).put("Speed_Machine<5", ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put("Speed_Machine<5", 1.0);
                        }
                     } else if(Double.parseDouble(row[index]) >= 5.0 && Double.parseDouble(row[index]) < 10.0){
                        try{
                           double d = count_map.get(var_name).get("Speed_Machine>=5&&Speed_Machine<10");
                           count_map.get(var_name).put("Speed_Machine>=5&&Speed_Machine<10", ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put("Speed_Machine>=5&&Speed_Machine<10", 1.0);
                        }
                     } else if (Double.parseDouble(row[index]) >= 10.0 && Double.parseDouble(row[index]) < 15.0){
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
                     
                  } else if (var_name.equals("Speed_Delta")){
                     if(Double.parseDouble(row[index]) > 0) {
                        try{
                           double d = count_map.get(var_name).get("Speed_Delta>0");
                           count_map.get(var_name).put("Speed_Delta>0", ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put("Speed_Delta>0", 1.0);
                        }
                     } else if(Double.parseDouble(row[index]) < 0){
                        try{
                           double d = count_map.get(var_name).get("Speed_Delta<0");
                           count_map.get(var_name).put("Speed_Delta<0", ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put("Speed_Delta<0", 1.0);
                        }
                     } else {
                        try{
                           double d = count_map.get(var_name).get("Speed_Delta==0");
                           count_map.get(var_name).put("Speed_Delta==0", ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put("Speed_Delta==0", 1.0);
                        }
                     }
                  } else if(var_name.equals("CurrentWheel_MachineDelta")){
                     //CurrentWheel_Machine,DOUBLEDELTA,delta:=10,CurrentWheel_Machine<2&&CurrentWheel_Machine>-2,CurrentWheel_Machine>=2||CurrentWheel_Machine<=-2
                     //CurrentWheel_Machine,DOUBLEDELTA,delta:=10,CurrentWheel_Machine<2.5&&CurrentWheel_Machine>-2.5,CurrentWheel_Machine>=2.5||CurrentWheel_Machine<=-2.5
                     //CurrentWheel_Machine,DOUBLEDELTA,delta:=10,CurrentWheel_Machine<5&&CurrentWheel_Machine>-5,CurrentWheel_Machine>=5||CurrentWheel_Machine<=-5
                     //CurrentWheel_Machine<15&&CurrentWheel_Machine>-15,CurrentWheel_Machine>=15||CurrentWheel_Machine<=-15
                     //CurrentWheel_Machine<20&&CurrentWheel_Machine>-20,CurrentWheel_Machine>=20||CurrentWheel_Machine<=-20
                     //CurrentWheel_Machine<25&&CurrentWheel_Machine>-25","CurrentWheel_Machine>=25||CurrentWheel_Machine<=-25"
                     double dd = Double.parseDouble(row[index]);
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
                     double dd = Double.parseDouble(row[index]);
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
                     
                  } else if (var_name.contains("velocity.linear_y")) {
                     double dd = 0;
                     try{
                        dd = round(Double.parseDouble(row[index]), 2);
                     } catch(java.lang.NumberFormatException ex){}
                     if(Math.abs(dd) < 0.25){
                        String key = "/velocity.linear_y<0.25&&velocity.linear_y>-0.25";
                        try {
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){
                           e.printStackTrace();
                           //System.exit(0);
                        }
                     } else if(Math.abs(dd) < 0.5) {
                        String key = "/velocity.linear_y>=0.25&&/velocity.linear_y<0.5||/velocity.linear_y<=-0.25&&/velocity.linear_y>-0.5";
                        try {
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){
                           e.printStackTrace();
                           //System.exit(0);
                        }
                     } else if(Math.abs(dd) < 0.75) {
                        String key = "/velocity.linear_y>=0.5&&/velocity.linear_y<0.75||/velocity.linear_y<=-0.5&&/velocity.linear_y>-0.75";
                        try {
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){
                           e.printStackTrace();
                           //System.exit(0);
                        }
                     } else if(Math.abs(dd) < 0.1) {
                        String key = "/velocity.linear_y>=0.75&&/velocity.linear_y<1||/velocity.linear_y<=-0.75&&/velocity.linear_y>-1";
                        try {
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){
                           e.printStackTrace();
                           //System.exit(0);
                        }
                     } else {
                        String key = "/velocity.linear_y>1||/velocity.linear_y<-1";
                        try {
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){
                           e.printStackTrace();
                           //System.exit(0);
                        }
                     }
                  } else if (var_name.contains("velocity")) {
                     double dd = 0;
                     try{
                        dd = round(Double.parseDouble(row[index]), 2);
                     } catch(java.lang.NumberFormatException ex){}
                     try{
                           double d = count_map.get(var_name).get(Double.toString(dd));
                           count_map.get(var_name).put(Double.toString(dd), ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(Double.toString(dd), 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){
                           e.printStackTrace();
                           //System.exit(0);
                        }
                        
                  } else if (var_name.contains("TELLO")) {
                     double dd = 0;
                     try{
                        dd = round(Double.parseDouble(row[index]), 1);
                     } catch(java.lang.NumberFormatException ex){}
                     try{
                           double d = count_map.get(var_name).get(Double.toString(dd));
                           count_map.get(var_name).put(Double.toString(dd), ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(Double.toString(dd), 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){
                           e.printStackTrace();
                           //System.exit(0);
                        }
                  
                  } else if (var_name.contains("/user_input.reaction_time_secs")) {
                     double dd = 0;
                     try{
                        dd = Double.parseDouble(row[index]);
                     } catch(java.lang.NumberFormatException ex){}
                     if(dd < 0.2){
                        String key = "/user_input.reaction_time_secs < 0.2";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     } else if(dd < 0.4){
                        String key = "0.2 <= /user_input.reaction_time_secs < 0.4";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     } else if(dd < 0.6){
                        String key = "0.4 <= /user_input.reaction_time_secs < 0.6";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     } else if(dd < 0.8){
                        String key = "0.6 <= /user_input.reaction_time_secs < 0.8";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     } else if(dd < 1){
                        String key = "0.8 <= /user_input.reaction_time_secs < 1";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){} 
                     } else if(dd < 1){
                        String key = "0.5 <= /user_input.reaction_time_secs < 1";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){} 
                     } else if(dd < 3){
                        String key = "1 <= /user_input.reaction_time_secs < 3";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}                     
                     } else if(dd < 6){
                        String key = "3 <= /user_input.reaction_time_secs < 6";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     } else if(dd < 9){
                        String key = "6 <= /user_input.reaction_time_secs < 9";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     } else if (dd < 12){
                        String key = "9 <= /user_input.reaction_time_secs < 12";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     } else if (dd < 15){
                        String key = "12 <= /user_input.reaction_time_secs < 15";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     } else {
                        String key = "/user_input.reaction_time_secs >= 15";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     }
                     
                  } else if (var_name.contains("/visp_auto_tracker/object_position")) {
                     double dd = 0;
                     try{
                        dd = round(Double.parseDouble(row[index]), 1);
                     } catch(java.lang.NumberFormatException ex){}
                     try{
                        double d = count_map.get(var_name).get(Double.toString(dd));
                        count_map.get(var_name).put(Double.toString(dd), ++d);
                     } catch(NullPointerException ex){
                        count_map.get(var_name).put(Double.toString(dd), 1.0);
                     } catch(ArrayIndexOutOfBoundsException e){
                        e.printStackTrace();
                        //System.exit(0);
                     }
                  
                  } else if (var_name.contains("/flight_data.battery_percentage")) {
                     double dd = 0;
                     try{
                        dd = Double.parseDouble(row[index]);
                     } catch(java.lang.NumberFormatException ex){}
                     if(dd < 20){
                        String key = "/flight_data.battery_percentage < 20";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     } else if (dd < 40){
                        String key = "20 <= /flight_data.battery_percentage < 40";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     } else if (dd < 60){
                        String key = "40 <= /flight_data.battery_percentage < 60";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                        
                     } else if(dd < 80){
                        String key = " 60 <= /flight_data.battery_percentage < 80";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     } else {
                        String key = "/flight_data.battery_percentage > 80";
                        try{
                           double d = count_map.get(var_name).get(key);
                           count_map.get(var_name).put(key, ++d);
                        } catch(NullPointerException ex){
                           count_map.get(var_name).put(key, 1.0);
                        } catch(ArrayIndexOutOfBoundsException e){}
                     }
                  
                  } else if (!var_name.contains("header")
                             && !var_name.contains("stamp")
                             && !var_name.contains("smart_video_exit_mode")
                             && !var_name.contains("power_state")
                             && !var_name.contains("throw_fly_timer")
                             && !var_name.contains("frame")){
                     //out.println("index of "+var_name+":"+index);
                     
                     HashMap<String, Double> temp_map = count_map.get(var_name);
                     try{
                        //out.println("Getting index "+index+" from row "+i+":"+Arrays.toString(row));
                        String str = row[index];
                        if(str.equals("")){continue;}
                        /*out.println(temp_map == null);
                        out.println(str == null);*/
                        double d = temp_map.get(str);
                        count_map.get(var_name).put(row[index], ++d);
                     } catch(NullPointerException ex){
                        //ex.printStackTrace();
                        try{
                           count_map.get(var_name).put(row[index], 1.0);
                        }catch(ArrayIndexOutOfBoundsException e){
                           e.printStackTrace();
                           //System.exit(0);
                        }
                     } catch (ArrayIndexOutOfBoundsException ex){
                        ex.printStackTrace();
                        out.println(ex.getMessage());
                        out.println("row index: "+i);
                        out.println("row length: "+row.length);
                        out.println("column name: "+var_name);
                        out.println("column index: "+index);
                        //System.exit(0);
                     }
                  }
               }catch(ArrayIndexOutOfBoundsException e){}
            }
         }
      }
      fis.close();
      dis.close();
      //out.format("positive_count: %s%nnegative_count: %s%nzero_count: %s%n", positive_count, negative_count, zero_count);
      //out.println(count_map);
      //print_count_map();
      //print_percentages(dims[0]-1);
      write_to_file(csv_file.replace(".csv","_count_map.txt"), (double) dims[0]);
   }
   
   public static double round(double val, double decimal_pts){
      double scale = Math.pow(10, decimal_pts);
      return Math.round(val * scale) / scale;
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
   
   
   public static void write_to_file(String filename, double d){
      try{
         FileWriter fw = new FileWriter(filename);
         Set<String> keys1 = count_map.keySet();
         for(String key1 : keys1){
            String priorfile_string = key1+",";
            Set<String> keys2 = count_map.get(key1).keySet();
            for (String key2 : keys2){
               if(!key1.contains("header") && !key1.contains("stamp")){
                  String contents = String.format("%s:%s:=%s --> %.5f%n",key1,key2,count_map.get(key1).get(key2),count_map.get(key1).get(key2) / d);
                  priorfile_string += String.format("%s:=%.5f,",key2,count_map.get(key1).get(key2)/d);
                  fw.write(contents);
                  //str += key1+":"+key2+":=" +count_map.get(key1).get(key2)+"\n";
               }
               
            }
            fw.write(priorfile_string+"\n\n");
         }
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
            if(!key1.contains("header") && !key1.contains("stamp")){
               str += key1+":"+key2+":=" +count_map.get(key1).get(key2)+"\n";
            }
            
         }
      }
      return str;
   }
   
   public static void print_percentages(int i){
      out.println("\n\n\n\n\n\n\n\n\nPERCENTAGES:");
      Set<String> keys1 = count_map.keySet();
      for(String key1 : keys1){
         String priorfile_string = key1+",";
         if(!key1.contains("GSR") || !key1.contains("Manual")  || !key1.contains("Auto")  || !key1.contains("Pupil") ){
            Set<String> keys2 = count_map.get(key1).keySet();
            for (String key2 : keys2){
               
               out.format("%s:%s:=%.3f%n",key1,key2,count_map.get(key1).get(key2)/i);
               priorfile_string += String.format("%s:=%.3f,",key2,count_map.get(key1).get(key2)/i);
               //out.println(key1+":"+key2+"=" +count_map.get(key1).get(key2));
            }
         }
         out.println(priorfile_string);
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
         dims[1] = StringUtils.splitPreserveAllTokens(thisLine, ",").length;
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