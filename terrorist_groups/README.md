GLOBAL TERRORISM DATABASE - Predictive Modeling

Data from http://www.start.umd.edu/gtd/contact/ - full GTD dataset
data downloaded in .xlsx format, so I converted to familiar csv:

$ pip install csvkit

$ in2csv datasets/globalterrorismdb_0617dist.xlsx > globalterrorism_db.csv

Goal of the challenge: use attack type, weapons used, description of the attack, etc. to build a model that can predict what group may have been responsible for an incident

My method: The first thing I did was remove rows from the dataset where the group name appeared less than five times. Later I went back and took out this step, which had an interesting effect on my results. Removing groups with <5 occurrences had a positive effect on the overall F1 scores of my model when the unknown labels were removed, with the major exception being Australia & Oceania: it jumped from 0.9464 with the filtered data to 0.4775 when I didn't remove groups with counts <5. This makes sense because there was the least amount of data for Australia & Oceania, and lots of groups with only a handful of incidents attributed to them. Ultimately I decided to filter out rows labeled by groups with only 1 or 2 occurrences. This seemed to be a happy medium and gave the best results.
Then I divided the incidents up by regions and chose to only consider the five most frequently occurring groups in each region, labeling all the remaining attacks as 'other', so that there would be six labels that the attacks could be classified as. Then I tried a few different classification algorithms to see which was the best at predicting terrorist groups. I tried two methods of handling the 'Unknown' attacks: first I labeled them as 'other' so that we didn't have to discard almost half of the data, then I tried removing all of the unlabeled rows from our training data. Labeling the unknown attacks as 'other' had a positive effect on the f1 scores.
Random Forest seemed to perform the best so I delved into it further. I used GridSearchCV() to choose optimal parameters for the random forest classifier.
Then I used SelectFromModel() to choose the most important features in predicting groupname, first for the whole dataset and then by region. 

Outcomes: Ultimately, my model was able to predict groupname on the whole dataset with an F1 score of 0.893 based on only three features: year, nationality, and region. When dividing up the data into global regions and removing 'region' as a feature, we achieved an F1 score of 0.9131.


Weaknesses/potential for improvement: Obviously filtering the dataset to only include rows labeled with groups responsible for n+ attacks has an effect on the results of the model. Removing all rows with <5 attacks seemed to take away too many data points, but not removing any rows resulted in very poor results for regions with a lot of isolated incidents. Maybe could optimize the criteria on which to filter the original dataset for each region.
