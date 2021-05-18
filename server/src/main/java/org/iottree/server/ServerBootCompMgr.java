package org.iottree.server;

import java.util.*;

import org.iottree.core.util.IServerBootComp;

public class ServerBootCompMgr
{
	static ServerBootCompMgr bcMgr = null ;
	
	static Object lockobj = new Object();
	
	static Hashtable<String, IServerBootComp> name2bc = new Hashtable<String, IServerBootComp>();
	
	static Hashtable<String,Thread> name2thread = new Hashtable<String,Thread>() ;
	
	public static IServerBootComp registerServerBoolComp(String classname)
	{
		try
		{
			Class<?> c = Class.forName(classname);
			if (c == null)
				return null;

			IServerBootComp tmpbc = (IServerBootComp) c.newInstance();
			name2bc.put(tmpbc.getBootCompName(), tmpbc);
			return tmpbc;
		}
		catch (Exception e)
		{
			System.out.println("Load Server Boot Comp Error:");
			e.printStackTrace();
			return null;
		}
	}
	
	
	public static ServerBootCompMgr getInstance()
	{
		if(bcMgr!=null)
			return bcMgr ;
		
		synchronized(lockobj)
		{
			if(bcMgr!=null)
				return bcMgr ;
			
			bcMgr = new ServerBootCompMgr();
			return bcMgr ;
		}
	}
	
	

	private ServerBootCompMgr()
	{
		String sbc = System.getProperty("server.boot.comp");
		if (sbc != null && !sbc.equals(""))
		{
			StringTokenizer tmpst = new StringTokenizer(sbc, ",");
			while (tmpst.hasMoreTokens())
			{
				String cn = tmpst.nextToken();
				if ((cn = cn.trim()).equals(""))
					continue;

				try
				{
					Class<?> c = Class.forName(cn);
					if (c == null)
						continue;

					IServerBootComp tmpbc = (IServerBootComp) c.newInstance();
					name2bc.put(tmpbc.getBootCompName(), tmpbc);
				}
				catch (Exception e)
				{
					System.out.println("Load Server Boot Comp Error:");
					e.printStackTrace();
				}
			}
		}
	}
	
	
	
	public String[] getAllServerBootNames()
	{
		String[] rets = new String[name2bc.size()];
		name2bc.keySet().toArray(rets);
		return rets;
	}
	
	public void stopAllBootComp()
	{
		for(IServerBootComp bc:name2bc.values())
		{
			try
			{
				bc.stopComp();
			}
			catch(Exception ee)
			{}
		}
	}
	
	public IServerBootComp startBootComp(String n) throws Exception
	{
		IServerBootComp bc = name2bc.get(n);
		if(bc==null)
			return null;
//		if(bc==null)
//			return false;
//		
//		if(bc.isRunning())
//			return true ;
		
		Thread t = name2thread.get(n);
		if(t!=null)
			return bc;
		
		ServerCompCtrlStarter sccs = new ServerCompCtrlStarter(bc);
		t = new Thread(sccs);
		t.start();
		return bc;
	}
	
	
	public IServerBootComp getBootComp(String n)
	{
		return name2bc.get(n);
	}
	
	public boolean isBootCompRunning(String n) throws Exception
	{
		IServerBootComp bc = name2bc.get(n);
		if(bc==null)
			return false;
		
		return bc.isRunning() ;
	}
	
	public void stopBootComp(String n) throws Exception
	{
		IServerBootComp bc = name2bc.get(n);
		if(bc==null)
			return ;
//		if(bc==null)
//			return false;
//		
//		if(bc.isRunning())
//			return true ;
		
		bc.stopComp();
	}
	
	class ServerCompCtrlStarter implements Runnable
	{
		IServerBootComp bootc = null ;
		
		public ServerCompCtrlStarter(IServerBootComp sbc)
		{
			bootc = sbc ;
		}
		
		public void run()
		{
			try
			{
				bootc.startComp();
			}
			catch(Exception e)
			{
				e.printStackTrace();
			}
			finally
			{
				name2thread.remove(bootc.getBootCompName()) ;
			}
		}
		
	}
}
