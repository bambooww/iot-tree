package org.iottree.core.res;

import java.io.*;

import java.util.*;
import org.iottree.core.util.Convert;

import org.apache.commons.fileupload.*;

/**
 * belong to rep channel dev or component
 * which contains resources like pics css or other file using same management
 * 
 * @author jason.zhu
 *
 */
public class ResDir //Comparable<ResDir>
{
	IResNode belongToNode = null ;
	
	String id = "" ;
	
	String title = "" ;
	
	File dirF = null ;
	
	//ResDir parent = null ;
	
	public ResDir(IResNode n,String id,String title,File dirf)
	{
		this.belongToNode = n ;
		
		this.id = id ;
		this.title = title ;
		this.dirF = dirf ;
	}
	
//	public ResDir(ResDir parent,String id,String title,File dirf)
//	{
//		this.parent = parent ;
//		this.belongToCxt = parent.getResCxt() ;
//		
//		this.id = id ;
//		this.title = title ;
//		this.dirF = dirf ;
//	}
	
	public IResNode getResNode()
	{
		return belongToNode ;
	}
	
//	public ResDir getParent()
//	{
//		return this.parent ;
//	}
	
//	public void setParent(ResDir p)
//	{
//		this.parent = p ;
//	}
	
	public String getResNodeUID()
	{
		return this.belongToNode.getResNodeUID() ;
	}
	
//	public ResDir getResDirParent()
//	{
//		return this.parent ;
//	}
	
	public File getResDir()
	{
		return dirF ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public List<ResItem> listResItems()
	{
		ArrayList<ResItem> rets = new ArrayList<>() ;
		File dirf = getResDir() ;
		if(!dirf.exists())
			return rets ;
			
		File[] fs = dirf.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(f.isDirectory())
					return false;
				return true;
			}});
		for(File f:fs)
		{
			ResItem ri =  new ResItem(f);
			rets.add(ri) ;
		}
		return rets ;
	}
	
	public List<ResItem> listResItemsPic()
	{
		ArrayList<ResItem> rets = new ArrayList<>() ;
		for(ResItem ri: listResItems())
		{
			if(ri.isPic())
				rets.add(ri) ;
		}
		return rets;
	}
	
	
	public ResItem getResItem(String name)
	{
		for(ResItem ri: listResItems())
		{
			if(ri.getName().contentEquals(name))
				return ri ;
		}
		return null;
	}
	
	public boolean containsResItem(String name)
	{
		return getResItem(name)!=null ;
	}
	
	public void setResItem(String name,FileItem f) throws Exception
	{
		if(Convert.isNullOrEmpty(name))
			throw new Exception("name cannot be null ") ;

		ResItem oldri = getResItem(name) ;
		
		String n = f.getName() ;
		int k = n.lastIndexOf('.') ;
		String extn = n.substring(k+1).toLowerCase() ;
		
		File dirf = getResDir();
		if(!dirf.exists())
			dirf.mkdirs();
		
		File tarf = new File(dirf,name+"."+extn) ;
		f.write(tarf);
		if(oldri!=null)
		{
			oldri.updateResFile(tarf,true) ;
		}
	}


//	@Override
//	public int compareTo(ResDir o)
//	{
//		return getCxtId().compareTo(o.getCxtId());
//	}
}
