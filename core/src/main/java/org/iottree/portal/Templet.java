package org.iottree.portal;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.Writer;

import org.iottree.core.util.Convert;

public class Templet
{
	TempletCat cat = null ;
	
	String name = null ;
	
	String title = null ;
	
	String page_path = null ;
	
	private transient TPage tpage = null ;
	
	public Templet(TempletCat tcat,String n,String t,String page_path)
	{
		this.cat = tcat ;
		this.name = n ;
		this.title = t ;
		this.page_path = page_path ;
	}
	
	public TempletCat getCat()
	{
		return this.cat ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getUID()
	{
		return this.cat.getName()+"."+this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public String getPagePath()
	{
		return this.page_path ;
	}

	public File getPageFile()
	{
		return new File(this.cat.getCatDir(),this.page_path) ;
	}
	
	public TPage getTPage()
	{
		return getTPage(false) ;
	}
	
	public TPage getTPage(boolean b_reload)
	{
		if(!b_reload && tpage!=null)
			return tpage ;
		
		synchronized(this)
		{
			if(!b_reload && tpage!=null)
				return tpage ;
			
			try
			{
				return tpage = this.loadTPage() ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return null ;
			}
		}
	}
	
	private TPage loadTPage() throws IOException
	{
		File f = getPageFile() ;
		if(!f.exists())
			return null;
		String txt = Convert.readFileTxt(f) ;
		return new TPage(txt) ;
	}
	
	public boolean renderOutSor(Writer out) throws IOException
	{
//		File f = getPageFile() ;
//		if(!f.exists())
//			return false;
//		try(FileInputStream fis = new FileInputStream(f);
//				InputStreamReader isr = new InputStreamReader(fis,"UTF-8");)
//		{
//			char[] cs = new char[1024] ;
//			int rlen ;
//			while((rlen=isr.read(cs))>0)
//			{
//				out.write(cs, 0, rlen);
//			}
//		}
//		out.flush(); 
//		return true ;
		TPage tpage = getTPage(true) ;
		if(tpage==null)
			return false;
		
		for(Object obj : tpage.getContList())
		{
			if(obj instanceof String)
				out.write((String)obj);
			else if(obj instanceof TPageBlk)
			{
				TPageBlk blk = (TPageBlk)obj ;
				blk.renderOutDef(out);
			}	
		}
		return true;
	}
	
	
	
	public boolean renderOutSetup(Writer out) throws IOException
	{
		TPage tpage = getTPage(true) ;
		if(tpage==null)
			return false;
		
		for(Object obj : tpage.getContList())
		{
			if(obj instanceof String)
				out.write((String)obj);
			else if(obj instanceof TPageBlk)
			{
				TPageBlk blk = (TPageBlk)obj ;
				blk.renderOutDef(out);
			}	
		}
		return true;
	}
}
