package org.iottree.core.msgnet.nodes;

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

public class NM_TagFilter2TSDB  extends MNNodeMid implements IMNTagFilter
{
	/**
	 * all tagpath under cxtNodePath 
	 */
	private boolean bAllNorTag = false;
	
	/**
	 * fixed selected tagpath under cxtNodePath
	 */
	ArrayList<String> tagPaths = null ;
	
	public NM_TagFilter2TSDB()
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
		return "tag_filter_tsdb";
	}

	@Override
	public String getTPTitle()
	{
		return g("tag_filter_tsdb");//"过滤保存接收站点数据";
	}

	@Override
	public String getColor()
	{
		return "#a1cbde";
	}
	
	@Override
	public String getIcon()
	{
		return "\\uf02c";
	}
	
	@Override
	public boolean isParamReady(StringBuilder failedr)
	{
		if(!this.bAllNorTag && (tagPaths==null ||tagPaths.size()<=0))
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
		jo.putOpt("all_nor_tag", this.bAllNorTag) ;
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
		this.bAllNorTag = jo.optBoolean("all_nor_tag",false) ;
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
	
	public boolean isAllNorTag()
	{
		return this.bAllNorTag ;
	}
	
	public ArrayList<String> getTagPaths()
	{
		return this.tagPaths ;
	}
	
	@Override
	public List<UATag> getFilterTags()
	{
		UAPrj uprj = this.getPrj() ;
		if(uprj==null)
			return null ;
		
		ArrayList<UATag> rets = new ArrayList<>() ;
		if(bAllNorTag)
		{
			rets.addAll(uprj.listTagsNorAll()) ;
			
			if(this.tagPaths!=null||this.tagPaths.size()>0)
			{ //only sys tag
				for(String tagp:this.tagPaths)
				{
					UATag tag = uprj.getTagByPath(tagp) ;
					if(tag==null)
						continue ;
					if(!tag.isSysTag())
						continue ;
					rets.add(tag) ;
				}
			}
		}
		else
		{
			if(this.tagPaths==null||this.tagPaths.size()<=0)
				return null ;
			
			for(String tagp:this.tagPaths)
			{
				UATag tag = uprj.getTagByPath(tagp) ;
				if(tag==null)
					continue ;
				rets.add(tag) ;
			}
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
			RT_DEBUG_ERR.fire("tag_filter_tsdb", "No InfluxDB_Measurement found,it may has no measurement res node set from InfluxDB_M");
			return null ;
		}
		String tablen = mt.getMeasurement() ;
		if(Convert.isNullOrEmpty(tablen))
		{
			RT_DEBUG_ERR.fire("tag_filter_tsdb", "InfluxDB_Measurement has no measurement name set");
			return null ;
		}
		InfluxDB_M dbm =getInfluxDB_M() ;
		if(dbm==null)
		{
			RT_DEBUG_ERR.fire("tag_filter_tsdb", "No infulxdb module found,it may has no measurement res node set from InfluxDB_M");
			return null ;
		}
		//StringBuilder sb = new StringBuilder() ;
		
		List<Point> pts = RT_getRTDataToInfluxPt(tablen) ;
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

	public  List<Point> RT_getRTDataToInfluxPt(String tablen)
	{
		UAPrj uprj = this.getPrj() ;
		if(uprj==null)
			return  null;
		
		ArrayList<Point> rets = new ArrayList<>() ;
		
		if(bAllNorTag)
		{
			for(UATag nortag:uprj.listTagsNorAll())
			{
				Point pt = calTagPoint(tablen,nortag,nortag.getNodeCxtPathInPrj()) ;
				if(pt==null)
					continue ;
				rets.add(pt) ;
			}
			
			if(this.tagPaths!=null||this.tagPaths.size()>0)
			{ //only sys tag
				for(String tagp:this.tagPaths)
				{
					UATag tag = uprj.getTagByPath(tagp) ;
					if(tag==null)
						continue ;
					if(!tag.isSysTag())
						continue ;
					Point pt = calTagPoint(tablen,tag,tagp) ;
					if(pt==null)
						continue ;
					rets.add(pt) ;
				}
			}
		}
		else
		{
			if(this.tagPaths==null||this.tagPaths.size()<=0)
				return null ;
			
			for(String tagp:this.tagPaths)
			{
				UATag tag = uprj.getTagByPath(tagp) ;
				if(tag==null)
					continue ;
				
				Point pt = calTagPoint(tablen,tag,tagp) ;
				if(pt==null)
					continue ;
				rets.add(pt) ;
			}
		}
		
		return rets ;
	}
	
	private Point calTagPoint(String tablen,UATag tag,String tagp)
	{
		UAVal val = tag.RT_getVal() ;
		if(val==null)
			return null;
		if(!val.isValid())
			return null ;
		
		Object v = val.getObjVal() ;
		if(v==null)
			return null ;
		String m = tablen ;
		Point point = Point.measurement(m);
		long ts = val.getValDT() ;
		if(ts<=0)
			return null ;
		
		point.time(ts,WritePrecision.MS);
		String fn = tagp ;
		ValTP vtp = tag.getValTp() ;
		
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
		divblks.add(new DivBlk("tag_filter_tsdb",divsb.toString())) ;
		
		super.RT_renderDiv(divblks);
	}
}
