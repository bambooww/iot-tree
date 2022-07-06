package org.iottree.core.res;

import java.io.*;
import java.util.*;

import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.iottree.core.util.Convert;
import org.iottree.core.util.web.WebRes;

public class ResItem
{
	static HashSet<String> PIC_EXTS = new HashSet<>() ;
	static
	{
		PIC_EXTS.add("png") ;
		PIC_EXTS.add("jpg") ;
		PIC_EXTS.add("gif") ;
	}
	
	File resF = null ;

	String name = null ;
	
	private boolean bPic = false;
	
	public ResItem(File f)
	{
		this.resF = f ;
		
		String n = resF.getName() ;
		int k = n.lastIndexOf('.') ;
		if(k<=0)
			throw new IllegalArgumentException("invalid file name");

		String extn = n.substring(k+1).toLowerCase() ;
		bPic = PIC_EXTS.contains(extn);
		name = n;//n.substring(0,k) ;
	}
	
	public String getResId()
	{
		return this.getName() ;
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
	
	
	transient private byte[] resCont = null ;
	
	transient private long resModifyDT = -1 ;
	
	private byte[] getOrLoadCont()
	{
		if(resCont!=null)
			return resCont ;
		
		synchronized(this)
		{
			File rf = getResFile();
			if(!rf.exists())
			{
				resCont = new byte[0] ;
				return resCont ;
			}
			
			try
			{
				resCont = Convert.readFileBuf(rf) ;
			}
			catch(Exception e)
			{
				e.printStackTrace();
				resCont = new byte[0] ;
			}
			return resCont ;
		}
		
	}
	
	public void renderOut(HttpServletRequest req,HttpServletResponse resp) throws IOException
	{
		byte[] bs = getOrLoadCont() ;
		if(bs==null||bs.length==0)
			return ;
		File rf = getResFile() ;
		WebRes.renderFile(req,resp, rf.getName(), bs,this.bPic,new Date( rf.lastModified()));
	}
}
