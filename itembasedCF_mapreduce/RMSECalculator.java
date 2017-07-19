package rmse;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileReader;
import java.io.IOException;

public class RMSECalculator {

	public static double calcRMSE(String inputfile) {
		double n = 0;
		File predictions = new File(inputfile);
		double rmseSum = 0;
		double rmse = 0;
		String line;
		String[] tokens;
		
		try {
			BufferedReader br = new BufferedReader(new FileReader(predictions));
			while ((line = br.readLine()) != null) {
				n++;
				line = line.toString().trim();
				/*tokens = line.split("\t")[1].split(",");
				double rating = Double.parseDouble(tokens[0].split(":")[1]);
				double prediction = Double.parseDouble(tokens[1].split(":")[1]);*/
				
				double rating = Double.parseDouble(line.split(",")[0]);
				double prediction = Double.parseDouble(line.split(",")[1]);
				
				rmseSum += Math.pow(rating - prediction, 2);
				//System.out.println(rmse);
				
			}
			br.close();
			
		} catch (IOException x) {
			System.err.format("IOException: %s%n", x);
		}
		
		// we take the total error and divide by n then take sqrt()
		rmse = Math.sqrt(rmseSum/n);
		return rmse;
		
		
	}
	
	
	public static void main(String[] args) {
		if (args.length < 1 ) {
			System.out.println("usage: RMSE Calculator");
		}
		
		String inputfile = args[0];
		double rmse = calcRMSE(inputfile);
		System.out.printf("RMSE: %.4f",  rmse);
		
	}
}
