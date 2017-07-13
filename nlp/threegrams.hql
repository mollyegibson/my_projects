DROP TABLE wordmap_ngrams;

---------- singlegrams file has 1)b. --------

CREATE TABLE wordmap_ngrams (wmap array<struct<ngram:array<string>,
                                estfrequency:double>>);

INSERT OVERWRITE TABLE wordmap_ngrams SELECT context_ngrams(sentences(lower(review)),
        array(null, null, null), 5000) as wm FROM reviews;

DROP TABLE threegrams;

CREATE TABLE threegrams (ngram array<string>, estfrequency double);

INSERT OVERWRITE TABLE threegrams SELECT X.ngram, X.estfrequency FROM (SELECT
                EXPLODE(wmap) AS X FROM wordmap_ngrams) Z;

SELECT tg.ngram, tg.estfrequency FROM threegrams tg, poswords pw WHERE
         tg.ngram[0] LIKE pw.posword OR tg.ngram[1] LIKE pw.posword LIMIT 15;

SELECT tg.ngram, tg.estfrequency FROM threegrams tg, negwords nw WHERE
        tg.ngram[0] LIKE nw.negword OR tg.ngram[1] LIKE nw.negword LIMIT 15;


------------ to find out which assignment was most (-),(+)-----
--SELECT AVG(pscore), hw_number FROM reviews GROUP BY hw_number;

------------ compare first half of semester to second half-------------------
SELECT AVG(pscore) FROM reviews WHERE hw_number=1 OR hw_number=2 OR hw_number=3 OR hw_number=4
	OR hw_number=5 OR hw_number=6;

SELECT AVG(pscore) FROM	reviews	WHERE hw_number=7 OR hw_number=8 OR hw_number=9
        OR hw_number=10 OR hw_number=NULL;

----------------- compare across labels--------------------------
SELECT AVG(sscore), AVG(pscore), label FROM reviews GROUP BY label;

