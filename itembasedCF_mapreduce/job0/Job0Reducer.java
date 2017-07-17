package job0;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.Text;
//import org.apache.hadoop.io.LongWritable;
//import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;


/*
 * Reducer receives (movieID, list of userID:rating's) 
 * and it outputs (userID, movieID:movieRating:numRating:sumRatings)
 */
public class Job0Reducer extends Reducer<Text, Text, Text, Text>{
	
	private Text K1 = new Text();
	private Text V1 = new Text();
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) 
		throws IOException, InterruptedException {
		
		int numRatings = 0;
		long sumRatings = 0;
		List<String> movies = new ArrayList<String>();
		
		// pre-processing step: compute each user's number of ratings/ sum of all ratings
		for (Text v : values) {
			sumRatings += Double.parseDouble(v.toString().split(":")[1]);
			numRatings += 1;
			movies.add(v.toString());
		}
		
		for (String mov : movies) {
			String[] vals = mov.split(":");
			V1.set(new Text(vals[0] + ";" + vals[1] + ":" + numRatings + ":" + sumRatings));
			context.write(key, V1);
		}
		

		
	}

}
