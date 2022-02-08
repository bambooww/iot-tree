package org.iottree.core.sim;

import java.io.InputStream;
import java.io.OutputStream;
import java.net.Socket;
import java.net.SocketException;
import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.UATag;
import org.iottree.core.task.TaskAction;
import org.iottree.core.util.CompressUUID;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.IXmlDataable;
import org.iottree.core.util.xmldata.XmlData;
import org.iottree.core.util.xmldata.data_class;
import org.iottree.core.util.xmldata.data_obj;
import org.iottree.core.util.xmldata.data_val;

/**
 * a slave channel will match a bus which will has one or more devices
 * 
 * @author jason.zhu
 *
 */
@data_class
public abstract class SimChannel implements Runnable
{
	private static final String[] CH_CNS = new String[] { "org.iottree.driver.common.modbus.sim.SlaveChannel" };

	static private LinkedHashMap<String, SimChannel> TP2CH = null;

	static private LinkedHashMap<String, SimChannel> getTp2Ch()
	{
		if (TP2CH != null)
			return TP2CH;

		LinkedHashMap<String, SimChannel> t2c = new LinkedHashMap<>();
		for (String cn : CH_CNS)
		{
			try
			{
				Class c = Class.forName(cn);
				SimChannel sc = (SimChannel) c.newInstance();
				t2c.put(sc.getTp(), sc);
			}
			catch ( Exception e)
			{
				e.printStackTrace();
			}
		}

		TP2CH = t2c;
		return t2c;
	}

	public static Collection<SimChannel> listAllChannelTps()
	{
		return getTp2Ch().values();
	}

	public static SimChannel createNewInstance(String chtp) throws Exception
	{
		SimChannel sc = getTp2Ch().get(chtp);
		if (sc == null)
			return null;
		return (SimChannel) sc.getClass().newInstance();
	}

	@data_val(param_name = "id")
	protected String id = null;

	@data_val(param_name = "n")
	protected String name = null;

	@data_val(param_name = "t")
	protected String title = null;

	@data_obj(param_name = "devs")
	ArrayList<SimDev> devs = new ArrayList<>();

	@data_val(param_name = "en")
	protected boolean bEnable = true;

	transient SimInstance belongTo = null;

	@data_obj(param_name = "cp")
	transient SimCP cp = null;

	Thread thread = null;

	public SimChannel()
	{
		this.id = CompressUUID.createNewId();
	}

	public abstract String getTp();

	public abstract String getTpTitle();

	public abstract SimDev createNewDev();

	public SimChannel withBasic(SimChannel osch)
	{
		this.name = osch.name;
		this.title = osch.title;
		this.bEnable = osch.bEnable;
		return this;
	}

	public String getId()
	{
		return this.id;
	}

	public SimChannel withId(String id)
	{
		this.id = id;
		return this;
	}

	public String getName()
	{
		return this.name;
	}

	public SimChannel withName(String n)
	{
		this.name = n;
		return this;
	}

	public String getTitle()
	{
		if (Convert.isNotNullEmpty(this.title))
			return this.title;
		return this.name;
	}

	public SimChannel withTitle(String t)
	{
		this.title = t;
		return this;
	}

	public boolean isEnable()
	{
		return this.bEnable;
	}

	public SimChannel withEnable(boolean b)
	{
		this.bEnable = b;
		return this;
	}

	public SimCP getConn()
	{
		return this.cp;
	}

	public void setConn(SimCP c) throws Exception
	{
		this.cp = c;
		this.cp.asChannel(this) ;
		this.save();
	}

	public List<SimDev> listDevItems()
	{
		return devs;
	}

	public SimDev getDev(String id)
	{
		for (SimDev d : this.devs)
		{
			if (d.getId().equals(id))
				return d;
		}
		return null;
	}

	public SimDev getDevByName(String n)
	{
		for (SimDev d : this.devs)
		{
			if (n.equals(d.getName()))
				return d;
		}
		return null;
	}

	public void setDevItem(int devid, List<UATag> tags)
	{
		// SimDev sdi = new SimDev();
		// //sdi.asDevTags(devid, tags) ;
		// this.devs.add(sdi) ;
	}

	public SimDev setDevBasic(SimDev sdi) throws Exception
	{
		String n = sdi.getName();
		if (Convert.isNullOrEmpty(n))
			throw new Exception("name cannot be null or empty");
		StringBuilder sb = new StringBuilder();
		if (!Convert.checkVarName(n, true, sb))
			throw new Exception(sb.toString());

		String devid = sdi.getId();
		SimDev olddev = this.getDev(devid);
		if (olddev != null)
		{
			SimDev tmpdev = this.getDevByName(n);
			if (tmpdev != null && tmpdev != olddev)
				throw new Exception("Device name [" + n + "] is already existed!");
		}
		else
		{
			SimDev tmpch = this.getDevByName(n);
			if (tmpch != null)
				throw new Exception("Device name [" + n + "] is already existed!");
		}

		int s = this.devs.size();
		for (int i = 0; i < s; i++)
		{
			SimDev dev = this.devs.get(i);
			if (dev.getId().equals(sdi.getId()))
			{
				dev.name = sdi.name;
				dev.title = sdi.title;
				dev.bEnable = sdi.bEnable;
				this.save();
				return dev;
			}
		}

		this.devs.add(sdi);
		this.save();
		// refreshActions();
		return sdi;
	}

	public SimDev setDevExt(SimDev sdi) throws Exception
	{
		String id = sdi.getId();

		int s = this.devs.size();
		for (int i = 0; i < s; i++)
		{
			SimDev dev = this.devs.get(i);
			if (dev.getId().equals(id))
			{
				sdi.name = dev.name;
				sdi.title = dev.title;
				sdi.bEnable = dev.bEnable;
				this.devs.set(i, sdi);

				this.save();
				return sdi;
			}
		}

		throw new Exception("no device found");
	}

	public void save() throws Exception
	{
		belongTo.saveChannel(this);
	}
	
	public boolean init()
	{
		for(SimDev dev:devs)
		{
			dev.init();
		}
		
		if (cp != null)
		{
			this.cp.asChannel(this) ;
		}
		
		if(cp==null)
			return false;
		
		return true;
	}

	public boolean RT_init(StringBuilder failedr)
	{
		if(cp==null)
		{
			failedr.append("no conn found in channel");
			return false;
		}

		if(!cp.RT_init(failedr))
			return false;
		
		
		return true;
	}

	protected abstract void RT_runConnInLoop(SimConn conn) throws Exception;

	public void run()
	{
		try
		{
			while(thread!=null)
			{
				try
				{
					//RT_runInThread();
					cp.RT_runInLoop();
				}
				catch(SocketException e)
				{
					//e.printStackTrace();
					System.out.println(" warn:"+e.getMessage());
				}
			}
		}
		catch ( Throwable e)
		{
			e.printStackTrace();
			// log.error("SlaveConn Broken:" + e.getMessage());

			// System.out.println("MSlaveTcpConn Broken:" + e.getMessage());
			// close() ;
		}
		finally
		{
			thread = null;

			// onRunnerStopped();
		}
	}

	public boolean RT_isRunning()
	{
		return thread != null;
	}

	synchronized public boolean RT_start(StringBuilder failedr)
	{
		if (thread != null)
			return true;

		if(!RT_init(failedr))
			return false;
		
		synchronized (this)
		{
			if (thread != null)
				return true;

			thread = new Thread(this);
			thread.start();
			return true;
		}
	}

	synchronized public void RT_stop()
	{
		Thread t = thread;
		if(t!=null)
		{
			try
			{
				t.interrupt();
				thread=null ;
			}
			finally
			{
				thread=null ;
			}
		}
		
		this.cp.RT_stop();
	}

	protected abstract void onConnOk(SimConn sc);

	protected abstract void onConnBroken(SimConn sc);
	// @Override
	// public XmlData toXmlData()
	// {
	// XmlData xd = new XmlData() ;
	// xd.setParamValue("id", this.id);
	// xd.setParamValue("n", this.name);
	// if(this.title!=null)
	// xd.setParamValue("t", this.title);
	// return xd;
	// }
	//
	// @Override
	// public void fromXmlData(XmlData xd)
	// {
	// this.id = xd.getParamValueStr("id") ;
	// this.title = xd.getParamValueStr("t") ;
	// this.name = xd.getParamValueStr("n") ;
	// }
}
