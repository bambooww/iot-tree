package org.iottree.core.router;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

public class DataPackerSelTags extends DataPacker
{
	public static final String TP="sel_tags";
	
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	ArrayList<UATag> selectedTags = new ArrayList<>() ;
	
	public DataPackerSelTags(RouterManager rm)
	{
		super(rm);
	}
	
	@Override
	public String getName()
	{
		return name;
	}

	@Override
	public String getTitle()
	{
		if(this.title==null)
			return "" ;
		return title;
	}

	@Override
	public String getDesc()
	{
		return desc;
	}

	@Override
	public String getTp()
	{
		return TP;
	}

	@Override
	public String getPackData() throws Exception
	{
		return null;
	}

	public List<UATag> getSelectedTags()
	{
		return this.selectedTags ;
	}
	
	public void setSelectedTagsByIds(List<String> ids)
	{
		ArrayList<UATag> tags = new ArrayList<>() ;
		for(String id:ids)
		{
			UATag tag = this.belongPrj.findTagById(id) ;
			if(tag==null)
				continue ;
			tags.add(tag) ;
		}
		this.selectedTags = tags ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = this.toJO() ;
		jo.putOpt("t", this.title) ;
		JSONArray jarr = new JSONArray() ;
		for(UATag tag:selectedTags)
		{
			jarr.put(tag.getId()) ;
		}
		jo.put("sel_tagids", jarr) ;
		return jo ;
	}
	

	protected boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		super.fromJO(jo,failedr);
		
		this.title = jo.optString("t") ;
		JSONArray jarr = jo.getJSONArray("sel_tagids") ;
		if(jarr!=null)
		{
			ArrayList<String> ids = new ArrayList<>() ;
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				String tmpid = jarr.getString(i) ;
				if(Convert.isNullOrEmpty(tmpid))
					continue ;
				ids.add(tmpid) ;
				setSelectedTagsByIds(ids) ;
			}
		}
		return true ;
	}

	
}
