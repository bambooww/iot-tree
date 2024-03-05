package org.iottree.core.store.record;

import org.iottree.core.UATag;
import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class RecTagParam
{
	/**
	 * under prj tag cxt path
	 */
	String tag = null ;
	
	/**
	 * 正常运行时，两次采集数据的中间有效的间隔，如果超过此间隔，可以认为是中间有采集中断或异常
	 */
	long gatherIntv ;
	
	/**
	 * 对应数据标签的值风格——此风格会影响后续的数据处理方式
	 */
	RecValStyle valStyle ;
	
	public RecTagParam(String tag,long gather_intv,RecValStyle vstyle)
	{
		this.tag = tag ;
		this.gatherIntv = gather_intv ;
		if(vstyle==null)
			vstyle = RecValStyle.successive_normal ;
		this.valStyle = vstyle ;
	}
	
	public RecTagParam(UATag tag,long gather_intv,RecValStyle vstyle)
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
		
		this.tag = tag.getNodeCxtPathInPrj() ;
		this.gatherIntv = gather_intv ;
	}
	
	public String getTag()
	{
		return tag ;
	}
	
	public long getGatherIntv()
	{
		return gatherIntv ;
	}
	
	public RecValStyle getValStyle()
	{
		return valStyle ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("tag", this.tag) ;
		jo.put("gather_intv", this.gatherIntv) ;
		jo.put("val_style", valStyle.getVal()) ;
		return jo ;
	}
	
	public static RecTagParam fromJO(JSONObject jo)
	{
		String tag = jo.optString("tag") ;
		if(Convert.isNullOrEmpty(tag))
			return null ;
		
		long gintv = jo.optLong("gather_intv",-1) ;
		int vsty = jo.optInt("val_style", 0) ;
		RecValStyle vs = RecValStyle.valOfInt(vsty) ;
		return new RecTagParam(tag,gintv,vs) ;
	}
}
