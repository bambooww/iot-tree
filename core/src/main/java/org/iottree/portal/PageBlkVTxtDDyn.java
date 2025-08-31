package org.iottree.portal;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * 内容静态，数据动态更新
 * @author zzj
 *
 */
public class PageBlkVTxtDDyn extends PageBlk
{
	public static final String TP = "vtxt_ddyn" ;
	String vTxt = null ;
	
	/**
	 * 动态数据源唯一id
	 */
	String ddynUID = null ;
	
	String ddynURL = null ; //通过url方式获取数据
	
	public PageBlkVTxtDDyn(Page owner,String blkname)
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
		return "显示静态数据动态";
	}
	
	public String getVTxt()
	{
		return this.vTxt ;
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
		return jo ;
	}
	
	
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		
		this.vTxt = jo.optString("vtxt") ;
		
		return true ;
	}
}
