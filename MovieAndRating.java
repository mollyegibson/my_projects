package predictRatings;

public class MovieAndRating implements Comparable<MovieAndRating>{

	private String movieID;
	private float rating;
	
	public MovieAndRating(String movieID, float rating) {
		this.movieID = movieID;
		this.rating = rating;
	}
	
	public String toString(){
		return movieID + ":" + rating;
	}
	
	public String getMovieID() {
		return movieID;
	}
	
	public void setMovieID(String movieID) {
		this.movieID = movieID;
	}
	
	public float getRating() {
		return rating;
	}
	
	public void setRating(float rating) {
		this.rating = rating;
	}
	
	@Override
	public int compareTo(MovieAndRating o) {

		int A = Integer.parseInt(movieID);
		int B = Integer.parseInt(o.movieID);
		
		if ((A - B) < 0) {
			return -1;
		} else if ((A - B) > 0) {
			return 1;
		}else {
			return 0;
		}
	}
}
