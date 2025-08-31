package org.iottree.portal;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class PageBlkTxt extends PageBlk
{
	public static final String TP = "txt" ;
	String txt = null ;
	
	boolean bHtml = true ;//
	
	public PageBlkTxt(Page owner,String blkname)
	{
		super(owner,blkname) ;
	}

	@Override
	public String getTP()
	{
		return TP;
	}

	@Override
	public String getTPT()
	{
		return "文本";
	}
	
	public String getTxt()
	{
		if(this.txt==null)
			return "" ;
		return this.txt ;
	}
	
	public boolean isHtml()
	{
		return this.bHtml ;
	}
	
	/**
	 * 运行时输出
	 * @return
	 */
	@Override
	public String RT_getOutTxt()
	{
		if(this.bHtml)
			return getTxt() ;
		else
			return Convert.plainToHtml(this.getTxt());
	}

	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		jo.putOpt("txt",this.txt) ;
		jo.put("bhtml", this.bHtml) ;		
		return jo ;
	}
	
	
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		
		this.txt = jo.optString("txt") ;
		this.bHtml = jo.optBoolean("bhtml",true) ;
		
		return true ;
	}
}
