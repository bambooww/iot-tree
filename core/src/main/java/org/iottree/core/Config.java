package org.iottree.core;

import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.PrintWriter;
import java.io.StringWriter;
import java.net.URL;
import java.util.*;

import javax.servlet.jsp.PageContext;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.iottree.core.util.xmldata.XmlHelper;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;


public class Config
{
	protected static ILogger log = LoggerManager.getLogger(Config.class) ;
	
	public static class LangItem
	{
		String lang = null ;
		
		String icon = null ;
		
		boolean bDefault=false ;
		
		public LangItem(String ln,String icon)
		{
			this.lang = ln ;
			this.icon = icon ;
		}
		
		public String getLang()
		{
			return lang ;
		}
		
		public String getIcon()
		{
			return icon ;
		}
		
		public boolean isDefault()
		{
			return bDefault ;
		}
	}
	
	static String configFileBase = null ;
	
	
	static String dataFileBase = null ;

	static String appConfigInitError = "" ;
	
	static String lastConfigError = "" ;
	
	//static boolean appConfigInitSucc = false;
	
	static String dataDirBase = null ;
	
	static String dataDynDirBase = null ;
	
	static String libDirBase = null ;
	
	static
	{
		configFileBase = System.getProperties().getProperty("user.dir");
		try
		{
			configFileBase = new File(configFileBase).getCanonicalPath() ;
		}
		catch(Exception e)
		{
			e.printStackTrace();
		}
	}
	

	
	public static String getAppConfigInitError()
	{
		return appConfigInitError;
	}
	
	public static String getConfigFileBase()
	{
		return configFileBase ;
	}
	
	
	public static File getConfFile(String conffn)
	{
		String sysconffn = configFileBase
		+ "/"+conffn;
		return new File(sysconffn);
	}
	
	public static File getRelatedFile(String path)
	{
		if(!path.startsWith("."))
			return new File(path) ;
		
		String sysconffn = configFileBase
		+ "/"+path;
		return new File(sysconffn);
	}
	
	
	private static String webappBase = null ;
	/**
	 * getWebappBase
	 * @return
	 */
	public static String getWebappBase()
	{
		if(webappBase!=null)
			return webappBase;
		
		Element wappsele = getConfElement("webapps") ;
		if(wappsele==null)
			throw new RuntimeException("no webapps config element found") ;
		
		String tmps = wappsele.getAttribute("base_dir");
		if(tmps!=null&&!tmps.equals(""))
		{
			try
			{
				tmps = getRelatedFile(tmps).getCanonicalPath();
				webappBase = tmps ;
			}
			catch(Exception ee)
			{
				throw new RuntimeException(ee.getMessage());
			}
		}
		else
		{
			webappBase= configFileBase + "/web/";
		}
		
		return webappBase;
	}
	
	public static class Webapp
	{
		String appName = null ;
		
		String path = null ;
		
		boolean bMain=false;
		
		private Webapp(String appn,String path,boolean b_main)
		{
			this.appName = appn ;
			this.path = path ;
			this.bMain = b_main ;
		}
		
		public String getAppName()
		{
			return appName ;
		}
		
		public String getPath()
		{
			return this.path ;
		}
		
		public boolean isMain()
		{
			return this.bMain ;
		}
	}
	
	public static class Webapps
	{
		int port = 80 ;
		
		int ajpPort = -1 ;
		
		int sslPort = -1 ;
		
		ArrayList<Webapp> webapps = new ArrayList<Webapp>() ;
		
		public Webapps()
		{
			
		}
		
		public int getPort()
		{
			return port ;
		}
		
		public int getAjpPort()
		{
			return ajpPort ;
		}
		
		public int getSslPort()
		{
			return sslPort; 
		}
		
		public List<Webapp> getAppList()
		{
			return webapps ;
		}
		
		public Webapp getApp(String name)
		{
			for(Webapp w:this.webapps)
			{
				if(name.equals(w.appName))
					return w ;
			}
			return null ;
		}
		
		public Webapp getMainApp()
		{
			for(Webapp w:this.webapps)
			{
				if(w.bMain)
					return w ;
			}
			return null ;
		}
	}
	
	public static Webapps getWebapps()
	{
		Element wappsele = getConfElement("webapps") ;
		if(wappsele==null)
			return null ;
		
		Webapps r = new Webapps() ;
		r.port = Convert.parseToInt32(wappsele.getAttribute("port"),80) ;
		r.ajpPort = Convert.parseToInt32(wappsele.getAttribute("ajp_port"),-1) ;
		r.sslPort = Convert.parseToInt32(wappsele.getAttribute("ssl_port"),-1) ;
		
		Element[] weles = XmlHelper.getSubChildElement(wappsele, "webapp") ;
		if(weles!=null)
		{
			for(Element wele:weles)
			{
				String appn = wele.getAttribute("name") ;
				String path = wele.getAttribute("path") ;
				boolean bload = !"false".equalsIgnoreCase(wele.getAttribute("load")) ;
				if(!bload)
					continue ;
				boolean bmain = "true".equals(wele.getAttribute("main")) ;
				r.webapps.add(new Webapp(appn,path,bmain)) ;
			}
		}
		return r ;
	}
	
	public static Webapp getWebappMain()
	{
		Webapps ws = getWebapps() ;
		if(ws==null)
			return null ;
		return ws.getMainApp() ;
	}
	/**
	 * ������е�ģ������
	 * @return
	 */
	public static ArrayList<String> getWebappModules()
	{
		ArrayList<String> rets = new ArrayList<String>() ;
		String twb = getWebappBase() ;
		if(Convert.isNullOrEmpty(twb))
			return rets ;
		File f = new File(twb) ;
		if(!f.exists())
			return rets ;
		Webapps w = getWebapps() ;
		for(File tmpf:f.listFiles())
		{
			if(!tmpf.isDirectory())
				continue ;
			String n = tmpf.getName() ;
			if(w!=null && w.getApp(n)==null)
				continue ;
				
			rets.add(tmpf.getName()) ;
		}
		return rets ;
	}
	
	/**
	 * ��ϵͳ�ڲ�����server�����ipͳһ����
	 * @return
	 */
	public static String getBindIP()
	{
		Element tmpe = loadConf() ;
		return tmpe.getAttribute("bind_ip");
	}
	
	
//	public static int getTomatoServerPort()
//	{
//		Element tmpe = loadConf() ;
//		String tmps = tmpe.getAttribute("tomato_port");
//		if(tmps!=null&&!tmps.equals(""))
//		{
//			return Integer.parseInt(tmps);
//		}
//		
//		return -1 ;
//	}
	
	
//	public static int getTomatoServerCtrlPort()
//	{
//		if(isSole())
//			throw new RuntimeException("app is running in sole mode!");
//		
//		Element tmpe = loadConf() ;
//		String tmps = tmpe.getAttribute("tomato_ctrl_port");
//		if(tmps!=null&&!tmps.equals(""))
//		{
//			return Integer.parseInt(tmps);
//		}
//		
//		return -1 ;
//	}
//	
//	
//	public static int getGridServerPort()
//	{
//		if(isSole())
//			throw new RuntimeException("app is running in sole mode!");
//		
//		Element tmpe = loadConf() ;
//		String tmps = tmpe.getAttribute("grid_port");
//		if(tmps!=null&&!tmps.equals(""))
//		{
//			return Integer.parseInt(tmps);
//		}
//		
//		return -1 ;
//	}
	
	public static String getDataDirBase()
	{
		if(dataDirBase!=null)
				return dataDirBase;
			
		return dataFileBase + "/data/";
	}
	
	public static String getDataDynDirBase()
	{
		if(Convert.isNullOrEmpty(dataDynDirBase))
			throw new RuntimeException("no data_dyn_dir found or dir is not existed in config.xml") ;
		
		return dataDynDirBase ;
	}
	
	public static String getLibDirBase()
	{
		return libDirBase ;
	}
	
	public static File getDataBackupDir()
	{
		String dir = getDataDirBase()+"backup/" ;
		File r= new File(dir) ;
		return r ;
	}
	
	public static String getDataTmpDir()
	{
		return getDataDirBase()+"tmp/" ;
	}
	
	public static String getDataOthersDir()
	{
		return getDataDirBase()+"others/" ;
	}
	/**
	 * ����ҳ��������,���ĳһ��·������ʵ�ļ�·��
	 * @param pc
	 * @param p
	 * @return
	 */
	public static File getWebPageContextRealPath(PageContext pc,String p)
	{
		File realp = null;
		if(pc==null)
		{//����ʹ��tomatoƽ̨��Ϣ

			String tmpp = getWebappBase()+p ;
			realp = new File(tmpp) ;
			if(realp.exists())
				return realp ;
			
			return realp ;
		}
		// ����ͨ����webapp�����Ļ�ȡ����·��
		//HttpServletRequest hsr = (HttpServletRequest) pc.getRequest();

		String tmpp = pc.getServletContext().getRealPath(p);
		File f = new File(tmpp);
		if (f.exists())
		{
			realp = f;
//			cxtRoot = WebRes
//					.getContextRootPath((HttpServletRequest) this.pageContext
//							.getRequest());
		}

		if (realp == null && p.startsWith("/"))
		{// tomato��ʽ��,ͨ��ȫ������
			tmpp = Config.getWebappBase();
			File tmpf = new File(tmpp, p);
			if (tmpf.exists())
			{
				realp = tmpf;
//				cxtRoot = "/";
			}
		}

		if (realp == null)
			throw new RuntimeException("no template found with path input=" + p);

		return realp ;
	}
	
	/**
	 * ��ȡһ��ҳ��·���е��ı�����
	 * @param pc
	 * @param p
	 * @param enc
	 * @return
	 */
	public static String readWebPageTxtByPath(PageContext pc,String p,String enc)
	{
		File f = getWebPageContextRealPath(pc,p) ;
		if(f==null)
			return null ;
		
		if(!f.exists())
			return null ;
		
			FileInputStream fis = null;
			if (enc == null || enc.equals(""))
				enc = "UTF-8";

			try
			{
				fis = new FileInputStream(f);
				byte[] buf = new byte[(int) f.length()];
				fis.read(buf);
				String s = new String(buf, enc);
				//s = s.replaceAll("\\[\\$CXT_ROOT\\]", cxt_root);
				return s ;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
			finally
			{
				try
				{
					if (fis != null)
						fis.close();
				}
				catch (IOException ioe)
				{
				}
			}
		
	}
	
	private static Object locker = new Object();

	private static Element confRootEle = null;
	
	private static boolean bDebug = false;
	
	/**
	 * 
	 */
	private static boolean bAuthDefaultAllow = true;
	
	/**
	 * �ж�ҳ��ȱʡ������Ƿ���Ҫ��½
	 */
	private static boolean bAuthDefaultLogin = false;
	
	private static String appTitle = "IOT-Tree Server" ;
	
	private static String serverId = "" ;
	/**
	 * һЩ����£���������Ҫ֪���Լ�����α������ʵ�
	 * ����Ϣ���Ǵ洢����������ķ���ǰ׺
	 */
	private static ArrayList<String> httpBases = new ArrayList<String>() ;
	
	private static String appLang = "en";
	
	/**
	 * ������֧��
	 */
	private static ArrayList<LangItem> multiLangItems = new ArrayList<>() ;
	
	private static String appCopyRight = "" ;
	
	public static Element loadConf()
	{
		if (confRootEle != null)
			return confRootEle;

		synchronized (locker)
		{
			if (confRootEle != null)
				return confRootEle;

			try
			{
				File f = getConfFile("config.xml");
				if (!f.exists())
				{
					throw new RuntimeException("no config.xml file found!");
				}
				
				DocumentBuilderFactory docBuilderFactory = null;
				DocumentBuilder docBuilder = null;
				Document doc = null;

				// parse XML XDATA File
				docBuilderFactory = DocumentBuilderFactory.newInstance();
				docBuilderFactory.setValidating(false);
				docBuilder = docBuilderFactory.newDocumentBuilder();

				doc = docBuilder.parse(f);

				confRootEle = doc.getDocumentElement();
				
				String tdata = confRootEle.getAttribute("data_dir") ;
				if(Convert.isNotNullEmpty(tdata))
				{
					tdata = tdata.replace('\\', '/');
					File fp = null ;
					if(tdata.startsWith("/")||tdata.indexOf(':')>0)
					{
						fp = new File(tdata) ;
					}
					else
					{
						fp = new File(configFileBase+"/"+tdata) ;
					}
					
					fp.mkdirs();
					dataDirBase = fp.getCanonicalPath() ;
					dataDirBase = dataDirBase.replace('\\', '/');
					if(!dataDirBase.endsWith("/"))
						dataDirBase += "/" ;
					
					dataFileBase = fp.getParentFile().getCanonicalPath() +"/";
				}
				else
				{
					dataFileBase = f.getParentFile().getCanonicalPath() +"/";
				}
				
				System.out.println("Data Dir Base="+dataDirBase) ;
				System.setProperty("iottree.data_dir",getDataDirBase());
				
				dataDynDirBase = confRootEle.getAttribute("data_dyn_dir") ;
				if(Convert.isNotNullEmpty(dataDynDirBase))
				{
					File tmpf = new File(dataDynDirBase) ;
					if(!tmpf.exists())
					{
						System.out.println(" Warn: no dyn dir found="+dataDynDirBase) ;
						dataDynDirBase = null ;
					}
					
					dataDynDirBase = tmpf.getCanonicalPath()+File.separatorChar ;
					
				}
				
				if(Convert.isNotNullEmpty(dataDynDirBase))
					System.out.println("  data_dyn_dir="+dataDynDirBase) ;
				else
					System.out.println("  data_dyn_dir is not set") ;
				
				tdata = confRootEle.getAttribute("lib_dir") ;
				if(Convert.isNotNullEmpty(tdata))
				{
					tdata = tdata.replace('\\', '/');
					File fp = null ;
					if(tdata.startsWith("/")||tdata.indexOf(':')>0)
					{
						fp = new File(tdata) ;
					}
					else
					{
						fp = new File(configFileBase+"/"+tdata) ;
					}
					
					fp.mkdirs();
					libDirBase = fp.getCanonicalPath()+"/" ;
					libDirBase = libDirBase.replace('\\', '/');
				}
				else
				{
					libDirBase = f.getParentFile().getCanonicalPath() +"/lib/";
					libDirBase = libDirBase.replace('\\', '/');
				}
				System.out.println("Lib Dir Base="+libDirBase) ;
				
				File tmpdir = new File(getDataDirBase()+"/tmp/") ;
				if(!tmpdir.exists())
					tmpdir.mkdirs() ;
				System.setProperty("iottree.tmp_dir",getDataDirBase()+"/tmp/");
				File javaiotemp = new File(getDataDirBase()+"/tmp_java_io/") ;
				if(!javaiotemp.exists())
					javaiotemp.mkdirs() ;
				System.setProperty("java.io.tmpdir",javaiotemp.getCanonicalPath());
				System.out.println("Data File Base="+dataFileBase) ;
				
				bDebug = "true".equalsIgnoreCase(confRootEle.getAttribute("debug"));
				appTitle = confRootEle.getAttribute("title");
				if(appTitle==null||appTitle.equals(""))
					appTitle = "IOT-Tree Server" ;
				
				appCopyRight = confRootEle.getAttribute("copyright");
				if(appCopyRight==null)
					appCopyRight = "" ;
				
				
				appLang = confRootEle.getAttribute("lang");
				if(Convert.isNullOrEmpty(appLang))
					appLang = "en";
				
				
				 loadLangsConfig();
				
				Element userele = getConfElement("user");
				if(userele!=null)
				{
					bAuthDefaultAllow = true;//!AppWebConfig.ATTRV_DENY.equalsIgnoreCase(userele.getAttribute("authorization_default"));
					bAuthDefaultLogin = "true".equalsIgnoreCase(userele.getAttribute("authorization_is_login"));
				}
				
				loadSystemConfig();
				
				return confRootEle;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				
				StringWriter sw = new StringWriter();
				PrintWriter pw = new PrintWriter(sw);
				e.printStackTrace(pw);
				lastConfigError = e.getMessage()+"\r\n"+sw.toString();
				return null;
			}
		}
	}
	
	
	private static void loadLangsConfig()
	{
		Element langsele = getConfElement("langs");
		if(langsele==null)
			return ;
		Element[] leles = Convert.getSubChildElement(langsele, "lang") ;
		for(Element ele:leles)
		{
			String n = ele.getAttribute("name") ;
			String icon = ele.getAttribute("icon") ;
			boolean bdefault = "true".equalsIgnoreCase(ele.getAttribute("default")) ;
			LangItem li = new LangItem(n,icon) ;
			if(bdefault)
				li.bDefault = true ;
			multiLangItems.add(li) ;
		}
	}
	
	private static void loadSystemConfig() throws IOException
	{
		Element sysele = getConfElement("system");
		
		
		Element[] hbdoms = Convert.getSubChildElement(sysele, "http_base") ;
		if(hbdoms!=null)
		{
			for(Element hbd:hbdoms)
			{
				String u = hbd.getAttribute("url") ;
				if(Convert.isNotNullEmpty(u))
				{
					httpBases.add(u) ;
				}
			}
		}

		Element[] envs = Convert.getSubChildElement(sysele, "env") ;
		if(envs!=null)
		{
			for(Element env:envs)
			{
				String n = env.getAttribute("name") ;
				if(Convert.isNullOrEmpty(n))
					continue ;
				
				String v = env.getAttribute("value");
				if(v==null)
					v = "" ;
				
				System.setProperty(n, v);
			}
		}
		
//		Element[] libdirs = Convert.getSubChildElement(sysele, "lib") ;
//		StringBuilder sb = new StringBuilder() ;
//		String pathsep = System.getProperty("path.separator") ;
//		if(Convert.isNullOrEmpty(pathsep))
//			pathsep = ";" ;
//		
//		if(libdirs!=null)
//		{
//			for(Element libd:libdirs)
//			{
//				String dir = libd.getAttribute("dir") ;
//				if(Convert.isNullOrEmpty(dir))
//					continue ;
//				
//				File f = new File(dir) ;
//				if(!f.exists())
//					continue ;
//				
//				if(!f.isDirectory())
//					continue ;
//				
//				File[] fs = f.listFiles(new FilenameFilter(){
//
//					public boolean accept(File dir, String name)
//					{
//						String n = name.toLowerCase() ;
//						if(n.endsWith(".jar"))
//							return true ;
//						if(n.endsWith(".zip"))
//							return true ;
//						return false;
//					}}) ;
//				
//				if(fs==null||fs.length<=0)
//					continue ;
//				
//				for(File tmpf : fs)
//				{
//					sb.append(tmpf.getCanonicalPath()).append(pathsep) ;
//				}
//				
//			}
//		}
//		
//		sb.append(System.getProperty("java.class.path")) ;
//		System.setProperty("java.class.path", sb.toString()) ;
		
		//System.out.println("java.class.path="+System.getProperty("java.class.path")) ;
	}
	
	
	public static String getLastConfigError()
	{
		return lastConfigError;
	}
	
	public static boolean isDebug()
	{
		return bDebug ;
	}
	
	public static String getServerId()
	{
		return serverId ;
	}
	
	public static String getAppTitle()
	{
		return appTitle ;
	}
	

	public static List<String> getHttpBases()
	{
		return httpBases;
	}
	
	/**
	 * �����Ի����£���ö�������Ŀ�б�
	 * @return
	 */
	public static List<LangItem> getMultiLangItems()
	{
		return multiLangItems;
	}

	public static LangItem getMultiLangDefault()
	{
		if(multiLangItems==null||multiLangItems.size()<=0)
			return null ;
		
		for(LangItem li:multiLangItems)
		{
			if(li.bDefault)
				return li ;
		}
		return multiLangItems.get(0) ;
	}
	
	
	public static String getAppCopyRight()
	{
		return appCopyRight ;
	}
	
	public static String getAppLang()
	{
		return appLang ;
	}
	

	public static boolean isAuthDefaultLogin()
	{
		return bAuthDefaultLogin ;
	}
	

	public static boolean isAuthDefaultAllow()
	{
		return bAuthDefaultAllow ;
	}

	public static Element getConfElement(String name)
	{
		Element re = loadConf();
		if(re==null)
			return null ;
		NodeList nl = re.getElementsByTagName(name);
		if(nl==null||nl.getLength()<=0)
		{//try load debug
			re = loadConfDebug();
			if(re!=null)
				nl = re.getElementsByTagName(name);
		}
		
		if (nl == null)
			return null;

		return (Element) nl.item(0);
	}
	
	static Element confRootEleDebug = null ; 
	
	private static Element loadConfDebug()
	{
		if (confRootEleDebug != null)
			return confRootEleDebug;

		synchronized (locker)
		{
			if (confRootEleDebug != null)
				return confRootEleDebug;

			try
			{
				File f = getConfFile("app_debug.xml");
				
				if(!f.exists())
					return null;//

				DocumentBuilderFactory docBuilderFactory = null;
				DocumentBuilder docBuilder = null;
				Document doc = null;

				// parse XML XDATA File
				docBuilderFactory = DocumentBuilderFactory.newInstance();
				docBuilderFactory.setValidating(false);
				docBuilder = docBuilderFactory.newDocumentBuilder();

				doc = docBuilder.parse(f);

				confRootEleDebug = doc.getDocumentElement();
				return confRootEleDebug;
			}
			catch(Exception ee)
			{
				ee.printStackTrace();
				return null ;
			}
		}
	}
	
	
	public static Element getConfElementDebug(String name)
	{
		Element re = loadConfDebug();
		NodeList nl = re.getElementsByTagName(name);
		if (nl == null)
			return null;

		return (Element) nl.item(0);
	}
	
	
	public static String transConfPath(String p)
	{
		if(p.startsWith("[$"))
		{
			if(p.startsWith("[$data]"))
				return Config.getDataDirBase()+"/"+p.substring(7) ;
			else if(p.startsWith("[$webapps]"))
				return Config.getWebappBase()+"/"+p.substring(10) ;
			throw new IllegalArgumentException("unknown conf path="+p) ;
		}
		return p ;
	}
	
	private static String version;
	 
    public static String getVersion()
    {
    	if(version!=null)
    		return version ;
       try
       {
           String res = "META-INF/maven/org.iottree/iottree-core/pom.properties";
           URL url = Thread.currentThread().getContextClassLoader().getResource(res);
           try(InputStream inputs = url.openStream())
           {
        	   HashMap<String,String> props = Convert.readStringMapFromStream(inputs, "utf-8") ;
        	   version = props.get("version");
           }
       }
       catch(Exception e)
       {
    	   e.printStackTrace();
    	   version="" ;
       }
       return version;
   }
    
   public static class InnerComp
   {
	   String name = null ;
	   
	   boolean bEnable=true ;
	   
	   private InnerComp(String n,boolean ben)
	   {
		   this.name = n ;
		   this.bEnable = ben ;
	   }
   }
   
    
    public static InnerComp getInnerComp(String name)
    {
    	Element ele = Config.getConfElement("system") ;
    	if(ele==null)
    		return null ;
    	for(Element ele0 :XmlHelper.getSubChildElement(ele, "inner_comp"))
    	{
    		String nn = ele0.getAttribute("name") ;
    		if(!name.equals(nn))
    			continue ;
    		
    		boolean ben = !"false".equals(ele0.getAttribute("enable")) ;
    		return new InnerComp(nn,ben) ;
    	}
    	return null ;
    }
}
