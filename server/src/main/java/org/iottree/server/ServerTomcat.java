package org.iottree.server;

import java.io.File;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.Context;
import org.apache.catalina.Loader;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.jasper.servlet.JasperInitializer;
import org.iottree.core.Config;
import org.iottree.core.Config.Webapp;
import org.iottree.core.UAServer;
import org.iottree.core.plugin.PlugManager;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IServerBootComp;
import org.iottree.core.util.web.AppWebConfig;

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
	
	public List<UAServer.WebItem> startTomcat(ClassLoader cl) throws Exception
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
		
//		Connector getNorConnector(int port)
//		tomcat.getService().addConnector(sslc);
		
		//tomcat.setPort(w.getPort());
		if(w.getPort()>0)
		{
			Connector norc = getNorConnector(w.getPort());
			tomcat.getService().addConnector(norc);
		}
		
		tomcat.setBaseDir(CATALINA_HOME);
		
		String wbase = Config.getWebappBase();
		File wbf = new File(wbase);
		
		LinkedHashMap<String,Context> app2cxt = new LinkedHashMap<>() ;
		HashMap<String,File> app2ff = new HashMap<>() ;
		//tomcat.setPort(w.getPort());
		for (Webapp app : w.getAppList())
		{
			String appn = app.getAppName();
			String path = app.getPath() ;
			String fp = wbase + "/" + appn;
			if(Convert.isNotNullEmpty(path))
				fp = wbase+"/"+path ;
			
			File ff = new File(fp) ;
			if (!(ff.exists()))
				continue;

			Context cxt = null ;
			if ("ROOT".equals(appn))
			{
				//System.out.println("starting webapp=ROOT");
				cxt = tomcat.addWebapp("", fp);
			}
			else// if("system".equals(dirn))
			{
				cxt = tomcat.addWebapp("/" + appn, fp);
			}
			
			app2cxt.put(appn, cxt) ;
			app2ff.put(appn,ff) ;
			cxt.addServletContainerInitializer(new JasperInitializer(), null);
		}
		
		tomcat.getConnector() ;//call it to list port
		System.out.println("web port "+((w.getPort()>0)?" http:"+w.getPort():"")+(w.getSslPort()>0?("  https:"+w.getSslPort()):"")+" tomcat starting ...") ;
		long st = System.currentTimeMillis() ;
		tomcat.start();
		
		for(Map.Entry<String,Context> a2c:app2cxt.entrySet())
		{
			String appn = a2c.getKey() ;
			Context cxt = a2c.getValue() ;
			Loader loader = cxt.getLoader();
	        ClassLoader clzld = loader.getClassLoader() ;
	        String doc_base = cxt.getDocBase(); //  /xx/xx
	        //System.out.println(" app="+appn+" docbase="+doc_base+"  clzld="+clzld.hashCode()) ;
	        if(appn.startsWith("_"))
	        	continue ;
	        File webf = new File(doc_base) ;
	        File web_xmlf = new File(webf,"web.xml") ;
	        if(!web_xmlf.exists())
	        	continue ;
	        System.out.println(">>>loaded webapp ["+appn+"] - "+doc_base);
	        AppWebConfig.registerModuleWebConfig(webf,appn, clzld) ;
		}
		
		ArrayList<AppWebConfig> awcs = AppWebConfig.getModuleWebConfigAll() ;
		for(AppWebConfig awc:awcs)
		{
			awc.loadListeners(awcs);
		}
        
		System.out.println(" tomcat started . cost ["+(System.currentTimeMillis()-st)+"] ms") ;
		
		
		ArrayList<UAServer.WebItem> wis = new ArrayList<>() ;
		for(Map.Entry<String, Context> n2cxt:app2cxt.entrySet())
		{
			String appn = n2cxt.getKey() ;
			ClassLoader tmpcl = n2cxt.getValue().getLoader().getClassLoader() ;
			//ClassLoader tmpcl = cxt.getClass().getClassLoader() ;
			//System.out.println(n2cxt.getKey()+ " cl="+tmpcl.hashCode()+" "+tmpcl.toString()) ;
			File webf = app2ff.get(appn) ;
			if(webf.isDirectory())
				webf = new File(webf,"WEB-INF/") ;
			
			UAServer.WebItem wi = new UAServer.WebItem(appn, tmpcl, webf) ;
			wis.add(wi) ;
		}
		
		//AppWebConfig.fireAllWebAppLoaded();
		
		return wis ;
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
		//connector.setProperty(name, value)
		connector.setAttribute("keyAlias", "tomcat");
		//connector.set
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

	
	private transient boolean bRunning = false;


	@Override
	public void startComp() throws Exception
	{
		startTomcat(Thread.currentThread().getContextClassLoader());
		bRunning = true;
	}



	@Override
	public void stopComp() throws Exception
	{
		if(tomcat==null)
		{
			bRunning = false;
			return ;
		}
		tomcat.stop();
		tomcat.destroy();
		bRunning = false;
	}



	@Override
	public boolean isRunning() throws Exception
	{
		return bRunning;
	}

}
