DROP TABLE wordmap_ngrams;

---------- singlegrams file has 1)b. --------

CREATE TABLE wordmap_ngrams (wmap array<struct<ngram:array<string>, 
				estfrequency:double>>);

INSERT OVERWRITE TABLE wordmap_ngrams SELECT context_ngrams(sentences(lower(review)),
	array(null, null), 5000) as wm FROM reviews;

DROP TABLE twograms;

CREATE TABLE twograms (ngram array<string>, estfrequency double);

INSERT OVERWRITE TABLE twograms SELECT X.ngram, X.estfrequency FROM (SELECT
		EXPLODE(wmap) AS X FROM wordmap_ngrams) Z;

-------------- which pos, neg ngrams appear most frequently---------
SELECT tg.ngram, tg.estfrequency FROM twograms tg, poswords pw WHERE
	 tg.ngram[0] LIKE pw.posword AND tg.ngram[1] LIKE pw.posword LIMIT 15;

------------ to find out which assignment was most (-),(+)-----
--SELECT AVG(pscore), hw_number FROM reviews GROUP BY hw_number;

