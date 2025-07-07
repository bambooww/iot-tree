package org.iottree.core.conn;

import java.io.File;
import java.util.ArrayList;
import java.util.Enumeration;
import java.util.HashMap;
import java.util.List;
import java.util.stream.Collectors;

import org.iottree.core.Config;
import org.iottree.core.ConnProvider;
import org.iottree.core.ConnPt;
import org.iottree.core.util.Convert;
import org.iottree.core.util.Env;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONArray;
import org.json.JSONObject;
import org.w3c.dom.Element;

import com.fazecast.jSerialComm.SerialPort;

import gnu.io.CommPortIdentifier;

public class ConnProCOM extends ConnProvider
{
	public static class COMItem
	{
		public String name ;
		
		public String title ;
		
		public COMItem(String n,String t)
		{
			this.name = n ;
			this.title = t ;
			
		}
		
//		public COMItem(String n)
//		{
//			this.name = n ;
//			this.title = n ;
//		}
		
		public String getShowTitle()
		{
			if(Convert.isNullOrEmpty(this.title))
				return name ;
			return "("+this.title+") "+this.name ;
		}
	}
	

	private static HashMap<String,String> comDefMap = null ; 
	

	public static HashMap<String,String> getComDefMap()
	{
		if(comDefMap!=null)
			return comDefMap ;
		
		HashMap<String,String> ret = new HashMap<>() ;
		try
		{
			File mapf = new File(Config.getDataDirBase()+"/dev_drv/conn_com_def.json") ;
			if(!mapf.exists())
				return comDefMap = ret ;
			String txt = Convert.readFileTxt(mapf) ;
			if(Convert.isNullOrEmpty(txt))
				return comDefMap = ret ;
			JSONArray jarr =new JSONArray(txt) ;
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				String nn = tmpjo.optString("name") ;
				if(Convert.isNullOrEmpty(nn))
					continue ;
				String tt = tmpjo.optString("title",nn) ;
				ret.put(nn,tt) ;
			}
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		return comDefMap = ret ;
	}
	
	
	private static Boolean bRxTx = null ;
	public static boolean usingRxTx()
	{
		if(bRxTx!=null)
			return bRxTx ;
		
		Element ele = Config.getConfElement("system");
		if(ele!=null && "true".equalsIgnoreCase(ele.getAttribute("rxtx")) )
		{
			bRxTx = true ;
			return true ;
		}
		
		bRxTx = Env.isJVM_Win32();
		return bRxTx ;
//		if(bRxTx!=null)
//			return bRxTx ;
//		
		
		
		
	}
	
	public static List<COMItem> listSysComs()
	{
		HashMap<String,String> com2tt = getComDefMap() ;
		if(usingRxTx())
		{
			try
			{
				List<COMItem> systemPorts = new ArrayList<>();
		        //获得系统可用的端口
		        Enumeration<CommPortIdentifier> portList = (Enumeration<CommPortIdentifier>)CommPortIdentifier.getPortIdentifiers();
		        while (portList.hasMoreElements())
		        {
		            String portn = portList.nextElement().getName();//获得端口的名字
		            String tt = com2tt.get(portn) ;
		            systemPorts.add(new COMItem(portn,tt));
		        }
		        return systemPorts;
			}
			catch(Throwable e)
			{
				e.printStackTrace();
				return new ArrayList<COMItem>(0) ;
			}
		}
		else
		{
			SerialPort[] serialPorts = SerialPort.getCommPorts();
			List<COMItem> portNameList = new ArrayList<>();
	        for(SerialPort serialPort:serialPorts)
	        {
	        	String portn = serialPort.getSystemPortName() ;
	        	String tt = com2tt.get(portn) ;
	            portNameList.add(new COMItem(portn,tt));
	        }
	        portNameList = portNameList.stream().distinct().collect(Collectors.toList());
	        return portNameList;
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