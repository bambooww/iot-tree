package org.iottree.core.store;

import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_val;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.InfluxDBClientFactory;
import com.influxdb.client.domain.Bucket;
import com.influxdb.client.domain.Organization;

@data_class
public class SourceInfluxDB extends Source //implements Closeable
{
	@data_val
	String url = "http://localhost:8086" ;
	
	@data_val
	String token = null ;
	
	@data_val
	String org = null ;
	
	@data_val
	String bucket = null ;
	
	
//	private InfluxDBClient influxDB = null ;
	
	public SourceInfluxDB asParams(String url,String token,String org,String bucket)
	{
		this.url = url ;
		this.token = token ;
		this.org = org ;
		this.bucket = bucket ;
		return this ;
	}
	
	@Override
	public String getSorTp()
	{
		return "influxdb";
	}

	@Override
	public String getSorTpTitle()
	{
		return "InfluxDB";
	}
	
	public String getUrl()
	{
		if(url==null)
			return "" ;
		return this.url;
	}
	
	public String getToken()
	{
		if(token==null)
			return "" ;
		return this.token;
	}
	
	public String getOrg()
	{
		if(org==null)
			return "" ;
		return this.org;
	}
	
	public String getBucket()
	{
		if(bucket==null)
			return "" ;
		return this.bucket;
	}
	
	public boolean checkValid(StringBuilder failedr)
	{
		if(Convert.isNullOrEmpty(url))
		{
			failedr.append("source influxdb url is null or empty") ;
			return false;
		}
		if(Convert.isNullOrEmpty(token))
		{
			failedr.append("source influxdb token is null or empty") ;
			return false;
		}
		if(Convert.isNullOrEmpty(org))
		{
			failedr.append("source influxdb org is null or empty") ;
			return false;
		}
		if(Convert.isNullOrEmpty(bucket))
		{
			failedr.append("source influxdb bucket is null or empty") ;
			return false;
		}
		return true ;
	}

	
	public boolean checkConn(StringBuilder failedr)
	{
		//throw new RuntimeException("no impl") ;
		if(!this.checkValid(failedr))
			return false ;
		
		try(InfluxDBClient dbc=  InfluxDBClientFactory.create(getUrl(),
					getToken().toCharArray(),getOrg(),getBucket()))
		{
			List<Organization> orgs = dbc.getOrganizationsApi().findOrganizations();
			// System.out.println("find orgs num="+orgs.size()) ;
			orgs.forEach(o -> System.out.println("    "+o.getName()));
			
			List<Bucket> buckets = dbc.getBucketsApi().findBuckets();
			// System.out.println("find buckets num="+buckets.size()) ;
			buckets.forEach(b -> System.out.println("   "+b.getName()));
		}
		catch(Exception ee)
		{
			failedr.append(ee.getMessage()) ;
			ee.printStackTrace();
			return false;
		}
		return true;
	}

	public static final String EXCHG_TP ="source_influxdb" ;

	@Override
	public String getExchgTP()
	{
		return EXCHG_TP;
	}

	@Override
	protected JSONObject toExchgPmJO()
	{
		return null;
	}

	@Override
	protected boolean fromExchgPmJO(JSONObject pmjo)
	{
		return false;
	}
	
//	
//	public void close()
//	{
//		influxDB.close();
//	}
	
	
	
}
