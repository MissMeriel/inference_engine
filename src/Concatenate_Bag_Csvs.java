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
import java.io.File;
import java.io.FilenameFilter;

public class Concatenate_Bag_Csvs{
   //args[0]: directory containing csv files
   public static HashMap<String,HashMap<String,Double>> countmap = new HashMap<String,HashMap<String,Double>>();
   public static String[][] csv_array;
   public static File[] matchingFiles;
   public static ArrayList<String> headers;
   
   public static void main(String[] args) throws IOException {
      File rootdir = new File(args[0]);
      matchingFiles = rootdir.listFiles(new FilenameFilter(){
         public boolean accept(File dir, String name) {
            return name.startsWith("sweep_for_target_2019-04-") && name.endsWith("interpolated.csv");
         }
      });
      String outfile = args[0] +"/all_bags_concat.csv";
      System.out.println("Files to concatenate: "+matchingFiles.length);
      int rows = setup_master_headers();
      csv_array = new String[rows][headers.size()];
      int current_row = 0;
      int filenumber = 1;
      FileWriter fw = new FileWriter(outfile);
      fw.write(String.join(",", headers)+"\n");
      
      int total_rows = 0;
      for(File f : matchingFiles){
         FileInputStream fis = new FileInputStream(f);
         DataInputStream dis = new DataInputStream(fis);
         String thisLine = dis.readLine();
         String[] temp = thisLine.split(",");
         ArrayList<String> current_headers = new ArrayList(Arrays.asList(temp));
         //add nonexistent header cols to headerline
         //update_master_headers(headers);
         while((thisLine = dis.readLine()) != null){
            //match header of this sheet to header of master
            String[] master_line = format_to_master_headers(StringUtils.splitPreserveAllTokens(thisLine, ","), current_headers);
            try{
               //System.out.println( Arrays.asList(master_line));
               fw.write(String.join(",", Arrays.asList(master_line))+"\n");
            } catch(Exception e){
               e.printStackTrace();
            }
            System.out.println("Wrote line # "+ (++total_rows) +" to file");
         }
         System.out.println("Wrote file # "+filenumber+" out of "+matchingFiles.length);
         filenumber++;
      }
      fw.close();
      System.out.println("Files concatenated to "+outfile);
   }
   
   public static String[] format_to_master_headers(String[] row, ArrayList<String> current_headers){
      String[] master_row = new String[headers.size()];
      int index = 0;
      for(String str : row){
         int master_index = headers.indexOf(current_headers.get(index));
         master_row[master_index] = str;
         index++;
      }
      return master_row;
   }
   
   public static void update_master_headers(String[] current_headers){
      ArrayList<String> al = new ArrayList(Arrays.asList(csv_array[0]));
      for (String str : current_headers){
         if(al.contains(str)){ continue; } else { csv_array[0][csv_array.length]=str; }
      }
      
   }
   
   public static int setup_master_headers() throws IOException{
      headers = new ArrayList();
      int lines = 0;
      int i = 1;
      for(File f : matchingFiles){
         System.out.println("file # "+i);
         FileInputStream fis = new FileInputStream(f);
         DataInputStream dis = new DataInputStream(fis);
         String thisLine = dis.readLine();
         String[] file_headers = thisLine.split(",");
         for(String str : file_headers){
            if(!headers.contains(str)){
               headers.add(str);
            }
         }
         i++;
      }
      return lines;
   }
   
   
   public static void write_to_file(String[] row, String filename){
      try{
         FileWriter fw = new FileWriter(filename);
         StringBuilder builder = new StringBuilder();
         fw.write(String.join(",", Arrays.asList(row)));
         fw.close();
      } catch(Exception e){
         e.printStackTrace();
      }
   }
   
   
   public static void write_to_file(String filename, double d){
      try{
         FileWriter fw = new FileWriter(filename);
         StringBuilder builder = new StringBuilder();
         for(String[] line : csv_array){
            fw.write(String.join(",", Arrays.asList(line)));
         }
         fw.close();
      } catch(Exception e){
         e.printStackTrace();
      }
      out.format("CSV written to %s%n",filename);
   }
   
}