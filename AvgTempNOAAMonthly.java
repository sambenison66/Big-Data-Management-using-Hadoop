/**
  *  Samuel Benison Jeyaraj Victor
  *  sambenison66@gmail.com *  Project 2 - Introduction to Map-Reduce (Hadoop)
  */

import java.io.IOException;
import java.util.*;
import java.lang.*;
import org.apache.commons.lang.StringUtils;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.conf.*;
import org.apache.hadoop.io.*;
import org.apache.hadoop.mapred.*;
import org.apache.hadoop.util.*;

public class AvgTempNOAAMonthly {
	
  public static ArrayList<Integer> arrayTemp = new ArrayList<Integer>();
  public static ArrayList<Integer> arrayPrcp = new ArrayList<Integer>();

  // Mapper Class for AvgTempNOAA-Monthly
  public static class MapReduceMapper extends MapReduceBase
      implements Mapper<LongWritable, Text, Text, Text> {


    public void map(LongWritable key, Text val,
        OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {

      // Getting each line of input and converting it into a string
      String line = val.toString();
      
      String getToken[] = line.split(",");
      String rawDate = getToken[2]; // Date
      String rawprcp = getToken[3]; // Precipitation
      String rawtmp = getToken[6]; // Temperature
      if(StringUtils.isNumeric(rawDate)) {
	      String dateKey = rawDate.substring(0, 6); // Took Year & Month from Date
	      String valueReport = rawtmp + "," + rawprcp; // Put Temperature & Precipitation in a String with comma
	      
	      output.collect(new Text(dateKey), new Text(valueReport));
      }
    }
  }


  // Reducer class for AvgTempNOAA-Monthly
  public static class MapReduceReducer extends MapReduceBase
      implements Reducer<Text, Text, Text, Text> {

    public void reduce(Text key, Iterator<Text> values,
        OutputCollector<Text, Text> output, Reporter reporter)
        throws IOException {

           // Iterator to find the average Temp & Prcp for each month
           int tempTotal = 0, prcpTotal = 0; // Initializing tempTotal & prcpTotal as 0
           int tempNum = 0, prcpNum = 0; // Initializing the total
           int avgTemp = 0, avgPrcp = 0; // Initializing average Temp & Prcp
           float formatTemp, formatPrcp; // Format Result
           String compTempRes = "NOT COMPARED", compPrcpRes = "NOT COMPARED"; // Default Comp Result
           while (values.hasNext()) {
        	   // Get the value and Split the Temp & Prcp
        	   String nextValue = values.next().toString();
        	   String getReport[] = nextValue.split(",");
        	   int currTemp = Integer.parseInt(getReport[0]);
        	   int currPrcp = Integer.parseInt(getReport[1]);
        	   // -9999 means Report i missing, so this if condition will ignore missing data
        	   if(currTemp != -9999) {
        		   tempTotal += currTemp; // Sum the Temperature of Particular month
        		   tempNum += 1; // Get the total number
        	   }
        	   // -9999 means Report i missing, so this if condition will ignore missing data
        	   if(currPrcp != -9999) {
        		   prcpTotal += currPrcp; // Sum the Precipitation of Particular month
        		   prcpNum += 1; // Get the total number
        	   }
           }
           // Calculate the average Temperature
           if(tempNum != 0) {
        	   avgTemp = tempTotal / tempNum;
           }
           // Calculate the average Precipitation
           if(prcpNum != 0) {
        	   avgPrcp = prcpTotal / prcpNum;
           }
           // Next is to gather all the averages and compare with previous year
           arrayTemp.add(avgTemp);
           arrayPrcp.add(avgPrcp);
           int arrayTempCount = arrayTemp.size();
           int arrayPrcpCount = arrayPrcp.size();
           // Compare and Record the Comparison result for Temperature
           if (arrayTempCount > 12) {
        	   int compInt = arrayTemp.get(arrayTempCount - 13);
        	   if(compInt < avgTemp) {
        		   compTempRes = "Getting Warmer than Last Year";
        	   } else if(compInt > avgTemp) {
        		   compTempRes = "Getting Colder than Last Year";
        	   } else if(compInt == avgTemp) {
        		   compTempRes = "Same Result as Last Year";
        	   }
           }
           // Compare and Record the Comparison result for Precipitation
           if (arrayPrcpCount > 12) {
        	   int compInt = arrayPrcp.get(arrayPrcpCount - 13);
        	   if(compInt < avgPrcp) {
        		   compPrcpRes = "Getting Wetter than Last Year";
        	   } else if(compInt > avgPrcp) {
        		   compPrcpRes = "Getting Dryer than Last Year";
        	   } else if(compInt == avgPrcp) {
        		   compPrcpRes = "Same Result as Last Year";
        	   }
           }
           // Convert the format from 171 to 17.1
           formatTemp = (float) (avgTemp * 0.1);
           formatPrcp = (float) (avgPrcp * 0.1);
           // Sort the Output values into a string
           String outputValue = " - " + String.valueOf(formatTemp) + "C - " + compTempRes + 
        		   " - " + String.valueOf(formatPrcp) + "mm - " + compPrcpRes;
           // Print the output
           output.collect(key, new Text(outputValue));  // sending the value to output file
    }
  }


  // Main Driver class for AvgTempNOAA-Monthly
  
  public static void main(String[] args) {

    JobConf conf = new JobConf(AvgTempNOAAMonthly.class);
    conf.setJobName("AvgTempNOAAMonthly");
    
    // Initializing the No.of Mappers and Reducers
    conf.setNumMapTasks(Integer.parseInt(args[2]));
    conf.setNumReduceTasks(Integer.parseInt(args[3]));
    
    // Initialize the Job Start Time
    long jobStartTime = System.currentTimeMillis();

    // Output KeyClass and ValueClass declarations
    conf.setOutputKeyClass(Text.class);
    conf.setOutputValueClass(Text.class);

    conf.setInputFormat(TextInputFormat.class);
    conf.setOutputFormat(TextOutputFormat.class);
    
    // Getting the Input and Output path
    FileInputFormat.addInputPath(conf, new Path(args[0]));
    FileOutputFormat.setOutputPath(conf, new Path(args[1]));

    // Mapper and Reducer class declarations
    conf.setMapperClass(MapReduceMapper.class);
    conf.setReducerClass(MapReduceReducer.class);

    try {
      JobClient.runJob(conf);
    } catch (Exception e) {
      e.printStackTrace();
    }
    
    // Initialize the Job End Time
    long jobEndTime = System.currentTimeMillis();
    
    // Calculating the Time Taken by the job
    long timeTaken = jobEndTime - jobStartTime;
    System.out.println("\n Time Taken = " + timeTaken);
    
  }
}

//References:
//https://www.youtube.com/watch?v=MoKW5eY5yVY
//https://www.youtube.com/watch?v=GWj3rSNuog0
