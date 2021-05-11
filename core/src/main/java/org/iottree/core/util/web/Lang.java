package org.iottree.core.util.web;

import java.io.File;
import java.io.FileInputStream;
import java.io.FilenameFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.servlet.*;
import javax.servlet.http.*;
import javax.servlet.jsp.*;
import javax.xml.parsers.ParserConfigurationException;

import org.iottree.core.util.dict.DataClass;
import org.iottree.core.util.dict.DataNode;
import org.iottree.core.util.dict.DictManager;
import org.xml.sax.SAXException;

/**
 * 
 * @author zzj
 *
 */
public class Lang
{
	static Object locker = new Object();

	private static HashMap<Class<?>, Lang> jspClass2Lang = new HashMap<>();

	
	private static HashMap<String, Class<?>> reqUri2JspClass = new HashMap<>();

	public static Lang getPageLang(Servlet jspp, HttpServletRequest req)
	{
		Class<?> jspc = jspp.getClass();
		Lang wpl = (Lang) jspClass2Lang.get(jspc);
		if (wpl != null)
		{
			return wpl;
		}

		synchronized (locker)
		{
			wpl = (Lang) jspClass2Lang.get(jspc);
			if (wpl != null)
				return wpl;

			try
			{
				Class tmpc = reqUri2JspClass.get(req.getRequestURI());
				if (tmpc != null)
				{
					System.out.println("remove page lang by old="
							+ req.getRequestURI());
					jspClass2Lang.remove(tmpc);
				}

				String realpath = jspp.getServletConfig().getServletContext()
						.getRealPath(req.getServletPath());
				File f = new File(realpath);
				File langf = new File(f.getAbsolutePath() + ".lang");

				if (!langf.exists())
					return null;

				wpl = new Lang(langf, null);

				//System.out.println("add new page lang=" + req.getRequestURI());
				reqUri2JspClass.put(req.getRequestURI(), jspc);
				jspClass2Lang.put(jspc, wpl);

				return wpl;
			}
			catch (Exception e)
			{
				e.printStackTrace();
				return null;
			}
		}
	}

	static class LangFilenameFilter implements FilenameFilter
	{
		private String jspFN = null;

		public LangFilenameFilter(String jspfn)
		{
			jspFN = jspfn.toLowerCase();
		}

		public boolean accept(File dir, String name)
		{
			String tmpn = name.toLowerCase();
			if (!tmpn.startsWith(jspFN))
				return false;

			return tmpn.endsWith(".lang");
		}
	}

	String defaultLang = "cn";

	DataClass langDC = null;

	Lang(File langf, String default_lan)
			throws Exception
	{
		try(FileInputStream fis = new FileInputStream(langf))
		{
			langDC = DictManager.loadDataClass(fis) ;
		}
		if (default_lan != null)
			defaultLang = default_lan;
	}

//	WebPageLang(byte[] langf, String default_lan)
//			throws Exception
//	{
//		langDC = DictManager.loadDataClass(null,langf);
//		if (default_lan != null)
//			defaultLang = default_lan;
//	}

	public String getLangValue(String key)
	{
		if (langDC == null)
			return "[X]" + key + "[X]";
		DataNode dn = langDC.findDataNodeByName(key);
		if (dn == null)
			return "[X]" + key + "[X]";
		String tmps = dn.getNameByLang(defaultLang);
		if (tmps != null)
			return tmps;

		return "[X]" + key + "[X]";
	}

	public DataNode getLangDataNode(String key)
	{
		if (langDC == null)
			return null;

		return langDC.findDataNodeByName(key);
	}
	
	public DataClass getLangDataClass()
	{
		return langDC ;
	}
	
	public List<DataNode> listLangDataNodesByPrefix(String prefix_n)
	{
		if(langDC==null)
			return new ArrayList<DataNode>() ;
		
		ArrayList<DataNode> rets = new ArrayList<DataNode>() ;
		DataNode[] dns = langDC.getRootNodes() ;
		if(dns==null||dns.length<=0)
			return rets ;
		for(DataNode dn:dns)
		{
			if(dn.getName().startsWith(prefix_n))
				rets.add(dn) ;
		}
		return rets ;
	}

	public String getLangValue(String key, String lang)
	{
		if (langDC == null)
			return "[X]" + key + "[X]";

		if (lang == null)
		{// ʹ��ҳ�涨�������
			lang = defaultLang;
		}

		DataNode dn = langDC.findDataNodeByName(key);
		if (dn == null)
			return "[X]" + key + "[X]";
		String tmps = dn.getNameByLang(lang);
		if (tmps != null)
			return tmps;
		return "[X]" + key + "[X]";
	}
}
