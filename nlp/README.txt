NATURAL LANGUAGE PROCESSING - SENTIMENT ANALYSIS
Text Pre-processing: Each review is stored in a txt file: hwx_review_id.txt. "x" is a placeholder for the homework
number (1,. . . ,10, M) and id is a placeholder for a unique randomly assigned 6 character
review ID. They're separated into folders corresponding the following labels: positive, negative, neutral, not-labeled. Filter out all empty reviews, reviews shorter than 50 words, and non-word characters. Then put data into TSV form with (hw_num, label, review) as the columns. Now put the data into a HIVE table, making sure to pre-process the text data into all lower-case letters and removing stop-words.

Sentiment analysis: Create an algorithm to score the sentiment of each review and predict the label. Write a script to find the most frequently occuring n-grams with n=1, n=2, n=3. Check the accuracy of your predictions compared to the original labels. Test locally; run on cluster with the following commands:

$ hive -f myHiveScript.hql 

$ spark-submit --master yarn-client mySparkProgram.py /path/to/input/files

