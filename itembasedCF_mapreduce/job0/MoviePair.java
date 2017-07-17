package coOccurrenceMatrix;

import java.io.DataInput;
import java.io.DataOutput;
import java.io.IOException;

import org.apache.hadoop.io.IntWritable;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.io.WritableComparable;

// a MoviePair class to help make the co-occurrence matrix
public class MoviePair implements WritableComparable<MoviePair>{
	
	private Text movieA;
	private Text movieB;
	
	//three types of constructors:

    public MoviePair(Text movieA, Text movieB, IntWritable co_count) {
        this.movieA = movieA;
        this.movieB = movieB;

    }

    public MoviePair(String movieA, String movieB, int count) {
        //this(new Text(word),new Text(neighbor));
    	this(new Text(movieA), new Text(movieB), new IntWritable(count));
    }
    //gotta have an empty constructor
    public MoviePair() {
        this.movieA = new Text();
        this.movieB = new Text();

    }
    
    public String toString() {
    	return movieA + ":" + movieB ;
    }
	@Override
	public void readFields(DataInput in) throws IOException {
		movieA.readFields(in);
		movieB.readFields(in);

	}
	
    public static MoviePair read(DataInput in) throws IOException {
        MoviePair movPair = new MoviePair();
        movPair.readFields(in);
        return movPair;
    }
	@Override
	public void write(DataOutput out) throws IOException {
		movieA.write(out);
		movieB.write(out);	

	}
	@Override
	public int compareTo(MoviePair o) {
		// we want to see if they're the same two movies but in opposite order
		int compAs = this.movieA.compareTo(o.getMovieA());
		int compAB = this.movieA.compareTo(o.getMovieB());
		int compB = 1;
		//if the other moviePair doesn't contain movieA, return 
		if (compAs != 0 && compAB != 0) {  
			return compAs; 
		}
		//but if the other pair does contain movieA, check if it also has movB
		
		// either this.movieA = o.movieA
		else if (compAs == 0) {
			compB = this.movieB.compareTo(o.getMovieB());
		}
		else if (compAB == 0) { // or this.movieA = o.movieB
			compB = this.movieB.compareTo(o.getMovieA());
		}
		// if it did contain A, but not B, return
		if (compB != 0) {
			return compB;
		}
		
		return this.movieA.compareTo(o.getMovieA());
		
	}
	@Override
	public int hashCode() {
		final int prime = 31;
		int result = 1;
		result = prime * result
				+ ((movieB == null) ? 0 : movieB.hashCode());
		result = prime * result + ((movieA == null) ? 0 : movieA.hashCode());
		return result;
	}
	@Override
	public boolean equals(Object obj) {
		if (this == obj)
			return true;
		if (obj == null || getClass() != obj.getClass())
			return false;
		
		MoviePair other = (MoviePair) obj;
		
		if (movieB != null ? !movieB.equals(other.movieB) : other.movieB != null) 
			return false;
		if (movieA != null ? !movieA.equals(other.movieA) : other.movieA != null)
			return false;
		
		return true;

	}
	
    public void setA(String movA){
        this.movieA.set(movA);
    }
    public void setB(String movB){
        this.movieB.set(movB);
    }

    public Text getMovieA() {
        return movieA;
    }

    public Text getMovieB() {
        return movieB;
    }
	
}