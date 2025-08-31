package org.iottree.portal;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * 内容静态，数据动态更新
 * @author zzj
 *
 */
public class PageBlkVTxtDUrl extends PageBlk
{
	public static final String TP = "vtxt_durl" ;
	
	String vTxt = null ;

	String ddynURL = null ; //通过url方式获取数据
	
	public PageBlkVTxtDUrl(Page owner,String blkname)
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
		return "显示静态URL动态";
	}
	
	public String getVTxt()
	{
		if(this.vTxt==null)
			return "" ;
		return this.vTxt ;
	}
	
	public String getDDynURL()
	{
		if(this.ddynURL==null)
			return "" ;
		return this.ddynURL;
	}
	/**
	 * 运行时输出
	 * @return
	 */
	@Override
	public String RT_getOutTxt()
	{
		return vTxt;
	}

	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		jo.putOpt("vtxt",this.vTxt) ;
		jo.putOpt("ddyn_url",this.ddynURL) ;
		return jo ;
	}
	
	
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		
		this.vTxt = jo.optString("vtxt") ;
		this.ddynURL = jo.optString("ddyn_url") ;
		return true ;
	}
}
