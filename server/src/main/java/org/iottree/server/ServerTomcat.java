package org.iottree.server;

import java.io.File;

import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.iottree.core.Config;
import org.iottree.core.Config.Webapp;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IServerBootComp;

/**
 * manager tomcat server as component
 * @author jason.zhu
 *
 */
public class ServerTomcat implements IServerBootComp
{
	static
	{
		System.setProperty("java.util.logging.manager","org.apache.juli.ClassLoaderLogManager");
		if(Config.isDebug())
			System.setProperty("java.util.logging.config.file","./log/logging_debug.properties");
		else
			System.setProperty("java.util.logging.config.file","./log/logging.properties");
		System.setProperty("java.endorsed.dirs","./tomcat/common/endorsed");
		System.setProperty("catalina.base","./tomcat");
		System.setProperty("catalina.home","./tomcat");
		System.setProperty("tomcat.util.scan.StandardJarScanFilter.jarsToSkip","*");
		System.setProperty("java.io.tmpdir","./tomcat/temp");
	}
	
	Tomcat tomcat = null ;
	public void startTomcat(ClassLoader cl) throws Exception
	{
		Config.Webapps w = Config.getWebapps();

		if (w == null)
		{
			throw new RuntimeException("no Webapps found!") ;
		}
		
		// int tomcatp =
		// Convert.parseToInt32(appele.getAttribute("port"),80) ;
		String CATALINA_HOME = Config.getConfigFileBase() + "/tomcat/";
		//String tomcatcn = "org.apache.catalina.startup.Tomcat" ;
		//tomcat = (Tomcat)cl.loadClass(tomcatcn).getDeclaredConstructor().newInstance();//new Tomcat();
		tomcat = new Tomcat();
		//tomcat.
		// tomcat.
		if(w.getAjpPort()>0)
		{
			Connector ajpc = new Connector("AJP/1.3");
			ajpc.setPort(w.getAjpPort());
			tomcat.getService().addConnector(ajpc);
		}

		if(w.getSslPort()>0)
		{
			Connector sslc = getSslConnector(w.getSslPort());
			tomcat.getService().addConnector(sslc);
		}
		
		tomcat.setPort(w.getPort());
		//Connector norc = getNorConnector(w.getPort());
		//tomcat.getService().addConnector(norc);
		
		tomcat.setBaseDir(CATALINA_HOME);
		
		String wbase = Config.getWebappBase();
		File wbf = new File(wbase);
		//tomcat.setPort(w.getPort());
		for (Webapp app : w.getAppList())
		{
			String appn = app.getAppName();
			String path = app.getPath() ;
			String fp = wbase + "/" + appn;
			if(Convert.isNotNullEmpty(path))
				fp = wbase+"/"+path ;
			if (!(new File(fp).exists()))
				continue;

			if ("ROOT".equals(appn))
			{
				//System.out.println("starting webapp=ROOT");
				tomcat.addWebapp("", fp);
			}
			else// if("system".equals(dirn))
			{
				///System.out.println("starting webapp="+appn);
				tomcat.addWebapp("/" + appn, fp);
			}
		}
		
		System.out.println("web port: "+w.getPort()) ;
		tomcat.start();
	}
	
	private static Connector getNorConnector(int port)
	{
		Connector connector = new Connector();
		connector.setPort(port);
		//connector.setSecure(true);
		connector.setScheme("http");
		connector.setAttribute("maxThreads", "200");
		return connector;
	}

	private static Connector getSslConnector(int port)
	{
		Connector connector = new Connector();
		connector.setPort(port);
		connector.setSecure(true);
		connector.setScheme("https");
		connector.setAttribute("keyAlias", "tomcat");
		connector.setAttribute("keystorePass", "123456");
		connector.setAttribute("keystoreType", "JKS");
		connector.setAttribute("keystoreFile", "../keystore.jks");
		connector.setAttribute("clientAuth", "false");
		connector.setAttribute("protocol", "HTTP/1.1");
		connector.setAttribute("sslProtocol", "TLS");
		connector.setAttribute("maxThreads", "200");
		connector.setAttribute("protocol", "org.apache.coyote.http11.Http11AprProtocol");
		connector.setAttribute("SSLEnabled", true);
		return connector;
	}



	@Override
	public String getBootCompName()
	{
		return "tomcat";
	}



	@Override
	public void startComp() throws Exception
	{
		startTomcat(Thread.currentThread().getContextClassLoader());
	}



	@Override
	public void stopComp() throws Exception
	{
		if(tomcat==null)
			return ;
		tomcat.destroy();
	}



	@Override
	public boolean isRunning() throws Exception
	{
		// TODO Auto-generated method stub
		return false;
	}

}
