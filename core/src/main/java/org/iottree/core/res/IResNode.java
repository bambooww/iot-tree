package org.iottree.core.res;

import java.io.File;
import java.io.FileFilter;
import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;

public interface IResNode
{
	public String getResNodeId() ;
	
	public String getResNodeTitle() ;
	
	public ResDir getResDir() ;
	
	public IResNode getResNodeSub(String subid) ;
	
	public IResNode getResNodeParent() ;
	
	
	public default String getResNodeUID()
	{
		IResNode pn=this.getResNodeParent() ;
		if(pn==null)
		{
			String p = ((IResCxt)this).getResPrefix() ;
			String nid = this.getResNodeId() ;
			if(Convert.isNotNullEmpty(nid))
				return p+nid ;
			else
				return p ;
		}
		
		return  pn.getResNodeUID()+"-"+this.getResNodeId();
	}
}
