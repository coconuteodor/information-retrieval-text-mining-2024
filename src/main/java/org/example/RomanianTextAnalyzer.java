package org.example;

import org.apache.lucene.analysis.*;
import org.apache.lucene.analysis.core.StopFilter;
import org.apache.lucene.analysis.miscellaneous.ASCIIFoldingFilter;
import org.apache.lucene.analysis.snowball.SnowballFilter;
import org.apache.lucene.analysis.standard.StandardTokenizer;
import org.tartarus.snowball.ext.RomanianStemmer;

import java.util.Arrays;
import java.util.List;

public class RomanianTextAnalyzer extends StopwordAnalyzerBase {

    public static final CharArraySet ROMANIAN_STOP_WORDS_SET;

    static {
        final List<String> stopWords = Arrays.asList("vreo", "acelea", "cata", "cita", "degraba", "lor", "alta", "tot", "ai", "dat", "despre", "peste", "bine", "dar", "foarte", "avea", "multi", "cit", "cat", "alt", "mai", "sa", "fie", "tu", "intrucat", "multe", "orice", "dintr", "dintre", "dintr-o", "dintr-", "un", "se", "intr", "intr-o", "intr-", "un", "niste", "multa", "insa", "il", "fost", "a", "abia", "nimic", "sub", "acel", "in", "altceva", "si", "avem", "altfel", "c", "ea", "acest", "li", "parca", "fi", "dintre", "unele", "m", "acestei", "mare", "cel", "este", "pe", "atitia", "atatia", "uneori", "acela", "iti", "astazi", "acestui", "o", "imi", "ele", "ceilalti", "pai", "fata", "noua", "sa-", "ti", "altul", "au", "i", "prin", "conform", "aceste", "anume", "azi", "k", "unul", "ala", "unei", "fara", "ei", "la", "aceeasi", "u", "inapoi", "acestea", "acesta", "aceasta", "catre", "sale", "asupra", "as", "aceea", "ba", "ale", "da", "le", "apoi", "aia", "suntem", "cum", "isi", "inainte", "s", "de", "cind", "cand", "cumva", "chiar", "acestia", "daca", "sunt", "care", "al", "numai", "cui", "sus", "tocmai", "prea", "cu", "mi", "eu", "doar", "niciodata", "nicidecum", "exact", "putini", "aiurea", "tuturor", "celor", "astfel", "atunci", "citeva", "cateva", "cat", "ca", "sau", "fel", "intre", "acolo", "nostri", "ma", "mult", "una", "ceea", "iar", "iara", "sintem", "suntem", "ati", "din", "geaba", "sai", "caruia", "adica", "inca", "are", "aici", "ca", "ia", "nici", "d", "oricum", "asta", "carora", "face", "citiva", "cativa", "voi", "unor", "f", "atat", "toata", "alaturi", "cea", "nu", "totusi", "ce", "altii", "acum", "sint", "sunt", "capat", "mod", "deasupra", "cam", "vom", "b", "toate", "careia", "aceasta", "atit", "atat", "nimeni", "ii", "ci", "unde", "ul", "plus", "era", "sa-", "mi", "l", "spre", "dupa", "nou", "cele", "acea", "un", "incit", "incat", "n", "cei", "or", "va", "deci", "acelasi", "atatea", "h", "vor", "decit", "decat", "noi", "cineva", "desi", "ceva", "j", "ului", "atitea", "atatea", "avut", "ar", "pina", "pana", "t", "atata", "unui", "el", "citi", "asa", "totul", "pentru", "atita", "v", "alti", "asemenea", "atatia", "te", "ne", "deja", "unii", "p", "atare", "cite", "cate", "cine", "cand", "toti", "vreun", "ori", "r", "alte", "lui", "ti", "ni", "aceia", "am");
        final CharArraySet stopSet = new CharArraySet(stopWords, false);
        ROMANIAN_STOP_WORDS_SET = CharArraySet.unmodifiableSet(stopSet);
    }

    public RomanianTextAnalyzer() {
        super(ROMANIAN_STOP_WORDS_SET);
    }

    @Override
    protected TokenStreamComponents createComponents(String fieldName) {
        final Tokenizer source = new StandardTokenizer();
        TokenStream result = new LowerCaseFilter(source);
        result = new SnowballFilter(result, new RomanianStemmer());
        result = new ASCIIFoldingFilter(result);
        result = new StopFilter(result, stopwords);
        return new TokenStreamComponents(source, result);
    }
}
