package org.iottree.server;

import java.io.File;
import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.apache.catalina.Context;
import org.apache.catalina.LifecycleState;
import org.apache.catalina.Loader;
import org.apache.catalina.connector.Connector;
import org.apache.catalina.startup.Tomcat;
import org.apache.coyote.http11.Http11NioProtocol;
import org.apache.jasper.servlet.JasperInitializer;
import org.apache.tomcat.util.net.SSLHostConfig;
import org.apache.tomcat.util.net.SSLHostConfigCertificate;
import org.iottree.core.Config;
import org.iottree.core.Config.Webapp;
import org.iottree.core.UAServer;
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
	
	private transient boolean bRunning = false;


	Tomcat tomcat = null ;
	
	public List<UAServer.WebItem> startTomcat(ClassLoader cl) throws Exception
	{
		Config.Webapps w = Config.getWebapps();

		if (w == null)
		{
			throw new RuntimeException("no Webapps found!") ;
		}
		
//		try (InputStream is = ServerTomcat.class.getResourceAsStream("/logging.properties"))
//		{
//		    if (is != null)
//		    {
//		    	for(String logln:Convert.readStreamTxtLines(is, "UTF-8"))
//		    	{
//		    		System.out.println(" log line="+logln) ;
//		    	}
//		        
//		    }
//		} catch (Exception e) {
//			System.out.println("load /logging.properties failed="+e.getMessage()) ;
//		}
		
		try (InputStream is = ServerTomcat.class.getResourceAsStream("/logging.properties"))
		{
		    if (is != null)
		    {
		    	java.util.logging.LogManager.getLogManager().readConfiguration(is);
		        System.out.println("load /logging.properties ok") ;
		    }
		} catch (Exception e) {
			System.out.println("load /logging.properties failed="+e.getMessage()) ;
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
		
		while (tomcat.getServer().getState() != LifecycleState.STARTED) {
		    Thread.sleep(100); // 
		}
		
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
		bRunning = true;
		return wis ;
	}
	
	
	
	private static Connector getNorConnector(int port)
	{
		Connector connector = new Connector();
		connector.setPort(port);
		//connector.setSecure(true);
		connector.setScheme("http");
		connector.setProperty("maxThreads", "200");
		return connector;
	}
	
	private static Connector getSslConnector(int port)
	{
	    // 1. 创建连接器并设置基本属性
	    Connector connector = new Connector();
	    connector.setPort(port);
	    connector.setSecure(true);
	    connector.setScheme("https");
	    connector.setProperty("maxThreads", "200");

	    // 2. 获取 NIO 协议处理器并开启 SSL
	    Http11NioProtocol protocol = (Http11NioProtocol) connector.getProtocolHandler();
	    protocol.setSSLEnabled(true);

	    // 3. 构建 SSLHostConfig，承载 TLS 协议和客户端认证配置
	    SSLHostConfig sslHostConfig = new SSLHostConfig();
	    sslHostConfig.setSslProtocol("TLS");
	    sslHostConfig.setCertificateVerification("none"); // 对应原 clientAuth="false"

	    // 4. 构建证书配置，原来 keystore 相关属性都在这里
	    SSLHostConfigCertificate certificate = new SSLHostConfigCertificate(
	            sslHostConfig,
	            SSLHostConfigCertificate.Type.UNDEFINED
	    );
	    certificate.setCertificateKeyAlias("tomcat");
	    certificate.setCertificateKeystorePassword("123456");
	    certificate.setCertificateKeystoreType("JKS");
	    certificate.setCertificateKeystoreFile(Config.getConfigFileBase()+"/keystore.jks");//relative to tomcat

	    // 5. 组装并应用
	    sslHostConfig.addCertificate(certificate);
	    protocol.addSslHostConfig(sslHostConfig);

	    return connector;
	}

	private static Connector getSslConnector_old(int port)
	{
		Connector connector = new Connector();
		connector.setPort(port);
		connector.setSecure(true);
		connector.setScheme("https");
		//connector.setProperty(name, value)
		//connector.set
		connector.setProperty("keyAlias", "tomcat");
		//connector.set
		connector.setProperty("keystorePass", "123456");
		connector.setProperty("keystoreType", "JKS");
		connector.setProperty("keystoreFile", "../keystore.jks");
		connector.setProperty("clientAuth", "false");
		connector.setProperty("protocol", "HTTP/1.1");
		connector.setProperty("sslProtocol", "TLS");
		connector.setProperty("maxThreads", "200");
		connector.setProperty("protocol", "org.apache.coyote.http11.Http11AprProtocol");
		connector.setProperty("SSLEnabled", "true");
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
