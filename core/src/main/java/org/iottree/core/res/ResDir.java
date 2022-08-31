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
	File resDir = null ;
	
	public ResDir(File dir)
	{
		this.resDir = dir ;
	}
//	IResNode belongToNode = null ;
//	
//	String id = "" ;
//	
//	String title = "" ;
//	
//	File dirF = null ;
//	
//	public ResDir(IResNode n,String id,String title,File dirf)
//	{
//		this.belongToNode = n ;
//		
//		this.id = id ;
//		this.title = title ;
//		this.dirF = dirf ;
//	}
//
//	public IResNode getResNode()
//	{
//		return belongToNode ;
//	}
//
//	public String getResNodeUID()
//	{
//		return this.belongToNode.getResNodeUID() ;
//	}

	public File getResDir()
	{
		return resDir ;
	}
	
	
	public boolean hasResItems()
	{
		if(!resDir.exists())
			return false;
		List<ResItem> ris = this.listResItems() ;
		return ris.size()>0 ;
	}
//	public String getId()
//	{
//		return id ;
//	}
//	
//	public String getTitle()
//	{
//		return this.title ;
//	}
	
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
		if(fs!=null)
		{
			for(File f:fs)
			{
				ResItem ri =  new ResItem(f);
				rets.add(ri) ;
			}
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
			if(ri.getName().equals(name))
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


	public boolean delResItem(String name)
	{
		ResItem ri = this.getResItem(name) ;
		if(ri==null)
			return false;
		
		File resf = ri.getResFile() ;
		if(resf==null)
			return false;
		boolean r = resf.delete();
		if(r)
		{//del cache
			
		}
		return r ;
	}
//	@Override
//	public int compareTo(ResDir o)
//	{
//		return getCxtId().compareTo(o.getCxtId());
//	}
}
