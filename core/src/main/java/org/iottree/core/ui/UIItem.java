package org.iottree.core.ui;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * 通过后端管理员手工定义的UI
 * 
 * @author jason.zhu
 *
 */
public class UIItem
{
	UIManager uiMgr = null ;
	
	String tempN = null ;
	
	String id = null ;
	
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	ArrayList<String> tagIds = new ArrayList<>() ;
	
	public UIItem(UIManager uim,String tempn,String name,String title,List<String> tagids)
	{
		this.uiMgr = uim ;
		this.id = IdCreator.newSeqId() ;
		this.tempN = tempn ;
		this.name = name ;
		this.title = title ;
		this.tagIds.addAll(tagids) ;
	}
	
	private UIItem(UIManager uim)
	{
		this.uiMgr = uim ;
	}
	
	public String getTempName()
	{
		return this.tempN ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public String getName()
	{
		return name ;
	}
	
	public String getTitle()
	{
		if(Convert.isNullOrEmpty(this.title))
			return "" ;
		return title ;
	}
	
	public String getDesc()
	{
		if(Convert.isNullOrEmpty(this.desc))
			return "" ;
		return this.desc;
	}
	
	public List<String> getTagIds()
	{
		return tagIds ;
	}
	
	public IUITemp getUITemp()
	{
		return uiMgr.getTempByName(this.tempN) ;
	}
	

	public String getUrl()
	{
		return getUITemp().calUrl(this.tagIds) ;
	}
	
	public int getWidth()
	{
		return getUITemp().getWidth() ;
	}
	
	public int getHeight()
	{
		return getUITemp().getHeight() ;
	}
	
	public String getIconUrl()
	{
		return getUITemp().getIconUrl() ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", this.id) ;
		jo.put("n", this.name);
		jo.putOpt("t", this.title) ;
		jo.putOpt("d",this.desc) ;
		jo.put("temp", tempN) ;
		jo.putOpt("tagids",this.tagIds) ;
		return jo ;
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = toJO() ;
		IUITemp uit = this.getUITemp() ;
		JSONObject uitjo = null;
		if(uit!=null)
			uitjo = uit.toJO() ;
		jo.putOpt("icon", this.getIconUrl()) ;
		jo.putOpt("ui_temp", uitjo) ;
		
		return jo ;
	}
	
	public static UIItem fromJO(UIManager uim,JSONObject jo) //,StringBuilder failedr)
	{
		UIItem ret = new UIItem(uim) ;
		
		ret.id = jo.getString("id") ;
		ret.name = jo.getString("n") ;
		ret.title = jo.optString("t") ;
		ret.desc = jo.optString("d") ;
		ret.tempN = jo.getString("temp") ;
		JSONArray jarr = jo.optJSONArray("tagids");
		if(jarr!=null)
		{
			int len = jarr.length() ;
			for(int i = 0 ; i < len ; i ++)
			{
				String tagid = jarr.getString(i) ;
				ret.tagIds.add(tagid) ;
			}
		}
		return ret ;
	}
}
