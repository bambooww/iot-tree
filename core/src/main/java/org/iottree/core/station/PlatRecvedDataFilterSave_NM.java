package org.iottree.core.station;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.IMNContainer;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeEnd;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNNode.OutResDef;
import org.iottree.core.msgnet.modules.RelationalDB_Table;
import org.iottree.core.msgnet.store.StoreTb;
import org.iottree.core.msgnet.store.influxdb.InfluxDB_M;
import org.iottree.core.msgnet.store.influxdb.InfluxDB_Measurement;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.write.Point;

public class PlatRecvedDataFilterSave_NM  extends MNNodeMid
{
	StoreTb storeTb = null ;
	
	public PlatRecvedDataFilterSave_NM()
	{
	}
	

	@Override
	public int getOutNum()
	{
		return 1;
	}
	

	private static HashMap<Integer,OutResDef> OUT2RES =new HashMap<>() ;
	static
	{
		OUT2RES.put(0,new OutResDef<InfluxDB_Measurement>(InfluxDB_Measurement.class,false)) ;
	}
	
	@Override
	public Map<Integer,OutResDef> getOut2Res()
	{
		return OUT2RES ;
	}


	@Override
	public String getTP()
	{
		return "recved_filter_save";
	}

	@Override
	public String getTPTitle()
	{
		return g("recved_filter_save");//"过滤保存接收站点数据";
	}

	@Override
	public String getColor()
	{
		return "#1d90ad";
	}

	@Override
	public String getIcon()
	{
		return "\\uf148";
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
	

	private UAPrj getPrj()
	{
		IMNContainer mnc = this.getBelongTo().getBelongTo().getBelongTo();
		if (mnc == null || !(mnc instanceof UAPrj))
			return null;

		return (UAPrj) mnc;
	}
	

	@Override
	protected void setParamJO(JSONObject jo)
	{
		if(storeTb==null)
		{
			UAPrj prj = this.getPrj() ;
			storeTb = new StoreTb(prj) ;
		}
		storeTb.fromJO(jo) ;
	}

	public StoreTb getStoreTb()
	{
		return this.storeTb ;
	}
	
	private InfluxDB_M getInfluxDB_M()
	{
		MNNodeRes noderes = this.getOutResNode(0) ;
		if(noderes==null)
			return null;
		if(!(noderes instanceof InfluxDB_Measurement))
		{
			return null ;
		}
		
		InfluxDB_Measurement mt = (InfluxDB_Measurement)noderes ;
		
		return (InfluxDB_M)mt.getOwnRelatedModule() ;
	}

	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		InfluxDB_M dbm =getInfluxDB_M() ;
		if(dbm==null)
		{
			RT_DEBUG_ERR.fire("plat_recv_fs", "No infulxdb module found,it may has no measurement res node set from InfluxDB_M");
			return null ;
		}
		StringBuilder sb = new StringBuilder() ;
		if(storeTb==null || !storeTb.isValid(sb))
			return null ;
		JSONObject pld = msg.getPayloadJO(null) ;
		if(pld==null)
			return null ;
		List<Point> pts = storeTb.RT_transRTDataToInfluxPt(pld) ;
		if(pts==null||pts.size()<=0)
			return null ;
		
		InfluxDBClient client = dbm.RT_getClient() ;
		WriteApiBlocking wapi = client.getWriteApiBlocking() ;
		long st = System.currentTimeMillis() ;
		wapi.writePoints(pts);
		return null;
	}

}
