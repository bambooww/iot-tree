package org.iottree.core.util;

import java.net.*;
import java.util.*;

public class NetUtil
{
	public static class Adapter
	{
		String name = "" ;
		
		String disName = "" ;
		
		String ip4="" ;
		
		String ip6="" ;
		
		private Adapter()
		{}
		
		private Adapter(String n,String disname,String ip4,String ip6)
		{
			name =n;
			disName = disname ;
			this.ip4 = ip4 ;
			this.ip6 = ip6 ;
		}
		
		public String getName()
		{
			return name;
		}
		
		public String getDisName()
		{
			return disName ;
		}
		
		public String getIp4()
		{
			return ip4 ;
		}
		
		public String getIp6()
		{
			return ip6 ;
		}
	}
	
	public static List<Adapter> listAdaptersWithDefault()
	{
		ArrayList<Adapter> rets = new ArrayList<Adapter>() ;
		rets.add(new Adapter("","default","","")) ;
		try
		{
			List<Adapter> adps = listAdapters() ;
			rets.addAll(adps);
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
		}
		return rets ;
	}
	
	public static List<Adapter> listAdapters() throws SocketException
	{
		ArrayList<Adapter> rets =new ArrayList<>() ;
		for(Enumeration<NetworkInterface> ias = NetworkInterface.getNetworkInterfaces() ;ias.hasMoreElements();)
		{
			NetworkInterface ni = ias.nextElement() ;
			if(ni.isVirtual())
				continue ;
			if(!ni.isUp())
				continue ;
			if(ni.isLoopback())
				continue ;
			List<InterfaceAddress> addrs = ni.getInterfaceAddresses() ;
			
			Adapter adp = new Adapter() ;
			adp.name = ni.getName() ;
			adp.disName = ni.getDisplayName() ;
			for(InterfaceAddress addr:addrs)
			{
				InetAddress ad = addr.getAddress();
				if(ad instanceof Inet4Address)
				{
					adp.ip4 = ad.getHostAddress();//.toString() ;
				}
				else if(ad instanceof Inet6Address)
				{
					adp.ip6 = ad.getHostAddress() ;
				}
			}
			rets.add(adp) ;
		}
		
		return rets;
	}
	
	/**
	 * 
	 * @param ipaddr
	 * @param timeout
	 * @return
	 * @throws Exception
	 */
	public static boolean ping(String host,int timeout) throws Exception
	{
        boolean status = InetAddress.getByName(host).isReachable(timeout);
	    return status;
	}


}
