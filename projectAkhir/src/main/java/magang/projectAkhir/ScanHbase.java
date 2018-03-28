package magang.projectAkhir;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;
import org.apache.jasper.tagplugins.jstl.core.Out;

import com.mongodb.BasicDBObject;
import com.mongodb.DB;
import com.mongodb.DBCollection;
import com.mongodb.DBObject;
import com.mongodb.Mongo;
import com.yammer.metrics.core.HealthCheck.Result;

import twitter4j.JSONException;
import twitter4j.JSONObject;

public class ScanHbase {
	 public static void main (String[] args) throws IOException, JSONException {
	        HTable table = new HTable(HBaseConfiguration.create(), "twitter");
	        Scan s = new Scan();
	        s.setMaxVersions(1);
	        ResultScanner scanner = table.getScanner(s);
	        for (org.apache.hadoop.hbase.client.Result rr = scanner.next(); rr != null; rr = scanner.next()) {
	        	
	            BasicDBObject obj = new BasicDBObject();
	            obj.put("_id", Bytes.toString(rr.getValue(Bytes.toBytes("data"), Bytes.toBytes("id"))));
	            obj.put("username", Bytes.toString(rr.getValue(Bytes.toBytes("data"), Bytes.toBytes("user"))));
	            obj.put("hastag", Bytes.toString(rr.getValue(Bytes.toBytes("data"), Bytes.toBytes("hastag"))));
	            obj.put("waktu", Bytes.toString(rr.getValue(Bytes.toBytes("data"), Bytes.toBytes("waktu"))));
	            obj.put("text", Bytes.toString(rr.getValue(Bytes.toBytes("data"), Bytes.toBytes("text"))));
	            save_mongo(obj);
	        }
	    }
	 private static void save_mongo(BasicDBObject obj) {
		Mongo mongo = new Mongo("localhost",27017);
		DB db = mongo.getDB("magang");
			 DBCollection collection = db.getCollection("tweet");
			 collection.insert(obj);
			 System.out.println("sukses simpan");
	 }
	}
