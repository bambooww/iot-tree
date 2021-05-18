package org.iottree.core.util.dict;

import java.io.ByteArrayInputStream;
import java.io.File;
import java.io.FilenameFilter;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.io.OutputStreamWriter;
import java.io.UnsupportedEncodingException;
import java.net.URL;
import java.util.*;

import javax.servlet.ServletRequest;
import javax.servlet.http.HttpServletRequest;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.xml.sax.SAXException;

public class DictManager
{

	private static Object locker = new Object();

	private static DictManager dictMgr = null;
	

	public static DictManager getInstance()
	{
		if(dictMgr!=null)
			return dictMgr ;
		
		synchronized (locker)
		{
			if(dictMgr!=null)
				return dictMgr ;
			
			try
			{
				dictMgr = new DictManager() ;
				return dictMgr ;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	static FilenameFilter DD_FF = new FilenameFilter()
	{

		public boolean accept(File dir, String name)
		{
			String n = name.toLowerCase();
			return n.startsWith("dd_") && n.endsWith(".xml");
		}
	};
	
	static class ModuleDictMap
	{
		HashMap<Integer, DataClass> id2class = new HashMap<Integer, DataClass>();

		HashMap<String, DataClass> name2class = new HashMap<String, DataClass>();
	}

	HashMap<Integer, DataClass> id2class = new HashMap<Integer, DataClass>();

	HashMap<String, DataClass> name2class = new HashMap<String, DataClass>();
	
	HashMap<String,ModuleDictMap> module2dict = new HashMap<String,ModuleDictMap>() ;

	private DictManager() throws Exception,
			IOException
	{
		String fp = Config.getDataDirBase() + "dict/";
		

		for (File tmpf : listDDFiles(fp))
		{
			DataClass dc = loadDataClass(null,tmpf);

			id2class.put(dc.getClassId(), dc);
			name2class.put(dc.getClassName(), dc);
		}
		
		//装载模块中的字典
		String webappr = Config.getWebappBase() ;
		ArrayList<String> mus = Config.getWebappModules() ;
		for(String mn:mus)
		{
			String mfp = webappr+"/"+mn+"/WEB-INF/dict/" ;
			
			ModuleDictMap mdm = module2dict.get(mn) ;
			if(mdm==null)
			{
				mdm = new ModuleDictMap() ;
				module2dict.put(mn,mdm) ;
			}
			
			for (File tmpf : listDDFiles(mfp))
			{
				DataClass dc = loadDataClass(mn,tmpf);

				mdm.id2class.put(dc.getClassId(), dc);
				mdm.name2class.put(dc.getClassName(), dc);
			}
		}
	}
	
	private File[] listDDFiles(String dir)
	{
		File f = new File(dir);
		if (!f.exists())
			return new File[0];

		if (!f.isDirectory())
			return new File[0];

		File[] fs = f.listFiles(DD_FF);
		if (fs == null)
			return new File[0];
		return fs ;
	}

	public static DataClass loadDataClass(String modulen,File tmpf)
			throws Exception
	{
		if (!tmpf.exists())
			return null;
		return new DataClass(modulen,tmpf);
	}
	
	
	public static DataClass loadLangDataClassByClass(Class c)
	throws Exception
	{
		String cnn = c.getCanonicalName();
		cnn = "/"+cnn.replace('.', '/')+".lang.xml" ;
		DataClass dc = loadDataClassByResPath(c,cnn) ;
		if(dc!=null)
		{
			String ulang = System.getProperty("user.language") ;
			if("zh".equals(ulang)||"cn".equals(ulang))
				ulang = "cn" ;
			else
				ulang = "en" ;
			
			dc.setDefaultLang(ulang) ;
		}
		
		return dc ;
	}
	/**
	 * 根据资源路径(形如： /com/dw/xxx.xml),获得对应的字典类
	 * @param resp
	 * @return
	 * @throws Exception
	 */
	public static DataClass loadDataClassByResPath(Class c,String resp)
	throws Exception
	{
		URL u = c.getResource(resp) ;
		if(u==null)
			return null ;
		
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;

		// parse XML XDATA File
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setValidating(false);
		docBuilder = docBuilderFactory.newDocumentBuilder();

		doc = docBuilder.parse(u.toString());

		Element rootele = doc.getDocumentElement();

		DataClass dc = new DataClass(null,rootele);
		return dc;
	}
	
	
	public static DataClass loadDataClass(InputStream inputs)
	throws Exception
	{
		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;

		// parse XML XDATA File
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setValidating(false);
		docBuilder = docBuilderFactory.newDocumentBuilder();

		doc = docBuilder.parse(inputs);

		Element rootele = doc.getDocumentElement();

		DataClass dc = new DataClass(null,rootele);
		return dc;
	}
	
	public static void writeDataClass(DataClass dc,OutputStream outputs) throws Exception
	{
		OutputStreamWriter osw = new OutputStreamWriter(outputs,"UTF-8");
		dc.writeToXml(osw);
		osw.flush();
	}

	public static DataClass loadDataClass(String modulen,byte[] cont)
			throws Exception
	{
		if (cont == null && cont.length <= 0)
			return null;

		DocumentBuilderFactory docBuilderFactory = null;
		DocumentBuilder docBuilder = null;
		Document doc = null;

		// parse XML XDATA File
		docBuilderFactory = DocumentBuilderFactory.newInstance();
		docBuilderFactory.setValidating(false);
		docBuilder = docBuilderFactory.newDocumentBuilder();

		doc = docBuilder.parse(new ByteArrayInputStream(cont));

		Element rootele = doc.getDocumentElement();

		DataClass dc = new DataClass(modulen,rootele);
		return dc;
	}

	public DataClass[] getAllDataClasses()
	{
		DataClass[] rets = new DataClass[id2class.size()];
		id2class.values().toArray(rets);
		return rets;
	}
	
	

	/**
	 * 根据类的id获得对应的类对象
	 * 
	 * @param cid
	 * @return
	 */
	public DataClass getDataClass(int cid)
	{
		return id2class.get(cid);
	}

	/**
	 * 根据类的唯一名称,获得类的对象
	 * 
	 * @param classn
	 * @return
	 */
	public DataClass getDataClass(String classn)
	{
		return name2class.get(classn);
	}
	
	/**
	 * 获取存在字典的所有模块名称
	 * @return
	 */
	public ArrayList<String> getAllHasDictModules()
	{
		ArrayList<String> rets = new ArrayList<String>() ;
		for(Map.Entry<String, ModuleDictMap> n2mdm:module2dict.entrySet())
		{
			if(n2mdm.getValue().id2class.size()<=0)
				continue ;
			
			rets.add(n2mdm.getKey()) ;
		}
		return rets ;
	}
	
	/**
	 * 获得某个模块下的所有字典类
	 * @param modulen
	 * @return
	 */
	public DataClass[] getAllDataClasses(String modulen)
	{
		ModuleDictMap mdm = module2dict.get(modulen) ;
		if(mdm==null)
			return null ;
		
		DataClass[] rets = new DataClass[mdm.id2class.size()];
		mdm.id2class.values().toArray(rets);
		return rets;
	}
	
	public DataClass getDataClass(String modulen,int cid)
	{
		ModuleDictMap mdm = module2dict.get(modulen) ;
		if(mdm==null)
			return null ;
		
		return mdm.id2class.get(cid) ;
	}
	
	public DataClass getDataClass(String modulen,String classn)
	{
		ModuleDictMap mdm = module2dict.get(modulen) ;
		if(mdm==null)
			return null ;
		
		return mdm.name2class.get(classn) ;
	}
	
	
	/**
	 * 根据http请求和字典类id获得对应的模块中定义字典类
	 * @param req
	 * @param cid
	 * @return
	 */
	public DataClass getModuleDataClass(HttpServletRequest req,int cid)
	{
		String mn = Convert.getModuleNameByHttpReq(req);
		if(mn==null)
			return null ;
		return getDataClass(mn,cid) ;
	}
	
	/**
	 * 根据Http请求和字典类名称获得模块中定义的字典类
	 * @param req
	 * @param classn
	 * @return
	 */
	public DataClass getModuleDataClass(HttpServletRequest req,String classn)
	{
		String mn =  Convert.getModuleNameByHttpReq(req);
		if(mn==null)
			return null ;
		return getDataClass(mn,classn) ;
	}
	
}