package org.iottree.core.ai;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

public class AIApiKey
{
	String name ;
	
	String title ;
	
	String apiHost ;
	
	String chatUrl ;
	
	String modelListUrl ;
	
	String apiKey ;
	
	public AIApiKey(String n,String t,String h,String k)
	{
		StringBuilder fr = new StringBuilder() ;
		if(!Convert.checkVarName(n, fr))
			throw new IllegalArgumentException(fr.toString()) ;
		this.name = n ;
		this.title = t ;
		this.apiHost = h ;
		this.apiKey = k ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		if(this.title==null)
			return "" ;
		return this.title ;
	}
	
	public String getApiHost()
	{
		if(this.apiHost==null)
			return "" ;
		return this.apiHost ;
	}
	
	
	public String getApiKey()
	{
		if(this.apiKey==null)
			return "" ;
		return this.apiKey ;
	}
	
	public String getChatUrl()
	{
		if(this.chatUrl==null)
			return "" ;
		return this.chatUrl ;
	}
	
	public String getModelListUrl()
	{
		if(this.modelListUrl==null)
			return "" ;
		return this.modelListUrl ;
	}
	
	public JSONObject toJO()
	{
		return new JSONObject().put("n",this.name).putOpt("t", this.title)
				.putOpt("api_h", this.apiHost).put("chat_u", this.chatUrl).putOpt("modellist_u", this.modelListUrl)
				.putOpt("api_k",this.apiKey) ;
	}
	
	static AIApiKey fromJO(JSONObject jo)
	{
		String n = jo.optString("n") ;
		if(Convert.isNullOrEmpty(n))
			return null ;
		String t = jo.optString("t") ;
		String u = jo.optString("api_h") ;
		String k = jo.optString("api_k") ;
		AIApiKey ret = new AIApiKey(n,t,u,k) ;
		ret.chatUrl = jo.optString("chat_u") ;
		ret.modelListUrl = jo.optString("modellist_u") ;
		return ret ;
	}
	
	private static  LinkedHashMap<String,AIApiKey> allKeys = null ;
	
	public static LinkedHashMap<String,AIApiKey> listAll()
	{
		if(allKeys!=null)
			return allKeys ;
		
		try
		{
			return allKeys = loadAll() ;
		}
		catch(Exception ee)
		{
			ee.printStackTrace();
			return null ;
		}
	}
	
	public static AIApiKey getApiKey(String name)
	{
		return listAll().get(name) ;
	}
	
	public static AIApiKey setApkKey(JSONObject jo,boolean b_new,StringBuilder failedr) throws Exception
	{
		String n = jo.optString("n") ;
		if(Convert.isNullOrEmpty(n))
		{
			failedr.append("no name input");
			return null ;
		}
		if(!Convert.checkVarName(n, failedr))
			return null ;
		if(b_new)
		{
			AIApiKey old = getApiKey(n) ;
			if(old!=null)
			{
				failedr.append("name="+n+" is already existed");
				return null ;
			}
		}
		
		AIApiKey aak = AIApiKey.fromJO(jo) ;
		if(aak==null)
		{
			failedr.append("invalid AIApiKey json");
			return null ;
		}
		saveApiKey(aak) ;
		listAll().put(aak.name,aak) ;
		return aak ;
	}
	
	public static boolean delApkKey(String name)
	{
		AIApiKey aak = listAll().remove(name) ;
		if(aak==null)
			return false ;
		File f = calcFile(name) ;
		return f.delete() ;
	}
	
	private static File calcFile(String n)
	{
		return new File(Config.getDataDirBase()+"ai/apikey_"+n+".json") ;
	}
	
	private static LinkedHashMap<String,AIApiKey> loadAll() throws IOException
	{
		LinkedHashMap<String,AIApiKey> ret = new LinkedHashMap<>() ;
		File f = new File(Config.getDataDirBase()+"ai/") ;
		if(!f.exists())
			return ret ;
		File[] fs = f.listFiles(new FileFilter() {

			@Override
			public boolean accept(File f)
			{
				if(!f.isFile())
					return false;
				String fn = f.getName() ;
				if(!fn.startsWith("apikey_") || !fn.endsWith(".json"))
					return false;
				return true;
			}}) ;
		for(File tmpf :fs)
		{
			JSONObject tmpjo = Convert.readFileJO(tmpf) ;
			if(tmpjo==null)
				continue ;
			AIApiKey aak = AIApiKey.fromJO(tmpjo) ;
			if(aak==null)
				continue ;
			ret.put(aak.name,aak) ;
		}
		return ret ;
	}
	
	private static void saveApiKey(AIApiKey aak) throws Exception
	{
		File f = calcFile(aak.name) ;
		if(!f.getParentFile().exists())
			f.getParentFile().mkdirs();
		Convert.writeFileJO(f, aak.toJO());
	}
	
}
