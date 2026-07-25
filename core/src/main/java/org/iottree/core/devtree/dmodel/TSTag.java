package org.iottree.core.devtree.dmodel;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.json.JSONObject;

public class TSTag
{
	String treeNid ;
	
	String tagpath ;
	
	int saveId =-1;
	
	String strVal ;
	
	private UATag tag = null ;
	
	public TSTag(TSStorage storage)
	{
		
	}
	
	public UATag getTag()
	{
		if(tag!=null)
			return tag; 
		
		UAPrj prj ;
		
		//return tag = prj.getTagById(id);
		return null;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().put("sid", this.saveId).put("tagp", tagpath) ;
	}
}
