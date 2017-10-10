from contextlib import contextmanager
from pyspark import SparkContext
from pyspark import SparkConf


SPARK_APP_NAME='Word Count ex.'
SPARK_EXECUTOR_MEMORY='20000m'

@contextmanager
def spark_manager():
	conf = SparkConf().setAppName(SPARK_APP_NAME) \
			.set("spark.executor.memory", SPARK_EXECUTOR_MEMORY)
	spark_context = SparkContext(conf=conf)

	try:
		yield spark_context
	finally: 
		spark_context.stop()

with spark_manager() as context:
	logFile = "simplefile.txt"
	textFileRDD = context.textFile(logFile)
	wc = textFileRDD.flatMap(lambda line: line.split())
	wc.saveAsTextFile("output5")

print("done")
