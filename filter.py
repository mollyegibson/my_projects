import sys
from pyspark import SparkContext
import re

if __name__ == "__main__":
	if len(sys.argv) < 2:
		print(sys.stderr, "Usage: WordCount <file>")
		exit(-1)    

sc = SparkContext()
stopwords = []
poswords = []
negwords = []


######################### initialize word lists ############################
stopfile = sys.argv[-2] #open(sys.argv[-2],'r') #.readlines()
posfile = sys.argv[-4] #open(sys.argv[-4], 'r') #.readlines()
negfile = sys.argv[-3] #open(sys.argv[-3], 'r') #.readlines()

filedict = {stopfile: stopwords, posfile: poswords, negfile: negwords}

for fname in filedict:	# for each file, empty its contents into corresponding list
	with open(fname) as f:	
		for line in f:
			filedict[fname].append(line.strip('\n')) 

######## continue adding to the lists as we go #############################
s_append = ['bonferroni', 'python', 'table', 'equation', 'principle', 'ecosystem', 'model', 'check', 'problem', 		'command', 'commands', 'formula', 'derived', 'description', 'cloud', 'compute', 'computing', 			'computer', 'approach', 'approached', 'approaches','sentiment', 'analysis', 'homeworks',
		'run']
# problem could be a negative word but people are always writing 'this hw problem'

n_append = ['not', 'wasn', 'didn', 'haven', 'couldn', 'hasn', 'don'] #, 'hope', ['not', 'sure']

p_append = [ 'understanding', 'understand', 'help', 'helped', 'learn', 'learned', 'know'] 
# including tuples, too


for s in s_append:  
	stopwords.append(s)

for n in n_append:
	negwords.append(n)

for p in p_append:
	poswords.append(p)
#############################################################################
# adverbs often tell us a lot about the sentiment... 'very' good versus 'somewhat'
emphatic = ['mostly', 'very', 'entirely', 'exactly', 'definitely', 'certainly', 'awfully', 'really', 'truly',
		'obviously', 'absolutely', 'quite', 'especially']

hesitant = ['kindof', 'kinda', 'sorta', 'kind', 'probably', 'merely', 'nearly', 'hardly', 'usually', 'only',
		'somewhat', 'little', 'bit']
# 'of' would be dropped from 'kind of', but unlikely that anyone is using kind to mean generous

#############################################################################
def wholeFile(x):
	fname=x[0]
	notabs = re.compile(r'[\n\t\W]+') #r'\W+\t\n[^w.,;!?() ]+')
	content = notabs.sub(' ',x[1].lower())
	return [(fname,content)]

def extractnum(hwstr):
	return re.findall(r'M|[0-9]+', hwstr)[0] # findall returns a list but we want the first (only) element

def count(mylist):
	return float(len(mylist))
	
def filtreview(review):
	revlist = review.split(' ')
	nontriv = filter(lambda x: x not in stopwords, revlist)
	cleanrev = ' '.join(nontriv) # put back into one string
	return cleanrev.strip() # make sure no leading/trailing whitespace
	
# score calculator
#####################################################################################
# we are concerned with the length of the neg, pos lists at the end 
# so to start off, going to make count variables which give 2 points to each word

# if the word is negative, check the word before it because double negative -> positive
# therefore take away 2*(2 words) and add 2*(1 positive term) 
# if emphasis precedes the word, it should count more: +1
# -> but could be a term like 'not very hard', or 'didn't really love' so check for that and adjust accordingly
# if a hesitant adverb precedes word, count less: -1
# if a positive word has a neg word before it, "not happy", that should count as 1 negative term
# 	
def calc_score(review):
	revlist = review.split(' ')
	pos = list(filter(lambda x: x in poswords, revlist))	
	neg = list(filter(lambda x: x in negwords, revlist))
	poscount = count(pos)*2	
	negcount = count(neg)*2	
	for word in revlist:
		if word in neg: 
			if revlist[revlist.index(word)-1] in neg:
				negcount -= 2 
				poscount += 2
			if revlist[revlist.index(word)-1] in emphatic:
				if revlist[revlist.index(word)-2] in n_append:
					negcount -= 2
					poscount += 2
				else: negcount += 1
			if revlist[revlist.index(word)-1] in hesitant:
				negcount -= 1	
		if word in pos: 
			if revlist[revlist.index(word)-1] in neg:
				poscount -= 2
			if revlist[revlist.index(word)-1] in emphatic:
				if revlist[revlist.index(word)-2] in n_append:
					poscount -= 2
					negcount += 2
				else: poscount += 1
			if revlist[revlist.index(word)-1] in hesitant:
				poscount -= 1
	if poscount!=0 and negcount!=0:
		p = round(poscount / (poscount + negcount), 2)		
		s = round((poscount - negcount) / (poscount + negcount), 2)
	else: 
		p = poscount
		s = poscount + negcount #both zero
	return '\t'.join([str(s), str(p)])

######################################################################################




allreviews = []
reviews = []
extract = [] 
tsvform = []
# i know this is sloppy code and i'm writing a lot of redundancies but i want to change the rdd one step at a time to make sure it works
	
for n in range(1, len(sys.argv)-4): # loop throug the first n arguments, last 3 are txt files
	allreviews.append(sc.wholeTextFiles("file://" + sys.argv[n]).flatMap(wholeFile)) 
# this makes a list of RDDs

# get rid of reviews with fewer than 50 words
# idk how to handle the four diff labeled directories other than to make lists
for r in allreviews:
	reviews.append(r.filter(lambda line: len(line[1].split(' ')) >= 50)) 

# extract the label and hw number from the file path	
for rev in reviews:
	extract.append(rev.map(lambda fields: (fields[0].split('/'),fields[1])) \
				.map(lambda entry: (entry[0][-1].split('_')[0], entry[0][-2], entry[1])) \
				.map(lambda xx: (extractnum(xx[0]), xx[1], xx[2])) \
				.map(lambda yy: (yy[0], yy[1], filtreview(yy[2]))) \
				.map(lambda zz: (zz[0], zz[1], calc_score(zz[2]), zz[2])))

def toTSV(data):	
	return '\t'.join(d for d in data)

#def tofloat(numstring):
#	return map(float, numstring)

for ex in extract:
	tsvform.append(ex.map(toTSV))

	

print("------------------------------------------------")

# hive table: hwnumber int, label string, review string, numpos int, numneg int


i = 0
for t in tsvform:
	# first convert them into the right types
	t.map(lambda row: (int(row[0]), row[1], float(row[2]), float(row[3]), row[4]))
	t.saveAsTextFile('file://' + sys.argv[-1] + str(i))
	i+=1	
	for review in t.take(3): print(review)


#print(extractnum('hw6'))
#print("pos " + len(poswords) + " neg " + len(negwords))
print("number of review input files: " + str(len(sys.argv)-5))
print("------------------------------------------------")
# makes it easier to spot output between all the other jargon
