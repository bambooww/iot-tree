package org.iottree.core.router;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.router.RouterInnCollator.OutStyle;
import org.iottree.core.router.RouterInnCollator.TagVal;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

public class RICSelTags extends RouterInnCollator
{
	public static final String TP="sel_tags";
	
	
	ArrayList<UATag> rtOutTags = new ArrayList<>() ;
	
	/**
	 * for write tag
	 */
	ArrayList<UATag> rtInWriteTags = new ArrayList<>() ;
	
	OutStyle outSty = OutStyle.interval ;
	
	public RICSelTags(RouterManager rm)
	{
		super(rm);
	}
	
	@Override
	public String getTp()
	{
		return TP;
	}
	
	protected RouterInnCollator newInstance(RouterManager rm)
	{
		return new RICSelTags(rm) ;
	}

	public OutStyle getOutStyle()
	{
		return outSty ;
	}

	public List<UATag> getRTOutTags()
	{
		return this.rtOutTags ;
	}
	
	public void setRTOutTagsByIds(List<String> ids)
	{
		ArrayList<UATag> tags = new ArrayList<>() ;
		for(String id:ids)
		{
			UATag tag = this.belongPrj.findTagById(id) ;
			if(tag==null)
				continue ;
			tags.add(tag) ;
		}
		this.rtOutTags = tags ;
	}
	
	public List<UATag> getRTInWriteTags()
	{
		return this.rtInWriteTags ;
	}
	
	public void setRTInWriteTagsByIds(List<String> ids)
	{
		ArrayList<UATag> tags = new ArrayList<>() ;
		for(String id:ids)
		{
			UATag tag = this.belongPrj.findTagById(id) ;
			if(tag==null)
				continue ;
			if(!tag.isCanWrite())
				continue ;
			tags.add(tag) ;
		}
		this.rtInWriteTags = tags ;
	}
	
	private List<JoinOut> jouts = Arrays.asList(
			new JoinOut(this,"sel_tags_out") //,false,true)
			) ;
	
	private List<JoinIn> jinws = Arrays.asList(
			new JoinIn(this,"sel_tags_in") //,false,true)
			) ;
	
	@Override
	public List<JoinIn> getJoinInList()
	{
		if(rtInWriteTags==null||rtInWriteTags.size()<=0)
			return null ;
		return jinws ;
	}
	
	@Override
	public List<JoinOut> getJoinOutList()
	{
		if(rtOutTags==null||rtOutTags.size()<=0)
			return null ;
		return jouts ;
	}
	
	/**
	 * override by sub
	 */
	@Override
	protected void RT_runInIntvLoop()
	{
		
	}
	
	/**
	 * override by sub
	 */
	@Override
	protected void RT_runOnChgTagVal(TagVal tv)
	{
		
	}
	
	public String pullOut(String join_out_name) throws Exception
	{
		switch(join_out_name)
		{
		case "out":
			return this.belongTo.belongTo.JS_get_rt_json_flat();
		default:
			return null ;
		}
	}
	
	@Override
	protected void RT_onRecvedFromJoinIn(JoinIn ji,String recved_txt)
	{
		
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		
		jo.put("out_sty", this.outSty.getInt()) ;
		JSONArray jarr = new JSONArray() ;
		if(rtOutTags!=null)
		{
			for(UATag tag:rtOutTags)
			{
				jarr.put(tag.getId()) ;
			}
		}
		jo.put("out_tagids", jarr) ;
		
		jarr = new JSONArray() ;
		if(this.rtInWriteTags!=null)
		{
			for(UATag tag:rtInWriteTags)
			{
				jarr.put(tag.getId()) ;
			}
		}
		jo.put("in_tagids", jarr) ;
		return jo ;
	}
	

	protected boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		super.fromJO(jo,failedr);
		
		
		this.outSty = OutStyle.valOfInt(jo.optInt("out_sty",0)) ;
		JSONArray jarr = jo.getJSONArray("out_tagids") ;
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
				setRTOutTagsByIds(ids) ;
			}
		}
		
		jarr = jo.getJSONArray("in_tagids") ;
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
				setRTInWriteTagsByIds(ids) ;
			}
		}
		return true ;
	}

	
}
