package org.iottree.core.msgnet.store;

import java.util.List;

import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.store.influxdb.InfluxDB_M;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;

public class StoreTbWriter_NE extends MNNodeEnd
{
	StoreTb storeTb = null ;
	
	public StoreTbWriter_NE()
	{
	}

	@Override
	public String getTP()
	{
		return "store_tb_w";
	}

	@Override
	public String getTPTitle()
	{
		return "Store Table Writer";
	}

	@Override
	public String getColor()
	{
		return "#1d90ad";
	}

	@Override
	public String getIcon()
	{
		return "PK_db";
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(storeTb==null)
		{
			failedr.append("no table set") ;
			return false;
		}
		return storeTb.isValid(failedr);
	}

	@Override
	public JSONObject getParamJO()
	{
		if(storeTb==null)
			return null ;
		return storeTb.toJO();
	}

	@Override
	protected void setParamJO(JSONObject jo)
	{
		if(storeTb==null)
			storeTb = new StoreTb() ;
		storeTb.fromJO(jo) ;
	}

	public StoreTb getStoreTb()
	{
		return this.storeTb ;
	}
	

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		StringBuilder sb = new StringBuilder() ;
		if(storeTb==null || !storeTb.isValid(sb))
			return null ;
		JSONObject pld = msg.getPayloadJO(null) ;
		if(pld==null)
			return null ;
		List<Point> pts = storeTb.RT_transRTDataToInfluxPt(pld) ;
		if(pts==null||pts.size()<=0)
			return null ;
		InfluxDB_M dbm = (InfluxDB_M)this.getOwnRelatedModule() ;
		InfluxDBClient client = dbm.RT_getClient() ;
		WriteApiBlocking wapi = client.getWriteApiBlocking() ;
		long st = System.currentTimeMillis() ;
		wapi.writePoints(pts);
		return null;
	}

}
