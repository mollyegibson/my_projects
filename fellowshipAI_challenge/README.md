GLOBAL TERRORISM DATABASE - Predictive Modeling

Data from http://www.start.umd.edu/gtd/contact/ - full GTD dataset
data downloaded in .xlsx format, so I converted to familiar csv:

$ pip install csvkit

$ in2csv datasets/globalterrorismdb_0617dist.xlsx > globalterrorism_db.csv

Goal of the challenge: use attack type, weapons used, description of the attack, etc. to build a model that can predict what group may have been responsible for an incident

My method: I divided the incidents up by regions and chose to only consider groups that were responsible for 5+ attacks. Then I tried a few different classification algorithms to see which was the best at predicting terrorist groups. 
Random Forest seemed to perform the best so I delved into it further. I used GridSearchCV() to choose optimal features for the random forest classifier.
Then I used SelectFromModel() to choose the most important features in predicting groupname, first for the whole dataset and then by region. 

Outcomes: Ultimately, my model was able to predict groupname with 71.15% accuracy based on only four features: year, nationality, target type, and region.
While this is perhaps not as accurate as we would like, there are a LOT of groups in the GTD and only about 50% of the incidents in the database are labeled. 
The model was able to achieve 82.56% accuracy in Central America & Caribbean, where 37% of attacks were unknown and there were only 59 labels to choose from.
It was able to achieve nearly 80% accuracy in Sub-Saharan Africa, which also had only 37% unknown attacks and 120 labels. Central Asia was consistently 
the region with the worst accuracy; this makes sense because there was not very much data in this area: only 500 total incidents, 89% of which were unlabeled. 

Weaknesses/potential for improvement: Choosing groups with only 5+ attacks was relatively arbitrary. Might want to change this and see effects on accuracy. 
It also might be worthwhile to lump all of the groups with < n attacks into an 'Other' group and explore the effects.
