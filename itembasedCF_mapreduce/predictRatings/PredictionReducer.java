package predictRatings;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;
import java.util.List;

import org.apache.hadoop.io.Text;
//import org.apache.hadoop.io.LongWritable;
//import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Reducer;
import org.apache.log4j.Logger;


/*
 * Reducer receives, from the training set, (userID, list of {movieID:rating, ....})
 * We also need to feed the similarities in, which we'll do in setup()
 */
public class PredictionReducer extends Reducer<Text, Text, Text, Text>{
	
	
	public static Logger LOGGER = Logger.getLogger(PredictRatingsDriver.class);
	
	File similarities = new File("sim_output2/part-r-00000");
	File testset = new File("TestingRatings.txt");
	
	private Text K1 = new Text();
	private Text V2 = new Text();
	
	
	private Map<String, Map<String, Float>> simsMap = new HashMap<String, Map<String, Float>>();
	private Map<String, List<MovieAndRating>> testingUsersMap = new HashMap<String, List<MovieAndRating>>();
	
	
	@Override
	public void reduce(Text key, Iterable<Text> values, Context context) 
		throws IOException, InterruptedException {
	
		//MovieAndRating mr = null;
		String usrID = key.toString().trim();
		
		if (testingUsersMap.containsKey(usrID)) { // we only care about users that we're testing; otherwise ignore
			
			// creating a list of all the user's rated movies so we can use that info to make our prediction
			List<MovieAndRating> mrList = new ArrayList<MovieAndRating>();
			
			for (Text v : values) {
				String[] tokens = v.toString().trim().split(":");
				MovieAndRating mr = new MovieAndRating(tokens[0], Float.parseFloat(tokens[1]));
				mrList.add(mr);
				
			}
			
			// the loop through all of the movies for which we're predicting and calculate predicted rating w weightedavg function
			for (MovieAndRating m : testingUsersMap.get(usrID)) {
				
				// parameters
				float pred = weightedAvg(m.getMovieID() , mrList);
				//K1.set(key + ":" + m.getMovieID());
				// could set the key as the userID and movieID we're predicting but keeping it simple for the RMSE calculator
				K1.set("");
				V2.set(String.format("%.3f,%.3f", m.getRating(), pred));

			}
		
			//for (MovieAndRating m : testingUsersMap.get(key.toString()))
			
			context.write(K1, V2);	
		}
		
	}
	
	// method to predict a movie's rating based on all of the user's ratings and the similarity matrix
	private float weightedAvg(String movieID, List<MovieAndRating> allRatings) {
		
		float weights = 0, total = 0;
		List<RatingAndSim> rsList = new ArrayList<RatingAndSim>();
		float similarity = 0;
		
		for (MovieAndRating mr : allRatings) {
			/* find movieA's similarity to every other movie for that user
			*  and then weight the user's rating for each other movie with their similarities
			*  sum over all pairs, keep track of the total 
			*/
			// ensure that we don't compare the same movie
			if (Integer.parseInt(movieID) != Integer.parseInt(mr.getMovieID())) {
				String idkey = String.valueOf(Math.min(Integer.parseInt(movieID), Integer.parseInt(mr.getMovieID())));
				String other = String.valueOf(Math.max(Integer.parseInt(movieID), Integer.parseInt(mr.getMovieID())));	
				// the smaller movieID always comes first !
				if (simsMap.containsKey(idkey)) {
				// look up if movieB is in movieA's list of movie-sims - if so, set the similarity
					
					if (simsMap.get(idkey).containsKey(other)) {
						similarity = simsMap.get(idkey).get(other);	
					}
					else { 
						similarity = 0;
					}
				} 
			} else {
				similarity = 1;
			}

			RatingAndSim rs = new RatingAndSim(mr.getRating(), similarity);
			rsList.add(rs);	
		}
		
		for (RatingAndSim rsim : rsList) {
			weights += rsim.rating * rsim.similarity;
			total += rsim.similarity;
		}
		
		if (total != 0) { //avoid division by zero
			return weights / total;
		}
		else {
			return total;
		}
		
	}
	
	
	// object for the similarities map, so that movieA will have a whole set of MovieAndSims corresponding to it
	class RatingAndSim {  
		float rating; float similarity;
		
		public RatingAndSim(float rating, float sim) {
			this.rating = rating; 
			this.similarity = sim;
		}
	}
	
	// object for each user in the testset to have a list of all his movies and ratings 
	/*class MovieAndRating {
		String movieID; float rating;
		
		public MovieAndRating(String movID, float rating) {
			this.movieID = movID;
			this.rating = rating;
		}
	}*/
	// use the setup to load our calculated movie-pair similarities
	// and to initialize a map with each user and their list of movies/ratings
	@Override
	protected void setup(Context context)
		throws IOException, InterruptedException {
		
		String line;
		String movieA;
		String[] Btokens;
		//String movieB;
		//MovieAndSim B = null;
		
		
		try (BufferedReader br = new BufferedReader(new FileReader(similarities))) {
			while ((line = br.readLine()) != null) {
				line = line.toString().trim();
				movieA = line.split(":")[0];
				Btokens = line.split(":")[1].split("\t");
				

	/*			if (Integer.parseInt(movieA) > Integer.parseInt(Btokens[0])) {
					movieB = movieA;
					movieA = Btokens[0];
				} else {
					movieB = Btokens[0];
				}*/
				
				
				//Bmap.put(Btokens[0], Float.parseFloat(Btokens[1]));
				/*				
				 * 
				 * B.movieID = Btokens[0];
				B.similarity = Float.parseFloat(Btokens[1]);*/
				
				// a map so you can check out movieA's similarity w all the other movies
				if (simsMap.containsKey(movieA)){ // if movA is already in the map, add B's info to the sub-map
					simsMap.get(movieA).put(Btokens[0], Float.parseFloat(Btokens[1]));
				} else {
					// otherwise create the sub-map with B's info and add it to the big map with movieA
					Map<String, Float> Bmap = new HashMap<String, Float>();
					Bmap.put(Btokens[0], Float.parseFloat(Btokens[1]));
					simsMap.put(movieA, Bmap);
				}
				
			
			} br.close();
		} catch(IOException x) {
			System.err.format("IOException: %s%n", x);
		}
		
		
		
		//float ratingA;
		
		/*LOGGER.info("----------------------------------------------------------------------------------------");
		LOGGER.info(simsMap.keySet());
		LOGGER.info("----------------------------------------------------------------------------------------");
		*/
		try (BufferedReader buffr = new BufferedReader(new FileReader(testset))) {
			while ((line = buffr.readLine()) != null) {
				String[] inputline = line.toString().trim().split(",");
				String mov = inputline[0];
				String user = inputline[1];
				float rat = Float.parseFloat(inputline[2]);
/*				B.movieID = Btokens[0];
				B.similarity = Float.parseFloat(Btokens[1]);*/
				
				// initially setup a list of all users' movies 'n ratings from the testset - which we'll be predicting
				
				if (testingUsersMap.containsKey(user)) { // if the user is already in our dictionary, append the movieRating to the existing list
					testingUsersMap.get(user).add(new MovieAndRating(mov, rat));
				} else {
					// otherwise create a list and add the user to the dict
					List<MovieAndRating> movrats = new ArrayList<MovieAndRating>();
					movrats.add(new MovieAndRating(mov, rat));
					testingUsersMap.put(user, movrats);
				}
								
			
			} buffr.close();
		} catch(IOException x) {
			System.err.format("IOException: %s%n", x);
		}

		
	}
	
	
}
