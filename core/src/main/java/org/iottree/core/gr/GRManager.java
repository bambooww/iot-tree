package org.iottree.core.gr;

import java.io.File;
import java.io.FileFilter;
import java.io.FileInputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.iottree.core.Config;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.InputSource;

public class GRManager
{
	static Object locker = new Object() ;
	static GRManager ins = null ;
	
	public static GRManager getInstance()
	{
		if(ins!=null)
			return ins ;
		
		synchronized(locker)
		{
			if(ins!=null)
				return ins ;
			
			ins = new GRManager() ;
			return ins ;
		}
	}
	
	static final String REF_HEAD = "/_iottree/pics/" ;
	
	ArrayList<GRCat> giCats = null;//new ArrayList<GICat>() ;
	HashMap<String,GRItem> name2gii = null ;
	
	private GRManager()
	{
		
	}
	
	public List<GRCat> getAllCats()
	{
		if(giCats!=null)
			return giCats ;
		
		ArrayList<GRCat> gics = new ArrayList<GRCat>() ;
		
		String pdir = Config.getWebappBase()+REF_HEAD ;
		File dirgr = new File(pdir);
		if(!dirgr.exists())
			return gics ;
		
		File[] fs = dirgr.listFiles(new FileFilter(){

			public boolean accept(File f)
			{
				return f.isDirectory() ;
			}}) ;
		
		for(File df:fs)
		{
			File grf = new File(df,"gr.xml") ;
			if(!grf.exists())
				continue ;
			
			try
			{
				GRCat gic = loadGrXml(pdir,df.getName()) ;
				gics.add(gic) ;
			}
			catch(Exception ee)
			{
				ee.printStackTrace() ;
			}
		}
		giCats = gics ;
		return gics ;
	}
	
	
	
	private GRCat loadGrXml(String pdir,String catn)
		throws Exception
	{
		FileInputStream fis = null ;
		Element rootele = null ;
		try
		{
			fis = new FileInputStream(pdir+catn+"/gr.xml") ;
			DocumentBuilderFactory docBuilderFactory= DocumentBuilderFactory.newInstance();
			docBuilderFactory.setNamespaceAware(false);
			docBuilderFactory.setValidating(false);
			DocumentBuilder docBuilder = docBuilderFactory.newDocumentBuilder();
		
			InputSource is = new InputSource(fis);
			//is.setEncoding("gb2312");
			Document doc = docBuilder.parse(is);
			rootele = doc.getDocumentElement();
		}
		finally
		{
			if(fis!=null)
				fis.close() ;
		}
		
		if(rootele==null)
			return null ;
		
		return new GRCat(pdir,catn,rootele) ;
	}
	
	
	
	private HashMap<String,GRItem> getName2Item()
	{
		if(name2gii!=null)
			return name2gii ;
		HashMap<String,GRItem> n2g = new HashMap<String,GRItem>() ;
		for(GRCat gic:getAllCats())
		{
			for(GRItem gii:gic.getGRItems())
			{
				n2g.put(gii.getName(),gii) ;
			}
		}
		name2gii = n2g ;
		return n2g ;
	}
	
	/**
	 * 
	 *
	 */
	public void clearCache()
	{
		name2gii = null ;
		giCats = null ;
	}
	
	/**
	 * @param jscn
	 * @return
	 */
	public GRItem getGRItemByName(String n)
	{
		HashMap<String,GRItem> n2g = getName2Item() ;
		return n2g.get(n) ;
	}
	
	public GRCat getGRCatByName(String n)
	{
		for(GRCat gic:getAllCats())
		{
			if(n.equals(gic.getName()))
				return gic ;
		}
		return null ;
	}
	
	public List<GRCat> getGRCatAll()
	{
		return getAllCats() ;
	}
}
