package computeSimilarity;


import java.io.IOException;

import org.apache.hadoop.io.LongWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Mapper;
import org.apache.log4j.Logger;

/*
 * mapper will receive an input file with each line:
 * 
 * movieA:movieB \t ratingA:numRatings:sumRatings, ratingB:numRatings:sumRatings
 * so we want to set moviePair as the key and the set of ratings as the value
 * 
 *
 */

public class ComputeSimMapper extends Mapper<LongWritable, Text, Text, Text>{
	
	public static Logger LOGGER = Logger.getLogger(ComputeSimDriver.class);
	
	//reusable variables to set for each output
	
	private Text K1 = new Text();
	private Text V1 = new Text();

	@Override
	public void map(LongWritable key, Text value, Context context) 
		throws IOException, InterruptedException {
		
		String[] tokens = value.toString().trim().split("\t");
		K1.set(tokens[0]); 
		V1.set(tokens[1]);
		
		context.write(K1, V1);
		

			
		}
		
		
}
	



