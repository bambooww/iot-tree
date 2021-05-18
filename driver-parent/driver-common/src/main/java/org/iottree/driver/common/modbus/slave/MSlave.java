package org.iottree.driver.common.modbus.slave;

import java.util.*;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlVal;
import org.w3c.dom.Element;


public abstract class MSlave
{
	private ArrayList<MSlaveDataProvider> bitPros = new ArrayList<MSlaveDataProvider>() ;
	
	private ArrayList<MSlaveDataProvider> wordPros = new ArrayList<MSlaveDataProvider>() ;
	
	void init(Element ele)
	{
		for(Element dele:Convert.getSubChildElement(ele, "data"))
		{
			MSlaveDataProvider dp = createProvider(dele) ;
			if(dp==null)
				continue ;
			
			if(dp.getDataType()==XmlVal.XmlValType.vt_bool)
				bitPros.add(dp) ;
			else
				wordPros.add(dp) ;
		}
	}
	
	private MSlaveDataProvider createProvider(Element ele)
	{
		try
		{
			String procn = ele.getAttribute("provider");
			if(Convert.isNullOrEmpty(procn))
				return null ;
			
			Class c = Class.forName(procn) ;
			MSlaveDataProvider dp = (MSlaveDataProvider)c.newInstance() ;
			dp.init(ele) ;
			
			return dp ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace() ;
			return null;
		}
	}
	
	
	public List<MSlaveDataProvider> getBitDataProviders()
	{
		return bitPros;
	}
	
	public List<MSlaveDataProvider> getWordDataProviders()
	{
		return wordPros;
	}
	
	
	public abstract void start();
	
	public abstract void stop();
}
