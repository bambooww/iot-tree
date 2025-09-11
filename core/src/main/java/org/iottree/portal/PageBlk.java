package org.iottree.portal;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * 页面中的区块设置定义
 * 
 * @author zzj
 */
public abstract class PageBlk
{
	Page owner ;
	
	String blkName ;
	
	//HashMap<String,String> params = null ;
	
	
	public PageBlk(Page owner,String blkname)
	{
		this.owner = owner ;
		this.blkName = blkname ;
	}
	
	public abstract String getTP() ;
	
	public abstract String getTPT() ;

	public Page getOwner()
	{
		return this.owner ;
	}
	
	public String getBlkName()
	{
		return this.blkName ;
	}
	
	/**
	 * 设置时输出
	 * @return
	 */
	public String getSetupOutTitle()
	{
		return "" ;
	}
	
	/**
	 * 运行时输出
	 * @return
	 */
	public abstract String RT_getOutTxt();
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("_tp", this.getTP()) ;
		jo.put("blk_n",this.blkName) ;
		
		return jo ;
	}
	
	
	public boolean fromJO(JSONObject jo)
	{
//		this.blkName = jo.optString("blk_n") ;
//		if(Convert.isNullOrEmpty(this.blkName))
//			return false;
		return true ;
	}
	
	static List<PageBlk> ALL_TPS = null;
	public static List<PageBlk> getAllTPs()
	{
		if(ALL_TPS!=null)
			return ALL_TPS ;
		return ALL_TPS= Arrays.asList(new PageBlkTxt(null,null),new PageBlkLet(null,null),new PageBlkVTxtDDyn(null,null),new PageBlkVTxtDUrl(null,null)) ;
	}

	static PageBlk transFromJO(Page p,JSONObject jo)
	{
		String tp = jo.optString("_tp") ;
		String blk_n = jo.optString("blk_n") ;
		return transFromJO(p,blk_n,tp,jo) ;
	}
	
	static PageBlk transFromJO(Page p,String blk_n,String pblk_tp,JSONObject jo)
	{
		//String tp = jo.optString("_tp") ;
		
		if(pblk_tp==null || Convert.isNullOrEmpty(blk_n))
			return null ;
		PageBlk ret = null ;
		switch(pblk_tp)
		{
		case PageBlkTxt.TP:
			ret = new PageBlkTxt(p,blk_n) ;
			break ;
		case PageBlkLet.TP:
			ret = new PageBlkLet(p,blk_n) ;
			break ;
		case PageBlkVTxtDUrl.TP:
			ret = new PageBlkVTxtDUrl(p,blk_n) ;
			break ;
		case PageBlkVTxtDDyn.TP:
			ret = new PageBlkVTxtDDyn(p,blk_n) ;
			break ;
		default:
			return null ;
		}
		
		if(ret.fromJO(jo))
			return ret ;
		else
			return null ;
	}
}
