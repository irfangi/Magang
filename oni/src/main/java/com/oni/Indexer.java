package com.oni;

import java.io.File;
import java.io.IOException;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.apache.lucene.analysis.standard.StandardAnalyzer;
import org.apache.lucene.document.Document;
import org.apache.lucene.document.Field;
import org.apache.lucene.document.StringField;
import org.apache.lucene.document.TextField;
import org.apache.lucene.index.IndexWriter;
import org.apache.lucene.index.IndexWriterConfig;
import org.apache.lucene.store.Directory;
import org.apache.lucene.store.FSDirectory;

import twitter4j.Status;

public class Indexer {
	private Directory directory;
	private IndexWriterConfig iwc;
	
	
	
	public Directory getDirectory() {
		return directory;
	}
	
	
	Path path = Paths.get("file");
	public Indexer() {
		StandardAnalyzer analyzer = new StandardAnalyzer();
		try {
			directory = FSDirectory.open(path);
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		iwc = new IndexWriterConfig(analyzer);
		iwc.setOpenMode(IndexWriterConfig.OpenMode.CREATE_OR_APPEND);
	}
	
	public void indexing(Status status) {
		try {
			System.out.println("*indexing...");
			IndexWriter iw = new IndexWriter(directory,iwc);
			addDoc(iw,status);
			iw.close();
		}catch(Exception e) {
			System.out.println("gagal indexing..."+e);
		}
	}
	
	private static void addDoc(IndexWriter iw, Status status) throws IOException, SQLException {
		Document doc = new Document();
		doc.add(new StringField("id",Long.toString(status.getId()),Field.Store.YES));
		doc.add(new TextField("value",status.getText(),Field.Store.YES));
		System.out.println("==indexing==");
		System.out.println("ID : "+status.getId());
		System.out.println("text : "+status.getText());
		iw.addDocument(doc);
	}
}
