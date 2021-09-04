package org.iottree.core.node;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.util.HashMap;

import org.iottree.core.UAManager;
import org.iottree.core.UAPrj;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;

public class PrjShareManager
{
	private static PrjShareManager instance ;
	
	public static PrjShareManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(PrjShareManager.class)
		{
			if(instance!=null)
				return instance ;
			
			instance = new PrjShareManager();
			return instance ;
		}
	}
	
	private HashMap<String,PrjSharer> id2share = new HashMap<>() ;
	
	private PrjShareManager()
	{}
	
	
	public PrjSharer setSharer(String prjid,boolean enbale,XmlData paramxd) throws Exception
	{
		UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
		if(prj==null)
			throw new Exception("no prj found") ;
		PrjSharer ps = getSharer(prjid);
		if(ps==null)
		{
			ps = new PrjShareMQTT() ;
		}
		
		ps.runStop();
//		else
//		{
//			if(ps.)
//		}
		ps.withPrjId(prjid, enbale).withParam(paramxd) ;
		saveSharer(ps) ;
		
		id2share.put(prjid, ps) ;
		return ps;
	}
	
	public PrjSharer getSharer(String prjid)// throws Exception
	{
		PrjSharer ps = id2share.get(prjid);
		if(ps!=null)
			return ps ;
		
		try
		{
			ps = loadSharer(prjid);
			if(ps!=null)
			{
				id2share.put(prjid, ps) ;
			}
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
		return ps ;
	}
	
	private PrjSharer loadSharer(String prjid) throws Exception
	{
		UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
		if(prj==null)
			return null ;
		
		File f = new File(prj.getRepSubDir(),"node_share.xml") ;
		if(!f.exists())
			return null ;
		
		try(FileInputStream fis = new FileInputStream(f);)
		{
			XmlData xd = XmlData.parseFromStream(fis, "UTF-8") ;
			PrjSharer ps = new PrjShareMQTT() ;
			ps.fromXmlData(xd);
			return ps ;
		}
	}
	
	private void saveSharer(PrjSharer ps) throws Exception
	{
		String prjid = ps.getPrjId() ;
		if(Convert.isNullOrEmpty(prjid))
		{
			throw new Exception("no prjid") ;
		}
		UAPrj prj = UAManager.getInstance().getPrjById(prjid) ;
		if(prj==null)
			throw new Exception("no prj found") ;
		
		File f = new File(prj.getRepSubDir(),"node_share.xml") ;
		
		XmlData tmpxd = ps.toXmlData() ;
		XmlData.writeToFile(tmpxd, f);
	}
}
