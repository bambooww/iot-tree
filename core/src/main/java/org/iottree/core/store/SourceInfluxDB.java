package org.iottree.core.store;

import java.io.Closeable;
import java.util.List;
import java.util.Map;
import java.util.concurrent.TimeUnit;

//import org.influxdb.InfluxDB;
//import org.influxdb.InfluxDB.ConsistencyLevel;
//import org.influxdb.InfluxDBFactory;
//import org.influxdb.dto.BatchPoints;
//import org.influxdb.dto.Point;
//import org.influxdb.dto.Point.Builder;
//import org.influxdb.dto.Query;
//import org.influxdb.dto.QueryResult;

//import com.influxdb.client.InfluxDBClient;
//import com.influxdb.client.InfluxDBClientFactory;
//
//import kotlin.NotImplementedError;

public class SourceInfluxDB // extends Source implements Closeable
{
//	private String dbUser =null ;
//	
//	private String dbPsw = null ;
//	
//	private String dbUrl = null ;
//	
//	/**
//	 * influx database
//	 */
//	private String dbName = null ;
//	
//	private String retentionPolicy = null ;
//	
//	private InfluxDBClient influxDB = null ;
//	
//	public SourceInfluxDB asParams(String user,String psw,String dburl,String dbname,String retention_policy)
//	{
//		this.dbUser = user ;
//		this.dbPsw = psw ;
//		this.dbUrl = dburl ;
//		this.dbName = dbname ;
//		this.retentionPolicy = retention_policy;
//		influxDbBuild();
//		return this ;
//	}
//	
//	public void createDB(String dbname)
//	{
//		influxDB.setDatabase(this.dbName);
//	}
//	
//	@Override
//	public String getSorTp()
//	{
//		return "influxdb";
//	}
//
//	@Override
//	public String getSorTpTitle()
//	{
//		return "InfluxDB";
//	}
//	
//	public boolean checkValid(StringBuilder failedr)
//	{
//		return true ;
//	}
//
//	private InfluxDB influxDbBuild()
//	{
//		if(influxDB!=null)
//			return influxDB;
//		
//		try
//		{
//			influxDB = InfluxDBClientFactory.connect(dbUrl,dbUser,dbPsw) ;
//		}
//		finally
//		{
//			influxDB.setRetentionPolicy(retentionPolicy) ;
//		}
//		
//		influxDB.setLogLevel(InfluxDB.LogLevel.NONE) ;
//		return influxDB;
//	}
//	
//	
//	
//	public boolean checkConn(StringBuilder failedr)
//	{
//		throw new NotImplementedError();
//	}
//	
//	public void createDefaultRetentionPolicy()
//	{
//		final String FMT = "create retention policy \"%s\" on \"%s\" duration %s replication %s default";
//		String cmd = String.format(FMT,"default",dbName,"30d",1) ;
//		this.query(cmd) ;
//	}
//	
//	public QueryResult query(String cmd)
//	{
//		return influxDB.query(new Query(cmd,dbName)) ;
//	}
//	
//	public void insert(String measurement,Map<String,String> tags,Map<String,Object> fields,long time,TimeUnit time_unit)
//	{
//		Builder builder = Point.measurement(measurement) ;
//		builder.tag(tags) ;
//		builder.fields(fields) ;
//		if(0 != time)
//			builder.time(time, time_unit);
//		influxDB.write(dbName,retentionPolicy,builder.build()) ;
//	}
//	
//	public void batchInsert(BatchPoints bp)
//	{
//		influxDB.write(bp);
//	}
//	
//	public void batchInsert(final String dbname,String retention_policy,ConsistencyLevel clvl,List<String> records)
//	{
//		influxDB.write(dbname,retention_policy,clvl,records);
//	}
//	
//	public void close()
//	{
//		influxDB.close();
//	}
//	
//	
//	public static Point buildPoint(String measurement,long time,Map<String,String> tags,Map<String,Object> fields)
//	{
//		return Point.measurement(measurement).time(time, TimeUnit.MILLISECONDS).tag(tags).fields(fields).build();
//	}
}
