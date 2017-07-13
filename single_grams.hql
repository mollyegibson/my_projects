-- one long array of tuples (word, freq):
--CREATE TABLE wordmap (wmap array<struct<ngram:array<string>,
--						 estfrequency:double>>); 

--INSERT OVERWRITE TABLE wordmap SELECT context_ngrams(sentences(lower(review)),
--				 array(null), 2000) as wm FROM reviews;

-- split words into their own lines
--CREATE TABLE indivwords (ngram string, estfrequency double);

--INSERT OVERWRITE TABLE indivwords SELECT X.ngram[0], X.estfrequency FROM 
--	(SELECT EXPLODE(wmap) AS X FROM wordmap) Z;


 -- get most frequently appearing positive words across all reviews
SELECT tw.ngram, tw.estfrequency FROM indivwords tw
	 LEFT OUTER JOIN poswords pw ON (tw.ngram = pw.posword)
	 WHERE pw.posword IS NOT NULL ORDER BY tw.estfrequency DESC;

-- and most freq. appearing negative words:
SELECT tw.ngram, tw.estfrequency FROM indivwords tw
         LEFT OUTER JOIN negwords nw ON (tw.ngram = nw.negword)
         WHERE nw.negword IS NOT NULL ORDER BY tw.estfrequency DESC;

