package org.iottree.core.ecp;

import java.io.File;
import java.io.IOException;
import java.util.LinkedHashMap;

import org.iottree.core.ecp.ECPComp.Head;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;


public class ECPCompLibDir extends ECPCompLib
{
	File dir ;
	
	public ECPCompLibDir(String lib_id, String title,File dir)
	{
		super(lib_id, title);
		this.dir = dir ;
	}
	
	public File getLibDir()
	{
		return this.dir; 
	}
	
	protected void saveLibBasic(String title,String desc) throws IOException
	{
		if(!dir.exists())
			dir.mkdirs() ;
		File hf = new File(dir,"_basic.json") ;
		JSONObject jo = new JSONObject().put("id", this.getLibId())
				.put("t", title).putOpt("d", desc) ;
		Convert.writeFileJO(hf, jo);
	}
	
	public static ECPCompLibDir loadLib(String lib_id,File dir) throws IOException
	{
		if(!dir.exists())
			return null ;
		File hf = new File(dir,"_basic.json") ;
		JSONObject jo = Convert.readFileJO(hf) ;
		if(jo==null)
			return null ;
		String tt = jo.optString("t") ;
		ECPCompLibDir r = new ECPCompLibDir(lib_id,tt,dir) ;
		r.lib_desc = jo.optString("d","");
		return r ;
	}
	
	public static ECPCompLibDir addLib(String title,String desc,File dir) throws IOException
	{
		String lib_id = IdCreator.newSeqId() ;
		File libdir = new File(dir,"lib_"+lib_id) ;
		ECPCompLibDir lib = new ECPCompLibDir(lib_id,title,libdir) ;
		lib.lib_desc = desc ;
		lib.saveLibBasic(title,desc);
		return lib ;
	}

	@Override
	public LinkedHashMap<String,ECPComp.Head> loadCompHeads() throws Exception
	{
		LinkedHashMap<String,ECPComp.Head> ret = new LinkedHashMap<>() ;
		if(!dir.exists())
			return ret ;
		
		File hf = new File(dir,"_heads.json") ;
		JSONArray jarr = Convert.readFileJArr(hf) ;
		if(jarr==null)
			return ret ;
		int n = jarr.length() ;
		for(int i = 0 ; i < n ; i ++)
		{
			JSONObject tmpjo = jarr.getJSONObject(i) ;
			Head h = Head.fromJO(this,tmpjo) ;
			if(h==null)
				continue ;
			ret.put(h.comp_id,h) ;
		}
		return ret ;
	}
	
	@Override
	protected void saveCompHeads(LinkedHashMap<String,ECPComp.Head> heads) throws Exception
	{
		File hf = new File(dir,"_heads.json") ;
		JSONArray jarr = new JSONArray() ;
		for(ECPComp.Head h:heads.values())
		{
			jarr.put(h.toJO()) ;
		}
		Convert.writeFileJArr(hf, jarr);
	}

	@Override
	protected JSONObject loadCompById(String comp_id) throws Exception
	{
		File f = new File(dir,"c_"+comp_id+".json") ;
		return Convert.readFileJO(f) ;
	}

	@Override
	protected void saveCompById(String comp_id, JSONObject jo) throws Exception
	{
		File f = new File(dir,"c_"+comp_id+".json") ;
		if(jo==null)
		{
			f.delete();
			return ;
		}
		
		Convert.writeFileJO(f, jo);
	}

	
	
}
