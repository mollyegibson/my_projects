package predictRatings;


import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

/*
 * The first mapper will receive an input file with each line:
 * (movieID, userID, rating) 
 * and we want to output (userID, movieID:rating) both k,v as Text objects
 * 
 */

public class Job0Mapper extends Mapper<LongWritable, Text, Text, Text>{

	
	//reusable variables to set for each output
	private Text K1 = new Text();
	private Text V1 = new Text();

	
	@Override
	public void map(LongWritable key, Text value, Context context) 
		throws IOException, InterruptedException {
		
		// the value is each line of the input file (movID, userID, rating) and the key is an unnecessary byte offset 
		// first step is to put the three values into an array
		String[] vals = value.toString().trim().split(",");
		
		
		// and then put them into our desired format
		String movieIDAndRating = vals[0] + ':' + vals[2];
		
		V1.set(movieIDAndRating);
		K1.set(vals[1]);
		
		context.write(K1, V1);
	}
	

}


