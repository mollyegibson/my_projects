package coOccurrenceMatrix;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
//import org.apache.hadoop.io.LongWritable;
//import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;

/*
 * Reducer receives key=userID, values=list of {movieID:rating:numRatings:sumRatings, ...}
 *  and we want to output (movieA:movieB, ratingA1, numRatingsA, sumRatingsA, ratingB1,numRatingsB, sumRatingsB)
 * where the key is the movie pair and the value is the list of stats for one user they have in common
 * 
 */
public class CoOccurrenceReducer extends Reducer<Text, Text, MoviePair, Text>{
	
	
	private MoviePair movPair = new MoviePair();
	private Text V2 = new Text();
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) 
		throws IOException, InterruptedException {
		
		
		List<String> movies = new ArrayList<String>();
		
		
		// add each of the movies&stats to a list
		for (Text value : values) {
			movies.add(value.toString().trim());	
		}

		for (int i=0; i < movies.size()-1; i++) {
			String[] movieAstats = movies.get(i).split(";");
			String movieA = movieAstats[0];
			
			for (int j=i+1; j < movies.size(); j++) {
				String[] movieBstats = movies.get(j).split(";");
				String movieB = movieBstats[0];
				
				if (Integer.parseInt(movieA) > Integer.parseInt(movieB)) {
					movPair.setA(movieB);
					movPair.setB(movieA);	
				} else {
					movPair.setA(movieA);
					movPair.setB(movieB);
				}
			
				V2.set(movieAstats[1] + "," + movieBstats[1]);
				context.write(movPair, V2);
			}
		}
		
	}

}
