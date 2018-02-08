package com.oni;

import java.io.IOException;
import java.io.ObjectOutputStream;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.util.ArrayList;
import java.util.List;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.index.DirectoryReader;
import org.apache.lucene.index.IndexReader;
import org.apache.lucene.queryparser.classic.ParseException;
import org.apache.lucene.queryparser.classic.QueryParser;
import org.apache.lucene.search.IndexSearcher;
import org.apache.lucene.search.Query;
import org.apache.lucene.search.ScoreDoc;
import org.apache.lucene.search.TopDocs;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

public class Searcher {
	IndexSearcher is;
	QueryParser qp;
	Query q;
	Indexer indexer = new Indexer();
	Path path = Paths.get("file");

	public List<Twitter> doSearching(String keyword) throws IOException {
		Directory d = FSDirectory.open(path);
		StandardAnalyzer analyzer = new StandardAnalyzer();
		String querystr = keyword;
		try {
			q = new QueryParser("value",analyzer).parse(querystr);
		} catch (ParseException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		int hitsPerPage = 0;
		IndexReader reader = DirectoryReader.open(d);
		IndexSearcher searcher = new IndexSearcher(reader);
		TopDocs docs = searcher.search(q,hitsPerPage);
		ScoreDoc[] hits = docs.scoreDocs;

		List<Twitter> listPencarian = new ArrayList<Twitter>();
		System.out.println("Menemukan "+hits.length+" :");
		for(int i = 0;i<hits.length;i++) {
			int docId = hits[i].doc;
			Document doc = searcher.doc(docId);
			Twitter twitter = new Twitter(doc.get("id"),doc.get("value"));

			listPencarian.add(twitter);
			//listPencarian.add(twitter);
			//a.add(doc.get("id"),doc.get("value"))
			System.out.println((i+1)+"."+doc.get("id")+"\t"+doc.get("value"));
		}
		reader.close();
		return listPencarian;
	}
}
