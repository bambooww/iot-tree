package org.iottree.core.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.io.StringReader;
import java.util.ArrayList;
import java.util.List;
import java.util.Properties;

import org.iottree.core.Config;

public class ProcessUtil
{
	public static class ProItemWin
	{
		String name;

		int pid;

		String sessionN;

		int session;

		String memUsed;

		public String toString()
		{
			return "[" + name + "][" + pid + "][" + sessionN + "][" + session + "][" + memUsed + "]";
		}
	}

	private static ProItemWin parseFrom(String str)
	{// iottree win panel.exe 27060 Console 1 32,656 K
		if (Convert.isNullOrEmpty(str))
			return null;
		String left, right;
		ProItemWin ret = new ProItemWin();

		int k = str.indexOf("Console");
		if (k > 0)
		{
			left = str.substring(0, k).trim();
			right = str.substring(k + 7).trim();
			ret.sessionN = "Console";
		}
		else
		{
			k = str.indexOf("Services");
			if (k <= 0)
				return null;
			left = str.substring(0, k).trim();
			right = str.substring(k + 8).trim();
			ret.sessionN = "Services";
		}

		// parse left
		k = left.lastIndexOf(' ');
		if (k < 0)
			return null;

		ret.name = left.substring(0, k).trim();
		ret.pid = Integer.parseInt(left.substring(k + 1));

		// parse right
		k = right.indexOf(' ');
		if (k < 0)
			return null;
		ret.session = Integer.parseInt(right.substring(0, k));
		ret.memUsed = right.substring(k + 1).trim();
		return ret;
	}

	public static List<ProItemWin> listWindowsProcess() throws Exception
	{
		List<ProItemWin> rets = new ArrayList<>();
		String res = runCmd("tasklist","") ;
		StringReader sr = new StringReader(res) ;
		BufferedReader br = new BufferedReader(sr) ;
		String ln = "";
		//System.out.println( ">>"+res) ;
		while ((ln = br.readLine()) != null)
		{
			
			if (Convert.isNullOrEmpty(ln))
				continue;
			ProItemWin pi = parseFrom(ln);
			if (pi == null)
				continue;
			// System.out.println(pi);
			rets.add(pi);
		}
		return rets;
	}

	/*
	 * 统计进程数量，适应与windows和linux
	 * 
	 * @Author syp
	 * 
	 * @param进程名称 返回值为进程个数
	 */
	public static int getProcessNums(String process_name) throws Exception
	{
		Runtime runtime = Runtime.getRuntime();
		Process process = null;
		int count = 0;

		Properties prop = System.getProperties();
		String os = prop.getProperty("os.name");
		if (os != null && os.toLowerCase().indexOf("linux") > -1)
		{

			process = runtime.exec("ps -ef");
			// list all process
			process = Runtime.getRuntime().exec("ps -ef");
			try (InputStream inputs = process.getInputStream();
					BufferedReader reader = new BufferedReader(new InputStreamReader(inputs)))
			{
				String line = null;
				while ((line = reader.readLine()) != null)
				{
					if (line.contains(process_name))
					{
						// System.out.println("line===>" + line);
						count++;
					}
				}
			}
		}
		else
		{
			// for windows
			List<ProItemWin> piws = listWindowsProcess();
			for (ProItemWin piw : piws)
			{
				if (piw.name.equals(process_name))
					count++;
			}
		}

		return count;

	}
	
	public static String runCmdNetList() throws Exception
	{
	    return runCmd("net.exe", "start");
	}

	public static boolean runCmdNetCheckRun(String service_list_name)  throws Exception
	{
	    String tmps = runCmd("net.exe", "start");
	    StringReader sr = new StringReader(tmps);
	    BufferedReader br = new BufferedReader(sr) ;
	    String ln;
	    do
	    {
	        ln = br.readLine();
	        if (ln == null)
	            break;
	        ln = ln.trim();
	        if (service_list_name.equals(ln))
	            return true;
	    }
	    while (ln != null);
	    return false;
	}



	public static String runCmdNetStart(String servicename) throws Exception
	{
	    return runCmd("net.exe", "start "+ servicename);
	}

	public static String runCmdNetStop(String servicename) throws Exception
	{
	    return runCmd("net.exe", "stop " + servicename);
	}
	
	public static String runCmd(String cmd,String arguments) throws Exception
	{
		Runtime runtime = Runtime.getRuntime();
		Process process = runtime.exec(cmd+(arguments==null?"":" "+arguments));
		
		StringBuilder sb = new StringBuilder() ;
		try (InputStream inputs = process.getInputStream();
				BufferedReader br = new BufferedReader(new InputStreamReader(inputs)))
		{
			String ln ;
			while((ln=br.readLine())!=null)
				sb.append(ln).append("\r\n") ;
		}
		
		return sb.toString() ;
	}
	
	
	
	public static final String SN_SERVICE_MON = "service_mon" ;
	
	
	/**
	 * iottree will reboot 
	 * before reboot it will start process and 
	 */
	public static void startMonerAndReboot() throws Exception
	{
//		File wf = new File("./iottree_mon_running.flag");
//		if(wf.exists())
//			return ;
//		
//		Thread daemon_th = new Thread(()->{
//			String javahome = System.getProperty("java.home") ;
//			String bdir = Config.getConfigFileBase() ;
//			File javaf = new File(javahome+"/bin/java.exe") ;
//			File logf = new File(bdir+"/log/service_mon.log") ;
//			
//			try
//			{
//				//String cmd = javaf.getCanonicalPath()+" -cp "+bdir+File.separator+"lib"+File.separator+"* org.iottree.core.util.ProcessUtil "+SN_SERVICE_MON+" > "+logf.getCanonicalPath() ;
//				String cmd = javaf.getCanonicalPath();
//				String pm = " -cp "+bdir+File.separator+"lib"+File.separator+"* org.iottree.core.util.ProcessUtil "+SN_SERVICE_MON +" > "+logf.getCanonicalPath() ;
//				System.out.println("startMonSerivceAndReboot cmd>> "+cmd+pm) ;
//				
//				ProcessBuilder processBuilder = new ProcessBuilder(cmd,pm);
//		
//		        Process pro= processBuilder.start();
//		        System.out.println("startMonSerivceAndReboot alive="+pro.isAlive()) ;
//		        pro.waitFor() ;
//			}
//			catch(Exception ee)
//			{
//				ee.printStackTrace();
//			}
//		}) ;
//		
//		daemon_th.setDaemon(true);
//		daemon_th.start(); 
		
	}
	
	

	public static void main(String[] args) throws Exception
	{
//		if(args.length<=0)
//			return ;
//		switch(args[0])
//		{
//		case SN_SERVICE_MON:
//			runIntervalCmdMon() ;
//			break ;
//		}
	}
}
