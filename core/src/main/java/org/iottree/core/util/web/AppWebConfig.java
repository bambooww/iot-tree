package org.iottree.core.util.web;

import java.io.File;
import java.lang.reflect.InvocationTargetException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.List;
import java.util.StringTokenizer;

import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.XmlHelper;
import org.iottree.portal.NavApp;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

public class AppWebConfig
{
	public static interface IWebLoadListener
	{
		public void onWebappAllLoaded(AppWebConfig webc,HashMap<String,AppWebConfig> module2awc) 
			throws Exception;
	}
	
	public static final String TAG_AUTH = "authorization";
	public static final String TAG_DEFAULT = "default";
	public static final String TAG_LOC = "location";
	
	public static final String ATTRN_TYPE = "type";
	public static final String ATTRN_IS_LOGIN = "is_login";
	public static final String ATTRN_SUPPORT_PORTAL = "support_portal";
	public static final String ATTRN_PATH = "path";
	public static final String ATTRN_USRES = "users";
	public static final String ATTRN_ROLES = "roles";
	public static final String ATTRN_EXTNAMES = "ext_names";
	
	public static final String ATTRN_INNER_ACCESS_ONLY = "inner_access_only";
	
	
	public static final String ATTRV_ALLOW = "allow";
	public static final String ATTRV_DENY = "deny";
	

	
	
	private static HashMap<String,AppWebConfig> module2webconf = new HashMap<String,AppWebConfig>() ;
	
	/**
	 * 根据模块名称获得对应的WebConfig对象
	 * @param modulen
	 * @return
	 * @throws Exception
	 */
	public static AppWebConfig getModuleWebConfig(String modulen)
	{
		return module2webconf.get(modulen);
	}
	
	public static AppWebConfig registerModuleWebConfig(File webf,String modulen,ClassLoader cl)
		//throws Exception
	{
		AppWebConfig wc = module2webconf.get(modulen);
		if(wc!=null)
			return wc ;
		
		//File webf = new File(Config.getWebappBase()+"/"+modulen) ;
		
		wc = new AppWebConfig(webf,modulen,cl);
		module2webconf.put(modulen, wc);
		return wc ;
	}
	
	
	public static ArrayList<AppWebConfig> getModuleWebConfigAll()
	{
		ArrayList<AppWebConfig> rets = new ArrayList<AppWebConfig>() ;
		rets.addAll(module2webconf.values()) ;
		return rets ;
	}
	
//	/**
//	 * call by server tomcat
//	 */
//	public static void fireAllWebAppLoaded()
//	{
//		for(AppWebConfig wc:module2webconf.values())
//		{
//			for(Object ob:wc.webLoadLiss)
//			{
//				if(!(ob instanceof IWebLoadListener))
//					continue ;
//				try
//				{
//					IWebLoadListener wll = (IWebLoadListener)ob ;
//					wll.onWebappAllLoaded(wc,module2webconf);
//				}
//				catch(Exception ee)
//				{
//					ee.printStackTrace();
//				}
//			}
//		}
//	}
	
	public static String transAbsPath(String appn,String p)
	{
		if(p==null)
			return null ;
		
		p = p.trim() ;
		if(!p.startsWith("/")&&!p.startsWith("http://"))
		{
			p = "/"+appn+"/"+p ;
		}
		return p ;
	}
	
	
	ClassLoader relatedCl = null ;
	
	private File webPathDir = null ;
	
	private String appName = null ;
	
	private Element confRootEle = null;
	
	private String titleCn = null ;
	
	private String titleEn = null ;
	
	private ArrayList<Object> webLoadLiss = new ArrayList<>() ;
	/**
	 * 
	 * @param b
	 */
	private AppWebConfig(File webf,String appn,ClassLoader cl) //,AppInfo appi)
	{
		webPathDir = webf ;
		relatedCl = cl ;
		appName = appn ;
		
		File conff = new File(webf,"web.xml") ;
		loadConf(conff);
		
		NavApp.loadFromWebConfig(this);
		
		//loadListeners() ;
	}
	
	private Element loadConf(File f)
	{
		if(!f.exists())
			return null ;
		
		if (confRootEle != null)
			return confRootEle;

		synchronized (this)
		{
			if (confRootEle != null)
				return confRootEle;

			try
			{
				DocumentBuilderFactory docBuilderFactory = null;
				DocumentBuilder docBuilder = null;
				Document doc = null;

				// parse XML XDATA File
				docBuilderFactory = DocumentBuilderFactory.newInstance();
				docBuilderFactory.setValidating(false);
				docBuilder = docBuilderFactory.newDocumentBuilder();

				doc = docBuilder.parse(f);

				confRootEle = doc.getDocumentElement();
				
				titleCn = confRootEle.getAttribute("title_cn") ;
				titleEn = confRootEle.getAttribute("title_en") ;
				return confRootEle;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}
	
	public ClassLoader getRelatedCL()
	{
		return this.relatedCl ;
	}
	
	public void loadListeners(List<AppWebConfig> awcs)
	{
		if(confRootEle==null)
			return ;
		
		String liss = confRootEle.getAttribute("listeners") ;
		if(Convert.isNullOrEmpty(liss))
			return ;
		List<String> ss = Convert.splitStrWith(liss, ",") ;
		for(String cn:ss)
		{
			try
			{
				Class<?> c = relatedCl.loadClass(cn) ;
				Object ob = c.getConstructor().newInstance() ;
				webLoadLiss.add(ob) ;
				
				if(ob instanceof IWebLoadListener)
				{
					try
					{
						IWebLoadListener wll = (IWebLoadListener)ob ;
						wll.onWebappAllLoaded(this,module2webconf);
					}
					catch(Exception ee)
					{
						ee.printStackTrace();
					}
				}
			}
			catch (Exception e)
			{
				System.err.println("load web listener failed :"+cn) ;
				e.printStackTrace();
			}
		}
	}
	
	public static Element loadConfElementFromFile(File f) throws Exception
	{
		if(!f.exists())
			return null ;
		
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;

		// parse XML XDATA File
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setValidating(false);
		docBuilder = docBuilderFactory.newDocumentBuilder();

		doc = docBuilder.parse(f);

		return doc.getDocumentElement();
	}
	

	public File getAppDirPath()
	{
		return webPathDir ;
	}
	
	public String getAppName()
	{
		return appName ;
	}
	
	public String getTitleCn()
	{
		return titleCn ;
	}
	
	public String getTitleEn()
	{
		return titleEn ;
	}
	
	
	public Element getRootElement()
	{
		return confRootEle ;
	}
	
	public Element getConfElement(String name)
	{
		if(confRootEle==null)
			return null ;
		
		NodeList nl = confRootEle.getElementsByTagName(name);
		if (nl == null)
			return null;

		return (Element) nl.item(0);
	}
	

}
