package org.iottree.core.store.record;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashSet;
import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.store.tssdb.TSSSavePK;
import org.iottree.core.store.tssdb.TSSTagSegs;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class RecProL1 extends RecPro
{
	

	//HashSet<String> selTagIds = new HashSet<>() ;
	
	private transient List<UATag> fitTags = null ;
	private transient List<UATag> selTags = null ;
	
	
	public abstract List<RecValStyle> getSupportedValStyle() ;
	
	/**
	 * fired by tssdb
	 * @param tagsegs
	 * @return
	 * @throws Exception
	 */
	protected abstract boolean RT_onTagSegsSaved(TSSSavePK savepk) throws Exception;
	

	
//	public HashSet<String> getSelectTagIds()
//	{
//		return this.selTagIds ;
//	}
	

	
	public List<UATag> listFitTags()
	{
		if(this.fitTags!=null)
			return this.fitTags ;
		
		List<RecValStyle> su_vs = this.getSupportedValStyle() ;
		if(su_vs==null||su_vs.size()<=0)
			return null ;
		
		ArrayList<UATag> rets = new ArrayList<>() ;
		for(RecTagParam pm:this.belongTo.getRecTagParams().values())
		{
			RecValStyle rvs = pm.getValStyle() ;
			if(rvs==null)
				continue ;
			if(su_vs.contains(rvs))
				rets.add(pm.getUATag()) ;
		}
		
		this.fitTags = rets ;
		return rets ;
	}
	

	
	public synchronized List<UATag> listSelectedTags()
	{
		if(this.selTags!=null)
			return this.selTags ;
		
		
		this.selTags = this.belongTo.listRecProUsingTags(this.id) ;
		return this.selTags ;
	}
	
	synchronized void clearCache()
	{
		fitTags = null ;
		selTags = null;
	}
	
	
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO() ;
		
		// for list using
		JSONArray jarr = new JSONArray() ;//(this.selTagIds) ;
		for(UATag tag: listSelectedTags())
			jarr.put(tag.getId()) ;
		jo.put("sel_tags", jarr) ;
		
		return jo ;
	}
	
	
	protected boolean fromJO(JSONObject jo,StringBuilder failed)
	{
		if(!super.fromJO(jo, failed))
			return false;
		
//		JSONArray jarr = jo.optJSONArray("sel_tags") ;
//		HashSet<String> tags = new HashSet<>() ;
//		if(jarr!=null)
//		{
//			int n = jarr.length() ;
//			for(int i = 0 ; i < n ; i ++)
//			{
//				tags.add(jarr.getString(i)) ;
//			}
//		}
//		this.selTagIds = tags ;
		return true ;
	}
}
