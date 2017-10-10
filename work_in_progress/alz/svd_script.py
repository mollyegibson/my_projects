import sys
from pyspark.mllib.linalg import Vectors
from pyspark.mllib.linalg.distributed import RowMatrix

from contextlib import contextmanager
from pyspark import SparkContext
from pyspark import SparkConf

if __name__ == "__main__":
	if len(sys.argv) < 2:
		print(sys.stderr, "Usage: WordCount <file>")
		exit(-1)    

sc = SparkContext()



datafile = "data/train.tsv"
adRDD = sc.textFile(datafile)
admap = adRDD.map(lambda line: line.split("\t"))
print(admap.shape)
	







