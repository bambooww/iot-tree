package org.iottree.core.sim;

import java.io.File;
import java.io.FilenameFilter;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.DataTranserXml;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;

@data_class
public class SimInstance
{
	@data_val(param_name = "id")
	String id = null ;
	
	@data_val(param_name = "n")
	String name = null ;
	
	@data_val(param_name = "t")
	String title = null ;
	
	@data_val(param_name = "en")
	boolean bEnable = true ;
	
	//@data_obj(obj_c = SimChannel.class,param_name = "chs")
	private ArrayList<SimChannel> channels = null;//new ArrayList<>() ; 
	
	public SimInstance()
	{
		this.id = CompressUUID.createNewId();
	}
	
	public SimInstance withBasic(SimChannel osch)
	{
		this.name = osch.name ;
		this.title = osch.title ;
		this.bEnable = osch.bEnable ;
		return this ;
	}
	
	public List<SimChannel> getChannels()
	{
		if(channels!=null)
			return channels ;
		
		synchronized(this)
		{
			if(channels!=null)
				return channels ;
			
			channels = loadChannels() ;
			return channels ;
		}
	}
	
	File getInsDir()
	{
		return SimManager.getInstance().getInsDir(this.id) ;
	}
	
	private ArrayList<SimChannel> loadChannels()
	{
		ArrayList<SimChannel> rets=  new ArrayList<>() ;
		File dir = getInsDir() ;
		if(!dir.exists())
			return rets ;
		
		final FilenameFilter ff = new FilenameFilter()
				{

					@Override
					public boolean accept(File dir, String name)
					{
						return name.startsWith("ch_")&&name.endsWith(".xml");
					}
			
				};
		File[] chfs = dir.listFiles(ff) ;
		if(chfs==null||chfs.length<=0)
			return rets ;
		for(File chf:chfs)
		{
			try
			{
				SimChannel ch = loadChannel(chf) ;
				if(ch==null)
					continue ;
				ch.belongTo = this;
				ch.init();
				rets.add(ch) ;
			}
			catch(Exception e)
			{
				System.out.println(" warn:load channel failed - "+chf.getName()) ;
			}
		}
		return rets ;
	}
	
	private SimChannel loadChannel(File f) throws Exception
	{
		if(!f.exists())
			return null ;
		XmlData tmpxd = XmlData.readFromFile(f);
		String cn = tmpxd.getParamValueStr(DataTranserXml.PN_CLASSN);
		if(Convert.isNullOrEmpty(cn))
			return null ;
		SimChannel sc = (SimChannel)Class.forName(cn).newInstance() ;
		DataTranserXml.injectXmDataToObj(sc, tmpxd);
		
		return sc ;
	}
	
	void saveChannel(SimChannel sc) throws Exception
	{
		File dir = getInsDir() ;
		if(!dir.exists())
			dir.mkdirs() ;
		
		File f = new File(dir,"ch_"+sc.getId()+".xml") ;
		XmlData xd = DataTranserXml.extractXmlDataFromObj(sc) ;
		//XmlData xd = rep.toUAXmlData();
		xd.setParamValue(DataTranserXml.PN_CLASSN, sc.getClass().getCanonicalName());
		XmlData.writeToFile(xd, f);
	}
	
	public boolean delChannel(String chid)
	{
		SimChannel ch = this.getChannel(chid) ;
		if(ch==null)
			return false;
		File dir = getInsDir() ;
		File f = new File(dir,"ch_"+chid+".xml") ;
		if(!f.exists())
			return false;
		f.delete() ;
		this.getChannels().remove(ch);
		return true ;
	}
	
	public String getId()
	{
		return this.id ;
	}
	
	public SimInstance withId(String id)
	{
		this.id = id ;
		return this ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public SimInstance withName(String n) throws Exception
	{
		StringBuilder chkf = new StringBuilder() ;
		if(!Convert.checkVarName(n, true, chkf))
			throw new Exception(chkf.toString()) ;
		
		this.name = n ;
		return this ;
	}
	
	public String getTitle()
	{
		if(Convert.isNotNullEmpty(this.title))
			return this.title ;
		return this.name ;
	}
	
	public SimInstance withTitle(String t)
	{
		this.title = t ;
		return this ;
	}
	
	public boolean isEnable()
	{
		return this.bEnable ;
	}
	
	public SimInstance withEnable(boolean b)
	{
		this.bEnable = b ;
		return this ;
	}
	
	public long getSavedDT()
	{
		File f = SimManager.getInstance().getInsDir(this.getId()) ;
		if(!f.exists())
			return -1 ;
		return f.lastModified() ;	
	}
	
	public SimChannel getChannel(String chid)
	{
		for(SimChannel sc:getChannels())
		{
			if(chid.equals(sc.getId()))
				return sc ;
		}
		return null ;
	}
	
	public SimChannel getChannelByName(String n)
	{
		for(SimChannel sc:getChannels())
		{
			if(n.equals(sc.getName()))
				return sc ;
		}
		return null ;
	}
	
	public SimChannel setChannelBasic(SimChannel sch) throws Exception
	{
		String n = sch.getName() ;
		if(Convert.isNullOrEmpty(n))
			throw new Exception("name cannot be null or empty") ;
		StringBuilder sb=  new StringBuilder() ;
		if(!Convert.checkVarName(n, true, sb))
			throw new Exception(sb.toString()) ;
		
		String chid = sch.getId() ;
		SimChannel oldch = this.getChannel(chid) ;
		if(oldch!=null)
		{
			SimChannel tmpch = this.getChannelByName(n) ;
			if(tmpch!=null&&tmpch!=oldch)
				throw new Exception("Channel name ["+n+"] is already existed!") ;
		}
		else
		{
			SimChannel tmpch = this.getChannelByName(n) ;
			if(tmpch!=null)
				throw new Exception("Channel name ["+n+"] is already existed!") ;
		}
		
		List<SimChannel> chs = getChannels() ;
		int s = chs.size();
		for (int i = 0; i < s; i++)
		{
			SimChannel ch = chs.get(i);
			if (ch.getId().equals(sch.getId()))
			{
				ch.name = sch.name ;
				ch.title = sch.title ;
				ch.bEnable = sch.bEnable ;
				ch.save();
				ch.init();
				return ch;
			}
		}

		sch.belongTo = this;
		chs.add(sch);
		sch.save();
		sch.init();
		//refreshActions();
		return sch;
	}
}