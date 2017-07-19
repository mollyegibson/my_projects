package predictRatings;


import org.apache.hadoop.conf.Configuration;
import org.apache.hadoop.conf.Configured;
import org.apache.hadoop.fs.Path;
import org.apache.hadoop.io.Text;
import org.apache.hadoop.mapreduce.Job;
import org.apache.hadoop.mapreduce.lib.input.FileInputFormat;
import org.apache.hadoop.mapreduce.lib.output.FileOutputFormat;
import org.apache.hadoop.util.Tool;
import org.apache.hadoop.util.ToolRunner;
import org.apache.log4j.Logger;



public class PredictRatingsDriver extends Configured implements Tool {


	public static Logger LOGGER = Logger.getLogger(PredictRatingsDriver.class);

	

	@Override
	public int run(String[] args) throws Exception {
		
		Job job = new Job(getConf());
		job.setJarByClass(PredictRatingsDriver.class);
		job.setJobName("Prediction Driver");
		
		FileInputFormat.setInputPaths(job, new Path(args[0]));
		FileOutputFormat.setOutputPath(job, new Path(args[1]));
		
		job.setMapOutputKeyClass(Text.class);
		job.setMapOutputValueClass(Text.class);
		
		job.setMapperClass(Job0Mapper.class);
		job.setReducerClass(PredictionReducer.class);
		//job.setCombinerClass(Job1Reducer.class);
		
		job.setOutputKeyClass(Text.class);
		job.setOutputValueClass(Text.class);
		
		boolean success = job.waitForCompletion(true);
		LOGGER.info("run(): status="+success);
		return success ? 0 : 1;
	}
	
	public static void main(String[] args) throws Exception {
		int exitcode = ToolRunner.run(new Configuration(), new PredictRatingsDriver(), args);
		System.exit(exitcode);
	}

	
}