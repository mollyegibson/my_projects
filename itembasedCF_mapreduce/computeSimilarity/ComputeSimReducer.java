package computeSimilarity;

import java.io.IOException;



import org.apache.hadoop.io.Text;
//import org.apache.hadoop.io.LongWritable;
//import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;



/*
 * Reducer receives key=movieA:movieB, values=list of {ratingA:numRatings:sumRatings,ratingB:num:sum, ...}
 *  and we want to output (movieA:movieB, similarity)
 * where the key is the movie pair and the value is the list of stats for one user they have in common
 * 
 */
public class ComputeSimReducer extends Reducer<Text, Text, Text, Text>{
	
	
	private Text K1 = new Text();
	private Text V2 = new Text();
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) 
		throws IOException, InterruptedException {
	
		double similarity = 0;
		double NratingAsqSum = 0;
		double NratingBsqSum = 0;
		double dotProduct = 0;
		double meanA = 0;
		double meanB = 0;
		int n = 0;
		
		for (Text value : values) {
			
			String[] ratings = value.toString().trim().split(",");
			String[] ratingsA = ratings[0].split(":");
			String[] ratingsB = ratings[1].split(":");
			double A = Double.parseDouble(ratingsA[0]);
			double B = Double.parseDouble(ratingsB[0]);
			
			// movie's mean rating is sumRatings / numRatings
			// we only need to compute this on the first iteration
			if (n == 0) {
				meanA = Double.parseDouble(ratingsA[2])/Double.parseDouble(ratingsA[1]);
				meanB = Double.parseDouble(ratingsB[2])/Double.parseDouble(ratingsB[1]);	
			}
			n++;
			
			
			dotProduct += (A - meanA)*(B - meanB);
			NratingAsqSum += Math.pow(A,2);
			NratingBsqSum += Math.pow(B,2);
				
		}
		// ensure that we're not dividing by zero
		if (NratingBsqSum != 0 && NratingAsqSum != 0) {
			similarity = dotProduct / (Math.sqrt(NratingAsqSum)*Math.sqrt(NratingBsqSum));
		} 

		String[] moviepair = key.toString().trim().split(":");
		
		// the same movie
		if (Integer.parseInt(moviepair[0])==Integer.parseInt(moviepair[1])) {
			similarity = 1;
		}
		K1.set(key); 
		V2.set(new Text(String.valueOf(similarity)));
		context.write(K1, V2);
	}
		

}
