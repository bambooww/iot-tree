package org.iottree.core.res;

import java.io.*;
import java.util.*;

public class ResItem
{
	static HashSet<String> PIC_EXTS = new HashSet<>() ;
	static
	{
		PIC_EXTS.add("png") ;
		PIC_EXTS.add("jpg") ;
		PIC_EXTS.add("gif") ;
	}
	
	ResCxt resCxt = null ;
	
	File resF = null ;

	String name = null ;
	
	private boolean bPic = false;
	
	public ResItem(ResCxt rc,File f)
	{
		this.resCxt = rc ;
		this.resF = f ;
		
		String n = resF.getName() ;
		int k = n.lastIndexOf('.') ;
		if(k<=0)
			throw new IllegalArgumentException("invalid file name");

		String extn = n.substring(k+1).toLowerCase() ;
		bPic = PIC_EXTS.contains(extn);
		name = n.substring(0,k) ;
	}
	
	public ResCxt getResCxt()
	{
		return resCxt ;
	}
	
	public String getResId()
	{
		return this.resCxt.getCxtId()+"-"+this.getName() ;
	}
	
	/**
	 * file name or other unique name in cxt
	 */
	public String getName()
	{
		return name ;
	}
	
	public String getFileName()
	{
		return resF.getName();
	}
	
	
	public File getResFile()
	{
		return resF ;
	}
	
	public boolean isPic()
	{
		return bPic ;
	}
	
	
	void updateResFile(File f,boolean del_old)
	{
		if(del_old)
		{
			if(!f.getName().contentEquals(getFileName()))
			{
				resF.delete() ;
			}
		}
		resF = f ;
	}
}
