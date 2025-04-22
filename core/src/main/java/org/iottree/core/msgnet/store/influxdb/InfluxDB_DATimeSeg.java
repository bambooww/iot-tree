package org.iottree.core.msgnet.store.influxdb;

import java.time.Instant;
import java.util.Date;
import java.util.List;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.QueryApi;
import com.influxdb.query.FluxRecord;
import com.influxdb.query.FluxTable;

/**
 * Data Analysis by Time Seg
 * 
 * 基于InfluxDB按照时间段的数据分析（计算）的节点支持
 * 
 * 1）设定输入的Measurement
 * 
 * 功能：
 * 1 按每小时，每天，每月，每年统计，每个时间段的差值、均值、总和值等，并可以形成数据输出
 *     可以自动记录上一次时间，后续减少计算量
 *  
 * @author jason.zhu
 *
 */
public class InfluxDB_DATimeSeg extends MNNodeMid
{
	String flux ;
	@Override
	public String getTP()
	{
		return "influxdb_da_ts";
	}

	@Override
	public String getTPTitle()
	{
		return "DA Time Seg";
	}

	@Override
	public String getColor()
	{
		return "#f3b484";
	}

	@Override
	public String getIcon()
	{
		return "PK_influxdb";
	}

	@Override
	public int getOutNum()
	{
		return 1;
	}


	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		return Convert.isNotNullTrimEmpty(this.flux) ;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("flux", flux) ;
		return jo;
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.flux = jo.optString("flux") ;
	}


	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		InfluxDB_M dbm = (InfluxDB_M)this.getOwnRelatedModule() ;
		InfluxDBClient client = dbm.RT_getClient() ;
		QueryApi qapi = client.getQueryApi() ;
		String res = qapi.queryRaw(this.flux) ;
		List<FluxTable> fts = qapi.query(this.flux) ;
		System.out.println(fts.size()) ;
		//FluxTable ft = fts.get(0) ;
		int s = fts.size() ;
		for(int i = 0 ; i < s ; i ++)
		{
			FluxTable ft = fts.get(i) ;
			System.out.println("tb idx="+i) ;
			for(FluxRecord rec:ft.getRecords())
			{
				Instant inst = rec.getTime() ;
				Date dt = Date.from(inst) ;
				String fn = rec.getField();
				Object val = rec.getValueByKey("_value") ;
				Object max = rec.getValueByKey("max") ;
				Object min = rec.getValueByKey("min") ;
				Object mean = rec.getValueByKey("mean") ;
				System.out.println("read at="+Convert.toFullYMDHMS(dt)+" "+fn+"="+val+"  max="+max+" mean="+mean+" min="+min) ;
			}
		}
		return RTOut.createOutAll(new MNMsg().asPayload(res));
	}
	
	

}
