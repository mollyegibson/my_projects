## ITEM-BASED COLLABORATIVE FILTERING RECOMMENDER SYSTEM ## 
### Implemented MapReduce jobs in Java, ran on local HDFS cluster ###

Final RMSE: 0.9306


To run the job:
In the directory where you have the Java files (workspace/netflix_recs/src), first compile the Java classes (in this case, I have 4 different jobs in separate directories).

$ javac -classpath `hadoop classpath` job0/*.java

$ javac -classpath `hadoop classpath` coOccurrenceMatrix/*.java

$ javac -classpath `hadoop classpath` computeSim/*.java

$ javac -classpath `hadoop classpath` predictRatings/*.java



Then we have .class files in the same job0 directory. Next, collect compiled Java files into a JAR file:

$ jar cvf job0.jar job0/*.class

$ jar cvf coOccurrenceMatrix.jar coOccurrenceMatrix/*.class

$ jar cvf computeSim.jar computeSim/*.class

$ jar cvf predictRatings.jar predictRatings/*.class

Now submit the MapReduce job to Hadoop using the JAR file: 

* note: would be much more efficient to combine all of the drivers into one file

$ hadoop jar job0.jar job0.Job0Driver TrainingRatings.txt job0_output1 

This hadoop jar command names the JAR file to use (job0.jar), the driver’s class (Job0Driver) and the HDFS input/output directories. Then the output will be in job0_output1/part-r-00000. Subsequently run the following:

$ hadoop jar coOccurrenceMatrix.jar coOccurrenceMatrix.CoOccurrenceDriver job0_output3/part-r-00000 co_output2

$ hadoop jar computeSim.jar computeSim.ComputeSimDriver co_output2/part-r-00000 sim_output2

$ hadoop jar predictRatings.jar predictRatings.PredictRatingsDriver -files TestingRatings.txt,sim_output2 TrainingRatings.txt predictions0

$ hadoop jar rmse.jar rmse.RMSECalculator predictions0/part-r-00000

************************************** for testing locally ******************************************

Click on the project name in Eclipse and select Properties. Select Java Build Path on the left hand side of the popup window and then click Add External Jars.  Select File System (LHS) and navigate to usr/lib/Hadoop/client-0.20.  Choose all three jar files which start with “slf4j”.  OK. 

Then right-click on driver class (Job0Driver in our case) and select Run As > Run Configurations… Ensure Java Application is selected on the LHS.  Then choose (top left corner) New Launch Configuration.  Switch to Arguments tab and enter the local input/output files.  I put a subset of the TrainingRatings.txt dataset into the same directory as the program so local tests are quick:
$ head -n 4000 TrainingRatings.txt > train_subset.txt
 Now run it!

***************************************************************************************************

JOB 0

The mapper in job0 of my program receives the input file in the form (__,movieID, userID, rating) and outputs (key=userID, value=movieID:rating).  Then the reducer receives a list of {movieID:rating, …} for each userID - i.e. the key=userID and the value={list of all the user’s ratings}.  Then the reducer completes the pre-processing step for each user by iterating through the list and counting the user’s number of ratings and sum of all ratings. The output from the job is in the form (key=userID, value=movieID:rating:numRatings:sumRatings).

Co-Occurrence Matrix

Once we have divided the data up into ‘user vectors’, the next step is to find the similarities between movies.  So the next MapReduce job takes our output from Job0, (userID \t movieID:rating:numRatings:sumRatings), and we use an identity mapper so that the reducer receives (userID, list of {movieID1:rating:num:sum, …}).  The reducer then iterates through the list and outputs the following for each pair of movies the user has seen (movieA:movieB, movieIDA:ratingA:numReviews:sumReviews, movieIDB:ratingB:numReviews:sumReviews).  

ComputeSimilarity

The next job computes the Pearson correlation between each pair of movies.  The mapper receives (_, movieA:movieB \t listOfStats) and outputs the movie pair as a key, stats as the value; the reducer then receives movieA:movieB and the list of all rating/stats pairs (corresponding to every pair of users who’ve both watched the movie).  The reducer loops through the list to find the Pearson correlation between the two movies.  The output of the job is in the format (key=movieA:movieB, value=similarity).

PredictRatings

Now it’s time to make our predictions. The input will be TrainingRatings again so that the MapReduce job will again compile a list of all movieID:rating's for each user. This step is identical to the mapper in job0 so we can use that existing mapper class. We'll feed TestingRatings.txt and our output from the previous step (sim_output) as distributed cache. The reducer has a setup() method which takes the sim_output and creates a nested map for the similarity matrix. The setup() also takes TestingRatings.txt and creates a map of the users and movies for which we’re predicting ratings.  Then the reduce() method receives, from the training set, the userID and all of the movies the user has already rated. To compute the prediction, we use a weighted avg to compare the similarities and user’s previous ratings with the current movie in question. The output is in the format (actual_rating, prediction).

RMSECalculator

Calculates root mean squared error from the predictRatings output. 


