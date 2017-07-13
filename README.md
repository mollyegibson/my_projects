# README #

This page contains a few examples of CS projects I've worked on. 

### DATABASE MANAGEMENT SYSTEMS - TRANSACTIONS AND CONCURRENCY CONTROL ###
###  using SimPy ###
Part A: With the typical strategy, where there are multiple readers and a single writer, there is a chance of starvation. Please suggest and implement a solution that would avoid starvation.

* Read requests: if there is no lock on the data, then grant read lock for the period of time requested. If there is a write lock, wait until the 
read lock is free and it's the reader's turn.

* Write requests: if there is no write lock on the data, then grant write lock for the period of time requested. If there is a write lock, wait until the write lock is free and it's the writer's turn.

Part B: Invalid Dirty Writes - this approach assumes that the likelihood of write conflict is rare. Thus, locks to read or write are not acquired, but rather the timestamp of the last read/write is used. If the timestamp of the last read/write is after the start of the current write, the current write is invalidated and the transaction is reattempted.


### CLOUD COMPUTING - NLP: SENTIMENT ANALYSIS ###
### using Hive, PySpark, HDFS in class Linux VM ###
Text Pre-processing: Each review is stored in a txt file: hwx_review_id.txt. "x" is a placeholder for the homework
number (1,. . . ,10, M) and id is a placeholder for a unique randomly assigned 6 character
review ID. They're separated into folders corresponding the following labels: positive, negative, neutral, not-labeled. Filter out all empty reviews, reviews shorter than 50 words, and non-word characters. Then put data into TSV form with (hw_num, label, review) as the columns. Now put the data into a HIVE table, making sure to pre-process the text data into all lower-case letters and removing stop-words.

Sentiment analysis: Create an algorithm to score the sentiment of each review and predict the label. Write a script to find the most frequently occuring n-grams with n=1, n=2, n=3. Check the accuracy of your predictions compared to the original labels. Test locally; run on cluster with the following commands:

$ hive -f myHiveScript.hql 

$ spark-submit --master yarn-client mySparkProgram.py /path/to/input/files

### LINEAR REGRESSION ANALYSIS on TRAFFIC FATALITY DATA (NHTSA) ###