package org.iottree.core.devtree;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.iottree.core.Config;
import org.iottree.core.util.Convert;
import org.json.JSONObject;

/**
 * for edge AI run task which can be update by center AI
 * 
 * @author jason.zhu
 */
public class DTClsManager
{
	private static DTClsManager instance = null ;
	
	private static File CLS_DIR = new File(Config.getDataDirBase()+"/devtree/clss/") ;
	
	public static DTClsManager getInstance()
	{
		if(instance!=null)
			return instance ;
		
		synchronized(DTClsManager.class)
		{
			if(instance!=null)
				return instance ;
			
			if(!CLS_DIR.exists())
				CLS_DIR.mkdirs() ;
			
			return instance = new DTClsManager() ;
		}
	}
	
	private static File calClsFile(List<String> cls_path)
	{
		if(cls_path==null||cls_path.size()<=0)
			throw new IllegalArgumentException("no cls path") ;
		
		return new File(CLS_DIR,Convert.combineWith(cls_path, '/')+".json") ;
	}
	
	static DTCls loadCls(String cls_path)// throws Exception
	{
		List<String> ss = Convert.splitStrWith(cls_path, "./") ;
		File f = calClsFile(ss);
		if(!f.exists())
			return null ;
		
		JSONObject jo=null;
		try
		{
			jo = Convert.readFileJO(f);
		}
		catch ( IOException e)
		{
			e.printStackTrace();
		}
		
		if(jo==null)
			return null ;
		
		DTCls cls = DTCls.fromClsJO(cls_path,jo) ;
		if(cls==null)
			return null ;
		return cls ;
	}
	
	private static HashMap<String,DTCls> PATH2CLS = new HashMap<>() ;
	
	public static DTCls getOrLoadCls(String cls_path)
	{
		if(Convert.isNullOrEmpty(cls_path))
			return null ;
		DTCls ret = PATH2CLS.get(cls_path) ;
		if(ret!=null)
			return ret ;
		
		synchronized(DTClsManager.class)
		{
			ret = PATH2CLS.get(cls_path) ;
			if(ret!=null)
				return ret ;

			ret = loadCls(cls_path) ;
			if(ret==null)
				return null ;
			PATH2CLS.put(cls_path,ret) ;
			return ret ;
		}
	}
	
	ArrayList<DTCls> devClss = new ArrayList<>() ;
	
	private DTClsManager()
	{
		
	}
	
	
	
	public List<DTCls> listDevDefs()
	{
		return devClss ;
	}
}
