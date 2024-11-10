package org.example;

import org.apache.lucene.analysis.Analyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.*;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;
import org.apache.tika.metadata.Metadata;
import org.apache.tika.parser.AutoDetectParser;
import org.apache.tika.parser.ParseContext;
import org.apache.tika.sax.BodyContentHandler;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

public class Main {
    public static void main(String[] args) throws IOException, ParseException {
        String mode = args[0];
        String directoryPath = args[2];
        String query = (args.length > 4 && "-query".equals(args[3])) ? args[4] : null;
        Directory index = FSDirectory.open(Paths.get("src/main/java/org/example/index"));
        RomanianTextAnalyzer analyzer = new RomanianTextAnalyzer();

        if ("-index".equals(mode)) {
            indexDocuments(directoryPath, analyzer, index);
        } else if ("-search".equals(mode)) {
            if (query == null) {
                System.out.println("Search requires -query params");
                System.exit(1);
            }
            searchExample(index, analyzer, query);
        } else {
            System.out.println("Use -index or -search in your command.");
            System.exit(1);
        }
    }

    private static void indexDocuments(String directoryPath, Analyzer analyzer, Directory index) throws IOException {
        Path indexPath = Paths.get("src/main/java/org/example/index");

        if (Files.exists(indexPath)) {
            Files.walk(indexPath)
                    .map(Path::toFile)
                    .forEach(File::delete);
        }
        Files.createDirectories(indexPath);
        IndexWriterConfig config = new IndexWriterConfig(analyzer);
        try (IndexWriter w = new IndexWriter(index, config)) {
            indexDirectory(new File(directoryPath), w);
        }
    }

    private static void indexDirectory(File dir, IndexWriter writer) throws IOException {
        File[] files = dir.listFiles();
        if (files != null) {
            for (File file : files) {
                if (file.isDirectory()) {
                    indexDirectory(file, writer);
                } else {
                    if (file.getName().endsWith(".pdf") || file.getName().endsWith(".txt") || file.getName().endsWith(".doc") || file.getName().endsWith(".docx")) {
                        indexFile(writer, file);
                    }
                }
            }
        }
    }

    private static void indexFile(IndexWriter writer, File file) throws IOException {
        try (FileInputStream fis = new FileInputStream(file)) {
            BodyContentHandler handler = new BodyContentHandler(-1);
            Metadata metadata = new Metadata();
            AutoDetectParser parser = new AutoDetectParser();
            ParseContext context = new ParseContext();
            try {
                parser.parse(fis, handler, metadata, context);
                Document doc = new Document();
                doc.add(new TextField("contents", handler.toString(), Field.Store.YES));
                doc.add(new StringField("filename", file.getName(), Field.Store.YES));
                writer.addDocument(doc);
            } catch (Exception e) {
                System.err.println("Error processing file " + file.getName());
            }
        }
    }

    private static void searchExample(Directory index, Analyzer analyzer, String querystr) throws IOException, ParseException {
        try (IndexReader reader = DirectoryReader.open(index)) {
            IndexSearcher searcher = new IndexSearcher(reader);
            Query q = new QueryParser("contents", analyzer).parse(querystr);
            TopDocs docs = searcher.search(q, 5);
            ScoreDoc[] hits = docs.scoreDocs;

            for (int i = 0; i < hits.length; i++) {
                int docId = hits[i].doc;
                Document d = searcher.doc(docId);
                System.out.println((i + 1) + ". " + d.get("filename"));
            }
        } catch (IndexNotFoundException e) {
            System.out.println("Run java -jar target/docsearch-1.0-SNAPSHOT.jar -index -directory <path to docs> first");
            System.exit(1);
        }
    }
}
