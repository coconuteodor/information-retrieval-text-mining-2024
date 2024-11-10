# Information Retrieval

* **DISCLAIMER**

I needed to modify the shade plugin in the build because I was getting the following error:

`Exception in thread "main" java.lang.SecurityException: Invalid signature file digest for Manifest main attributes`

* **PERSONAL CONTRIBUTION**

For this project, I developed the main logic so the two flows work
based on the given work mode (index/search). I used the Tika library to 
parse the different types of files (pdf/txt/doc/docx) to have a
uniform input for the text analyzer. 

I created my own RomanianTextAnalyzer
that extends StopwordAnalyzerBase to which I added lower casing, replacing 
diacritics with the base letter, stop filter for stopwords (a list manually inserted)
and a SnowballFilter for stemming with the help of the RomanianStemmer from Lucene.
I also tested with the SnowballFilter before and after removing the diacritics, but didn't
notice a big difference. 

I also created the logic so that the indexing is saved between the runs, so we don't need
to wait each time for indexing. The indexing is overriding any old indexes. The index and search
commands use a common directory to write/read the indexes. (src/main/java/org/example/index)
