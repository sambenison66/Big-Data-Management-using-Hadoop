ReadMe:

1. My Input file consists of 551766 tuples.
2. Jar file is attached with the file name, run the file with the below syntax

hadoop jar <Jar file directory> <Class Name> <HDFS Input path> <Output HDFS path> <no.of Mapper> <no.of Reducer>

3. Time Taken to run the job will be calculated by the program.
4. In the Input file (.csv), 3rd column should be Date, 4th should be Precipitation data, 7th should be Temperature. If not column number should be adjusted in the program.
5. I have taken Precipitation, Temperature, Wind Report
6. It has 3 different class file for Monthly, Seasonal and Yearly average calculation
7. The program will calculate Average Temperature compare it with same month/season of last year and return whether it got warmer or colder.
8. Similarly it will calculate average Precipitation, compare and return whether it got Dryer or Wetter.
9. All the Outputs were placed in the output file.
10. Some important commands were mentioned in a seperate file.


Collect your own weather data csv file from here: https://www.ncdc.noaa.gov/cdo-web/