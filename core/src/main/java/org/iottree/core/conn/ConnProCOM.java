package org.iottree.core.conn;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlData;

import gnu.io.CommPortIdentifier;

public class ConnProCOM extends ConnProvider
{
//	private static File getRXTXComLibFile()
//	{
//		//return new File(Config.getConfigFileBase()+"/lib/rxtx/win_x64/rxtxSerial.dll");
//		return new File(Config.getConfigFileBase()+"/lib/rxtx/win_x86/rxtxSerial.dll");
//	}
//	
//	private static boolean loadRXTX() throws Exception
//	{
//		File f = getRXTXComLibFile() ;
//		if(!f.exists())
//			return false;
//		System.load(f.getCanonicalPath());
//		return true ;
//	}
//	
//	static
//	{
//		try
//		{
//			loadRXTX() ;
//		}
//		catch(Throwable e)
//		{
//			e.printStackTrace();
//		}
//	}
	
	
	public static List<String> listSysComs()
	{
		try
		{
			List<String> systemPorts = new ArrayList<>();
	        //获得系统可用的端口
	        Enumeration<CommPortIdentifier> portList = (Enumeration<CommPortIdentifier>)CommPortIdentifier.getPortIdentifiers();
	        while (portList.hasMoreElements()) {
	            String portName = portList.nextElement().getName();//获得端口的名字
	            systemPorts.add(portName);
	        }
	        return systemPorts;
		}
		catch(Throwable e)
		{
			e.printStackTrace();
			return new ArrayList<String>(0) ;
		}
	}

	
	public ConnProCOM()
	{
	
	}

	@Override
	public String getProviderType()
	{
		return "com" ;
	}
	
	@Override
	public String getProviderTpt()
	{
		return "COM" ;
	}
	
	public boolean isSingleProvider()
	{
		return true;
	}
	
	@Override
	public Class<? extends ConnPt> supportConnPtClass()
	{
		return ConnPtCOM.class ;
	}
	
	public String getName()
	{
		String n = super.getName() ;
		if(Convert.isNotNullEmpty(n))
			return n;
		return "" ;
	}
	
	public String getTitle()
	{
		String n = super.getTitle() ;
		if(Convert.isNotNullEmpty(n))
			return n;
		return "COM" ;
	}
	
	public boolean fromXmlData(XmlData xd,StringBuilder errsb)
	{
		if(!super.fromXmlData(xd,errsb))
			return false;
		
		return true ;
	}

	public XmlData toXmlData()
	{
		XmlData xd = super.toXmlData() ;
		
		return xd ;
	}
	


	
	public void disconnAll() //throws IOException
	{
		for(ConnPt ci:this.listConns())
		{
			try
			{
				ConnPtCOM cc = (ConnPtCOM)ci ;
				cc.disconnect();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
		}
	}
	
	
	public void stop()
	{
		super.stop() ;
		
		disconnAll();
	}
	
	@Override
	protected long connpRunInterval()
	{
		return 3000;
	}
	
	
	@Override
	protected void connpRunInLoop() throws Exception
	{
		for(ConnPt ci:this.listConns())
		{
			ConnPtCOM citc = (ConnPtCOM)ci ;
			citc.RT_checkConn() ;
		}
	}

}