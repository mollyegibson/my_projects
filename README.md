# README #

This repository contains a few examples of CS projects I've worked on. 

### DATABASE MANAGEMENT SYSTEMS - TRANSACTIONS AND CONCURRENCY CONTROL ###
###  using SimPy ###
•	Simulated database transaction reading and writing with SimPy to address two frequently arising failures: starvation and invalid writes.  Generated randomly occurring read and write requests using two-phase locking strategy to avoid starvation.  Implemented timestamp-based concurrency control with 3.5% invalid write transactions.

### CLOUD COMPUTING - NLP: SENTIMENT ANALYSIS ###
### using Hive, PySpark, HDFS in class Linux VM ###
•	Filtered and pre-processed unstructured user review data and put into HIVE tables. Wrote an algorithm using PySpark to score sentiment in each review and predict ground-truth labels.  Ran on HDFS cluster in Linux virtual machine.

### ITEM-BASED COLLABORATIVE FILTERING RECOMMENDATION SYSTEM ###
### MapReduce implemented in Java, ran on HDFS in Linux VM ###
•	Developed a recommender system in Java with a subset of the Netflix Prize dataset (3,250,000 movie reviews).  Divided the data into user vectors, paired co-occurring movies, and computed similarity with Pearson correlation.  Predicted ratings for users in a testing dataset (100,000 reviews).  Ran on HDFS cluster in Linux VM.

### LINEAR REGRESSION ANALYSIS on TRAFFIC FATALITY DATA (NHTSA) ###
•	Ran multiple regressions on different features to determine which were most significant in predicting traffic fatalities.
