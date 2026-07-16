package org.iottree.core.devtree;

import org.iottree.core.UAManager;
import org.iottree.core.UANode;
import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * belong to runblk instance,config in device tree
 * 
 * @author jason.zhu
 *
 */
public class DTRunTag
{
	DTNode owner ;
	
	String tagPath ;// tag full path with prjn
	
	boolean showInTree = false;
	
	private transient UATag tag = null ;
	
	public DTRunTag(DTNode nd,String tagp)
	{
		this.owner = nd ;
		this.tagPath = tagp ;
	}
	
	public DTRunTag(DTNode owner,DTRunTag oth)
	{
		this.owner = owner ;
		this.tagPath = oth.tagPath ;
		this.showInTree = oth.showInTree ;
	}
	
	public DTNode getOwner()
	{
		return this.owner ;
	}
	
	public String getTagPath()
	{
		return this.tagPath ;
	}
	
	public DTRunTag asShowInTree(boolean b)
	{
		this.showInTree = b ;
		return this ;
	}
	
	public UAPrj getPrj()
	{
		UATag t = getTag();
		if(t==null)
			return null ;
		return t.getBelongToPrj() ;
	}
	
	public UATag getTag()
	{
		if(tag!=null)
			return tag ;
		UANode ua = UAManager.getInstance().findNodeByPath(this.tagPath) ;
		if(ua==null || !(ua instanceof UATag))
			return null ;
		return tag = (UATag)ua;
	}
	
	public JSONObject toJO(boolean b_show_detail)
	{
		JSONObject ret = new JSONObject().put("tagp", this.tagPath) ;
		if(this.showInTree)
			ret.put("show_in_tree", true) ;
		
		if(b_show_detail)
		{
			UATag tag = this.getTag() ;
			if(tag!=null)
			{
				ret.put("tagt", tag.getNodePathTitle());
				ret.put("tagvt", tag.getValTp().getStr()) ;
			}
		}
		return ret ;
	}
	
	public static DTRunTag formJO(DTNode nd,JSONObject jo)
	{
		String tagp = jo.optString("tagp") ;
		if(Convert.isNullOrEmpty(tagp))
			return null ;
		return new DTRunTag(nd,tagp).asShowInTree(jo.optBoolean("show_in_tree",false)) ;
	}
}
