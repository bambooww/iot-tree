package org.iottree.core.util;

import java.io.IOException;
import java.io.InputStream;
import java.util.HashMap;

import org.iottree.core.Config;
import org.iottree.core.dict.DataClass;
import org.iottree.core.dict.DataNode;
import org.iottree.core.dict.DictManager;

public class Lan
{
	static HashMap<String, Object> pk2lang = new HashMap<>();

	public static Lan getLangInPk(Class<?> c)
	{
		String pkn = c.getPackage().getName();
		Object ln = pk2lang.get(pkn);
		if (ln != null)
		{
			if (ln instanceof String)
				return null;
			else
				return (Lan) ln;
		}

		synchronized (c)
		{
			ln = pk2lang.get(pkn);
			if (ln != null)
			{
				if (ln instanceof String)
					return null;
				else
					return (Lan) ln;
			}

			Lan lg = null;
			try
			{
				lg = loadLang(c);
			}
			catch ( Exception e)
			{
				e.printStackTrace();
			}
			if (lg == null)
				pk2lang.put(pkn, "");
			else
				pk2lang.put(pkn, lg);
			return lg;
		}
	}

	private static Lan loadLang(Class<?> c) throws Exception
	{
		String pkn = c.getPackage().getName();

		String pkresln = "/" + pkn.replaceAll("\\.", "/") + "/lang.xml";
		// InputStream inputs = null ;

		// inputs = c.getResourceAsStream(pkresln) ;
		// if(inputs==null)
		// {
		// pk2lang.put(pkn,"") ;
		// return null ;
		// }

		DataClass dc = DictManager.loadDataClassByResPath(c, pkresln);
		if (dc == null)
		{
			pk2lang.put(pkn, "");
			return null;
		}
		return new Lan(dc);
	}

	private static String sys_ln = null;
	
	private static ThreadLocal<String> th_lang = new ThreadLocal<>() ;
	
	/**
	 * may be used in jsp page
	 * @param lang
	 */
	public static void setLangInThread(String lang)
	{
		if(Convert.isNotNullEmpty(lang))
			th_lang.set(lang);
		else
			th_lang.remove();
	}
	
	/**
	 * used for install config
	 * @param ln
	 */
	public static void setSysUsingLang(String ln)
	{
		sys_ln = ln ;
	}
	
	public static String getUsingLang()
	{
		String ln = th_lang.get() ;
		if(ln==null)
		{
			ln = sys_ln ;
		}
		
		if(ln==null)
		{
			ln = Config.getAppLang() ;
		}
		
		if(Convert.isNullOrEmpty(ln))
			ln = "en" ;
		return ln ;
	}

	HashMap<String, String> name2val = new HashMap<>();
	DataClass dc = null;

	private Lan(DataClass dc)
	{
		this.dc = dc;
	}

	public String g(String name)
	{
		DataNode dn = dc.getNodeByName(name);
		if (dn == null)
			return "[x" + name + "x]";
		
		return dn.getNameByLang(getUsingLang());
	}
}
