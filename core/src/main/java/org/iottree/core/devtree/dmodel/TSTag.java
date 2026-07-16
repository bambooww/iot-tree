package org.iottree.core.devtree.dmodel;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;

public class TSTag
{
	String treeNid ;
	
	String tagpath ;
	
	int tagIID =-1;
	
	String strVal ;
	
	private UATag tag = null ;
	
	public UATag getTag()
	{
		if(tag!=null)
			return tag; 
		
		UAPrj prj ;
		
		//return tag = prj.getTagById(id);
		return null;
	}
}
