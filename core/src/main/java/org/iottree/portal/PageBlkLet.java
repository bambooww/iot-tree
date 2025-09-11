package org.iottree.portal;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * Portlet 
 * 
 * content is load by ajax
 * @author jason.zhu
 *
 */
public class PageBlkLet extends PageBlk
{
	public static final String TP = "portlet" ;
	
	String url = null ;
	
	public PageBlkLet(Page owner,String blkname)
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
		return "Portlet";
	}
	
	public String getUrl()
	{
		if(this.url==null)
			return "" ;
		return this.url ;
	}
	
	/**
	 * 运行时输出
	 * @return
	 */
	@Override
	public String RT_getOutTxt()
	{
		String tmps = "<div class='_portlet' id='blk_"+this.getBlkName()+"' _let_url='"+this.url+"'></div>" ;
		return tmps ;
	}

	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		jo.putOpt("url",this.url) ;
		return jo ;
	}
	
	
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		
		this.url = jo.optString("url") ;
		
		return true ;
	}
}

