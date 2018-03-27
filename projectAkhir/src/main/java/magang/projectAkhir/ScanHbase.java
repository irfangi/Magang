package magang.projectAkhir;

import java.io.IOException;

import org.apache.hadoop.hbase.HBaseConfiguration;
import org.apache.hadoop.hbase.client.HTable;
import org.apache.hadoop.hbase.client.ResultScanner;
import org.apache.hadoop.hbase.client.Scan;
import org.apache.hadoop.hbase.util.Bytes;

import com.yammer.metrics.core.HealthCheck.Result;

public class ScanHbase {
	 public static void main (String[] args) throws IOException {
	        HTable table = new HTable(HBaseConfiguration.create(), "twitter");
	        Scan s = new Scan();
	        s.setMaxVersions(1);
	        s.setTimeRange (1522131343807L, 1522131871589L);
	        ResultScanner scanner = table.getScanner(s);
	        for (org.apache.hadoop.hbase.client.Result rr = scanner.next(); rr != null; rr = scanner.next()) {
	        	System.out.println("Username");
	        	System.out.println("===========================");
	            System.out.println(Bytes.toString(rr.getRow()) + " => " +
	                    Bytes.toString(rr.getValue(Bytes.toBytes("data"), Bytes.toBytes("user")))
	            + " => " +
                Bytes.toString(rr.getValue(Bytes.toBytes("data"), Bytes.toBytes("hastag"))));
	            System.out.println("===========================");
	            
	        }
	    }
	}
