package org.iottree.core.station;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.UANode;
import org.iottree.core.UANodeOCTagsCxt;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.UAVal;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.msgnet.IMNContainer;
import org.iottree.core.msgnet.IMNTagFilter;
import org.iottree.core.msgnet.MNConn;
import org.iottree.core.msgnet.MNMsg;
import org.iottree.core.msgnet.MNNodeMid;
import org.iottree.core.msgnet.MNNodeRes;
import org.iottree.core.msgnet.RTOut;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.msgnet.store.influxdb.InfluxDB_M;
import org.iottree.core.msgnet.store.influxdb.InfluxDB_Measurement;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

import com.influxdb.client.InfluxDBClient;
import com.influxdb.client.WriteApiBlocking;
import com.influxdb.client.domain.WritePrecision;
import com.influxdb.client.write.Point;

public class PlatRecvedDataFilterSave_NM  extends MNNodeMid implements IMNTagFilter
{
	/**
	 * all tagpath under cxtNodePath 
	 */
	boolean bAllTagPaths = false;
	
	/**
	 * fixed selected tagpath under cxtNodePath
	 */
	ArrayList<String> tagPaths = null ;
	
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
		OUT2RES.put(0,new OutResDef(InfluxDB_Measurement.class,false)) ;
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
	public boolean isFitForPrj(UAPrj prj)
	{
		if(prj==null)
			return false;
		return prj.isPrjPStationIns() ;
	}

	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(!this.bAllTagPaths && (tagPaths==null ||tagPaths.size()<=0))
		{
			failedr.append("no Sub Tags set") ;
			return false;
		}
		return true;
	}

	@Override
	public JSONObject getParamJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.putOpt("all_tag_paths", this.bAllTagPaths) ;
		jo.putOpt("tag_paths", this.tagPaths) ;
		
		return jo ;
	}
	

//	private UAPrj getPrj()
//	{
//		IMNContainer mnc = this.getBelongTo().getBelongTo().getBelongTo();
//		if (mnc == null || !(mnc instanceof UAPrj))
//			return null;
//
//		return (UAPrj) mnc;
//	}
	

	@Override
	protected void setParamJO(JSONObject jo)
	{
		this.bAllTagPaths = jo.optBoolean("all_tag_paths",false) ;
		JSONArray jarr = jo.optJSONArray("tag_paths") ;
		if(jarr!=null)
		{
			ArrayList<String> subts = new ArrayList<>() ;
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
				subts.add(jarr.getString(i)) ;
			this.tagPaths = subts ;
		}
	}
	
	public boolean isAllTagPaths()
	{
		return this.bAllTagPaths ;
	}
	
	public ArrayList<String> getTagPaths()
	{
		return this.tagPaths ;
	}

	@Override
	public List<UATag> getFilterTags()
	{
		if(this.tagPaths==null)
			return null ;
		UAPrj prj = this.getPrj() ;
		if(prj==null)
			return null ;
		ArrayList<UATag> rets = new ArrayList<>() ;
		for(String p:this.tagPaths)
		{
			UATag t = prj.getTagByPath(p) ;
			if(t==null)
				continue ;
			rets.add(t) ;
		}
		return rets;
	}
	
	public InfluxDB_Measurement getInfluxDB_Measurement()
	{
		MNNodeRes noderes = this.getOutResNode(0) ;
		if(noderes==null)
			return null;
		if(!(noderes instanceof InfluxDB_Measurement))
		{
			return null ;
		}
		
		return (InfluxDB_Measurement)noderes ;
	}

	private InfluxDB_M getInfluxDB_M()
	{
		InfluxDB_Measurement mt = getInfluxDB_Measurement() ;
		if(mt==null)
			return null ;
		return (InfluxDB_M)mt.getOwnRelatedModule() ;
	}

	private transient List<Point> lastWritePts = null ;
	
	@Override
	protected RTOut RT_onMsgIn(MNConn in_conn, MNMsg msg) throws Exception
	{
		InfluxDB_Measurement mt = getInfluxDB_Measurement() ;
		if(mt==null)
		{
			RT_DEBUG_ERR.fire("plat_recv_fs", "No InfluxDB_Measurement found,it may has no measurement res node set from InfluxDB_M");
			return null ;
		}
		String tablen = mt.getMeasurement() ;
		if(Convert.isNullOrEmpty(tablen))
		{
			RT_DEBUG_ERR.fire("plat_recv_fs", "InfluxDB_Measurement has no measurement name set");
			return null ;
		}
		InfluxDB_M dbm =getInfluxDB_M() ;
		if(dbm==null)
		{
			RT_DEBUG_ERR.fire("plat_recv_fs", "No infulxdb module found,it may has no measurement res node set from InfluxDB_M");
			return null ;
		}
		//StringBuilder sb = new StringBuilder() ;
		
		JSONObject pld = msg.getPayloadJO(null) ;
		if(pld==null)
			return null ;
		List<Point> pts = RT_transRTDataToInfluxPt(tablen,pld) ;
		if(pts==null||pts.size()<=0)
			return null ;
		
		lastWritePts = pts;
		mt.RT_writePoints(pts) ;
//		InfluxDBClient client = dbm.RT_getClient() ;
//		WriteApiBlocking wapi = client.getWriteApiBlocking() ;
//		//long st = System.currentTimeMillis() ;
//		wapi.writePoints(pts);
		return null;
	}

	public  List<Point> RT_transRTDataToInfluxPt(String tablen,JSONObject rtdata)
	{
		UANodeOCTagsCxt gcxt = this.getPrj() ;
		if(gcxt==null)
			return  null;
		
		JSONObject curn = rtdata ;
		if(this.tagPaths==null||this.tagPaths.size()<=0)
			return null ;
		
		//find cxt node
		ArrayList<Point> rets = new ArrayList<>() ;
		for(String tagsubp:this.tagPaths)
		{
			List<String> subps = Convert.splitStrWith(tagsubp, "/.") ;
			if(subps==null||subps.size()<=0)
				continue ;
			JSONObject tagjo = RT_getSubTagBySubPath(curn,subps) ;
			if(tagjo==null)
				continue ;
			
			UANode tagn = gcxt.getDescendantNodeByPath(subps) ;
			if(tagn==null || !(tagn instanceof UATag))
				continue ;
			UATag tag = (UATag)tagn ;
			
			Point pt = calTagPoint(tablen,tag,subps,tagjo) ;
			if(pt==null)
				continue ;
			rets.add(pt) ;
		}
		
		return rets ;
	}
	
	private JSONObject RT_getSubTagBySubPath(JSONObject curn,List<String> subpaths)
	{
		int n ;
		if(subpaths==null||(n=subpaths.size())<=0)
			return null ;
		for(int i = 0 ; i < n - 1 ; i ++)
		{
			curn = RT_getSubNodeByName(curn,subpaths.get(i)) ;
			if(curn==null)
				return null ;
		}
		
		JSONArray jarr = curn.optJSONArray("tags") ;
		if(jarr==null)
			return null ;
		String tagn = subpaths.get(n-1) ;
		n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject tjo= jarr.getJSONObject(i) ;
			if(tagn.equals(tjo.optString("n")))
				return tjo ;
		}
		return null ;
	}
	
	private JSONObject RT_getSubNodeByName(JSONObject curn,String n)
	{
		JSONArray jarr = curn.getJSONArray("subs") ;
		if(jarr==null)
			return null ;
		int num = jarr.length() ;
		for(int i = 0 ; i < num ; i ++)
		{
			JSONObject tmpjo = jarr.getJSONObject(i) ;
			if(n.equals(tmpjo.optString("n")))
				return tmpjo ;
		}
		return null ;
	}
	
	private Point calTagPoint(String tablen,UATag tag,List<String> subps,JSONObject tagjo)
	{
		boolean valid = tagjo.optBoolean("valid",false) ;
		if(!valid)
			return null ;
		String m = tablen ;
		Point point = Point.measurement(m);
		long ts = tagjo.optLong("dt",-1) ;
		if(ts<=0)
			return null ;
		String strv = tagjo.optString("strv") ;
		if(strv==null)
			return null ;
		point.time(ts,WritePrecision.MS);
		
		//System.out.println(Convert.toFullYMDHMS(new Date(ts))) ;
		String fn = Convert.combineStrWith(subps, '.') ;
		
		ValTP vtp = tag.getValTp() ;
		Object v = UAVal.transStr2ObjVal(vtp, strv) ;
		
		if(v instanceof Number)
			point.addField(fn,(Number)v) ;
		else if(v instanceof String)
			point.addField(fn,(String)v) ;
		else if(v instanceof Boolean)
			point.addField(fn,(Boolean)v) ;
		else // if(v==null)
			point.addField(fn, (Number)null) ;
		
		return point ;
	}
	
	@Override
	protected void RT_renderDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder() ;
		divsb.append("<div class='rt_blk'>") ;
		if(lastWritePts!=null)
			divsb.append(" last write influxdb pts="+lastWritePts.size()) ;
		divsb.append("</div>") ;
		divblks.add(new DivBlk("station_recved_fsave",divsb.toString())) ;
		
		super.RT_renderDiv(divblks);
	}


}
