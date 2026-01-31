package org.iottree.ext.grpc;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.Map;

import org.iottree.core.*;
import org.iottree.ext.grpc.RtSyn.TagSynVal;
import org.iottree.ext.grpc.RtSyn.TagSynVals;

public class TagsBuffer
{
	public static class TagItem
	{
		public String path ;
		
		public UATag tag ;
		
		public long lastUpDT = -1 ;
		
		public TagItem(String path,UATag tag)
		{
			this.path = path ;
			this.tag = tag ;
		}
		
		
	}
	String clientId ;
	
	HashSet<String> tagPaths ;
	
	boolean _bNew = true ;//
	
	private transient HashMap<String,TagItem> path2tag = null ;
	
	public TagsBuffer(String clientid,HashSet<String> tagpaths)
	{
		this.clientId = clientid ;
		this.tagPaths = tagpaths ;
	}
	
	public String getClientId()
	{
		return this.clientId ;
	}
	
	public HashSet<String> getTagPaths()
	{
		return this.tagPaths ;
	}
	
	public HashMap<String,TagItem> getPath2Tag()
	{
		if(path2tag!=null)
			return this.path2tag ;
		
		HashMap<String,TagItem> p2t = new HashMap<>() ;
		for(String p:this.tagPaths)
		{
			UANode nd=	UAManager.getInstance().findNodeByPath(p) ;
			if(nd==null || !(nd instanceof UATag))
				continue ;
			TagItem ti = new TagItem(p,(UATag)nd);
			p2t.put(p,ti) ;
		}
		return this.path2tag = p2t;
	}
	
	/**
	 * 
	 * @param min_dt if tag updt<min_dt,it will be out
	 * @return
	 */
	public List<TagItem> filterUpdatedTagItem(boolean up_all)
	{
		ArrayList<TagItem> rets = new ArrayList<>() ;
		//long dt = System.currentTimeMillis() ;
		HashMap<String,TagItem> p2ti = getPath2Tag() ;
		if(up_all)
		{
			rets.addAll(p2ti.values()) ;
			return rets ;
		}
		
		for(TagItem ti:p2ti.values())
		{
			UAVal val = ti.tag.RT_getVal() ;
			if(val==null)
				continue ;
			long valdt = val.getValDT() ;
			if(valdt==ti.lastUpDT)
				continue ;
			rets.add(ti) ;
			ti.lastUpDT = valdt;
		}
		return rets ;
	}
	
	
	public TagSynVals filterUpdatedTagSynVals(boolean up_all)
	{
		TagSynVals.Builder retb =  TagSynVals.newBuilder() ;
		List<TagItem> tis = filterUpdatedTagItem(up_all) ;
		for(TagItem ti:tis)
		{
			String path = ti.path;
			UATag tag = ti.tag ;
			UAVal uav = tag.RT_getVal() ;
			
			TagSynVal.Builder b = TagSynVal.newBuilder() ;
			b.setTagPath(path).setIid(tag.getIID());
			if(uav!=null&&uav.isValid())
			{
				b.setStrVal(uav.getStrVal(tag.getDecDigits()))
					.setUpdateDt(uav.getValDT()).setChangeDt(uav.getValChgDT())
					.setValid(true);
			}
			else
			{
				b.setStrVal("")
				.setUpdateDt(uav.getValDT()).setChangeDt(uav.getValChgDT())
				.setValid(false);
			}
			
			retb.addTagVals(b.build()) ;
		}
		return retb.build();
	}
	
	
	public TagSynVals filterUpdatedTagSynVals()
	{
		TagSynVals.Builder retb =  TagSynVals.newBuilder() ;
		
		for(Map.Entry<String,TagItem> p2ti:getPath2Tag().entrySet())
		{
			String path = p2ti.getKey() ;
			UATag tag = p2ti.getValue().tag ;
			UAVal uav = tag.RT_getVal() ;
			
			TagSynVal.Builder b = TagSynVal.newBuilder() ;
			b.setTagPath(path).setIid(tag.getIID());
			if(uav!=null&&uav.isValid())
			{
				b.setStrVal(uav.getStrVal(tag.getDecDigits()))
					.setUpdateDt(uav.getValDT()).setChangeDt(uav.getValChgDT())
					.setValid(true);
			}
			else
			{
				b.setStrVal("")
				.setUpdateDt(uav.getValDT()).setChangeDt(uav.getValChgDT())
				.setValid(false);
			}
			
			retb.addTagVals(b.build()) ;
		}
		return retb.build() ;	
	}
	
}
