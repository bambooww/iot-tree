package org.iottree.core;

import java.util.HashMap;
import java.util.Map;

import org.influxdb.InfluxDB.ConsistencyLevel;
import org.influxdb.dto.BatchPoints;
import org.influxdb.dto.Point;
import org.influxdb.dto.QueryResult;
import org.iottree.core.store.SourceInfluxDB;

import junit.framework.TestCase;

public class StoreTest extends TestCase
{
	static final String DB_NAME = "db-test" ;
	
	public void testBatchInsert() throws Exception
	{
		
		try(SourceInfluxDB db = new SourceInfluxDB())
		{
			db.asParams("admin","admin","localhost",DB_NAME,"hour") ;
			//QueryResult res = db.query("select * from measurement where name='' order by teim desc limit 1000") ;
			Map<String,String> tags1 = new HashMap<>() ;
			tags1.put("tag1", "标签值");
			Map<String,String> tags2 = new HashMap<>() ;
			tags1.put("tag2", "标签值");
			
			Map<String,Object> fields1 = new HashMap<>() ;
			fields1.put("field1", "abc");
			fields1.put("field2", 123456);
			
			Map<String,Object> fields2 = new HashMap<>() ;
			fields1.put("field1", "St是abc");
			fields1.put("field2", 3.1415926);
			
			Point p1 = SourceInfluxDB.buildPoint("table1", System.currentTimeMillis(), tags1, fields1);
			Point p2 = SourceInfluxDB.buildPoint("table1", System.currentTimeMillis(), tags2, fields2);
			BatchPoints batch_pts = BatchPoints.database(DB_NAME).tag("tag1", "标签值1")
					.retentionPolicy("hour").consistency(ConsistencyLevel.ALL).build();
		}
	}
}
