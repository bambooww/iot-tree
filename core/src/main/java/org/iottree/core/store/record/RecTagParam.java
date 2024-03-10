package org.iottree.core.store.record;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UATag;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.store.tssdb.TSSTagSegs;
import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class RecTagParam implements Comparable<RecTagParam>
{
	RecManager belongTo = null ;
	
	String tagId ;
//	/**
//	 * under prj tag cxt path
//	 */
//	String tag = null ;
	
	boolean bEnable = true ;
	
	UATag tag = null ;
	
	String tagPath = null ;
	/**
	 * 正常运行时，两次采集数据的中间有效的间隔，如果超过此间隔，可以认为是中间有采集中断或异常
	 */
	private long gatherIntv ;
	
	/**
	 * 最小记录间隔——可以减少记录数据条目数
	 */
	long minRecordIntv ;
	
	/**
	 * 对应数据标签的值风格——此风格会影响后续的数据处理方式
	 */
	RecValStyle valStyle ;
	
	/**
	 * 存放天数
	 */
	int keepDays = -1 ;
	
	ArrayList<String> usingProIds = new ArrayList<>() ;
	
	
	
	transient UATag uaTag = null ;
	
	transient TSSTagSegs<?> tssTagSegs = null ;
	
//	private RecTagParam(RecManager belongto,String tagid,long gather_intv,RecValStyle vstyle,int keep_days)
//	{
//		belongTo = belongto ;
//		this.tagId = tagid ;
//		this.gatherIntv = gather_intv ;
//		if(vstyle==null)
//			vstyle = RecValStyle.successive_normal ;
//		this.valStyle = vstyle ;
//		this.keepDays = keep_days ;
//	}
	
	public RecTagParam(RecManager belongto,UATag tag,long gather_intv,RecValStyle vstyle,int keep_days,long min_rec_intv)
	{
		if(vstyle==null)
		{
			if(tag.getValTp()==ValTP.vt_bool)
				vstyle = RecValStyle.discrete ;
			else
				vstyle = RecValStyle.successive_normal ;
		}
		else
		{
			if(tag.getValTp()==ValTP.vt_bool && vstyle!=RecValStyle.discrete)
				throw new IllegalArgumentException("UATag is bool value type which must set Value Style to Discrete") ;
			this.valStyle = vstyle ;
		}
		
		this.belongTo = belongto ;
		this.tagId = tag.getId() ;
		this.tag = tag ;
		this.tagPath = tag.getNodeCxtPathInPrj() ;
		this.gatherIntv = gather_intv ;
		this.keepDays = keep_days ;
		this.minRecordIntv = min_rec_intv ;
	}
	
//	RecTagParam asUATag(UATag tag)
//	{
//		uaTag = tag ;
//		return this ;
//	}
	
	RecTagParam asTSSTagSegs(TSSTagSegs<?> tss_ts)
	{
		tssTagSegs = tss_ts ;
		return this ;
	}
	
	public String getTagId()
	{
		return tagId ;
	}
	

	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public UATag getUATag()
	{
		return tag ;
	}
	
	public String getTagPath()
	{
		return this.tagPath ;
	}
	
	public long getUsingGatherIntv()
	{
		if(gatherIntv<=0)
			return gatherIntv ;
		
		if(minRecordIntv>0 && minRecordIntv*3>gatherIntv)
			return minRecordIntv*3 ;
		else
			return gatherIntv ;
	}
	
	public long getGatherIntv()
	{
		return gatherIntv ;
	}
	
	public RecValStyle getValStyle()
	{
		return valStyle ;
	}
	
	public int getKeepDays()
	{
		return this.keepDays ;
	}
	
	public long getMinRecordIntv()
	{
		return this.minRecordIntv ;
	}
	
	public List<String> getUsingProIds()
	{
		return this.usingProIds ;
	}
	
	synchronized boolean setUsingProId(String proid)
	{
		if(this.usingProIds.contains(proid))
			return false;
		this.usingProIds.add(proid) ;
		return true ;
	}
	synchronized boolean unsetUsingProId(String proid)
	{
		return this.usingProIds.remove(proid) ;
	}
	
	public List<RecProL1> listPros()
	{
//		ArrayList<RecProL1> rets=  new ArrayList<>() ;
//		for(RecPro rp:this.belongTo.getId2RecPro().values())
//		{
//			if(!(rp instanceof RecProL1))
//				continue ;
//			RecProL1 rpl1 = (RecProL1)rp ;
//			if(rpl1.getSelectTagIds().contains(this.tagId))
//				rets.add(rpl1) ;
//		}
//		return rets ;
		if(this.usingProIds==null||this.usingProIds.size()<=0)
			return null;
		
		ArrayList<RecProL1> pros = new ArrayList<>(usingProIds.size()) ;
		for(String pid:usingProIds)
		{
			RecPro rp = belongTo.getRecProById(pid) ;
			if(rp==null)
				continue ;
			if(rp instanceof RecProL1)
				pros.add((RecProL1)rp) ;
		}
		return pros ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("tagid", this.tagId) ;
		jo.put("en", bEnable) ;
		jo.put("gather_intv", this.gatherIntv) ;
		jo.put("val_style", valStyle.getVal()) ;
		jo.put("keep_days", keepDays) ;
		jo.put("min_rec_intv", this.minRecordIntv) ;
		jo.putOpt("using_pros", usingProIds) ;
		return jo ;
	}
	
	public static RecTagParam fromJO(RecManager recm,JSONObject jo)
	{
		String tagid = jo.optString("tagid") ;
		if(Convert.isNullOrEmpty(tagid))
			return null ;
		
		UANode node = UAManager.getInstance().findNodeById(tagid) ;
		if(!(node instanceof UATag))
			return null ;
		UATag tag = (UATag)node ;
		
		long gintv = jo.optLong("gather_intv",-1) ;
		int vsty = jo.optInt("val_style", 0) ;
		RecValStyle vs = RecValStyle.valOfInt(vsty) ;
		int keep_days = jo.optInt("keep_days",-1) ;
		long min_rec_intv = jo.optLong("min_rec_intv",-1) ;
		
		RecTagParam ret = new RecTagParam(recm,tag,gintv,vs,keep_days,min_rec_intv) ;
		ret.bEnable = jo.optBoolean("en",true) ;
		JSONArray jarr = jo.optJSONArray("using_pros") ;
		if(jarr!=null)
		{
			int n = jarr.length();
			for(int i = 0 ; i < n ; i ++)
			{
				String id = jarr.getString(i) ;
				ret.usingProIds.add(id);
			}
		}
		return ret ;
	}

	@Override
	public int compareTo(RecTagParam o)
	{
		return this.tagPath.compareTo(o.tagPath);
	}
}
