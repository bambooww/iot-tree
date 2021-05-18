package org.iottree.core.res;

import java.io.*;

import java.util.*;
import org.iottree.core.util.Convert;

import org.apache.commons.fileupload.*;

/**
 * belong to rep channel dev or component
 * which contains resources like pics css or other file using same management
 * 
 * rep:
 * @author zzj
 *
 */
public class ResCxt implements Comparable<ResCxt>
{
	String prefix ;
	
	File cxtDirF = null ;
	//public static final String RES_IDX
	String id = null ;
	
	String title = "" ;
	
	public ResCxt(String prefix,String id,String title,File dirf)
	{
		if(prefix.contains("_"))
			throw new IllegalArgumentException("invalid prefix") ;
		if(id.contains("_"))
			throw new IllegalArgumentException("invalid id") ;
		this.prefix = prefix ;
		cxtDirF = dirf ;
		this.id = id ;
		this.title = title ;
		
		ResCxtManager.getInstance().setResCxt(this);
	}
	
	public File getResDir()
	{
		return cxtDirF ;
	}
	
	public String getPrefix()
	{
		return prefix ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public String getCxtId()
	{
		if(id==null)
			return prefix;
		return prefix+"_"+id ;
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
			ResItem ri =  new ResItem(this,f);
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

	

	@Override
	public int compareTo(ResCxt o)
	{
		return getCxtId().compareTo(o.getCxtId());
	}
}
