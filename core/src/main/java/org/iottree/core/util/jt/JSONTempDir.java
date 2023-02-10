package org.iottree.core.util.jt;

import java.io.File;
import java.io.FileFilter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;


public class JSONTempDir
{
	String id = null ;
	
	File tempDir = null ;

	//HashMap<String, JSONTempOb> name2jsontemp_ob = new HashMap<>();
	
	LinkedHashMap<String, JSONTemp> name2jsontemp = new LinkedHashMap<>();
	
	//HashMap<String,LinkedHashMap<String,JSONTemp>> name2swagger_jts = new HashMap<>() ;
	
	private int tempNum = 0 ;
	
	public JSONTempDir(String id,File dir) throws Exception
	{
		this.id = id ;
		this.tempDir = dir ;
		
		//loadJSONTempObs();
		//loadJSONTemps();
		
		if (tempDir.exists())
		{
			File[] fs = tempDir.listFiles(new FileFilter() {
				@Override
				public boolean accept(File f)
				{
					String fn = f.getName();
					return fn.startsWith("jt_") && fn.endsWith(".json");
				}
			});
			
			tempNum = fs.length ;
		}
		
	}
	
	public String getId()
	{
		return this.id ;
	}
	
//	private void loadJSONTempObs() throws IOException
//	{
//		//File dir = new File(AppConfig.getDataDirBase() + "/work/json_temps/");
//		if (!tempDir.exists())
//			return;
//		File[] fs = tempDir.listFiles(new FileFilter() {
//			@Override
//			public boolean accept(File f)
//			{
//				String fn = f.getName();
//				return fn.startsWith("jto_") && fn.endsWith(".json");
//			}
//		});
//
//		for (File deff : fs)
//		{
//			String txt = Convert.readFileTxt(deff, "UTF-8");
//			JSONObject jo = new JSONObject(txt);
//			JSONTempOb dw = JSONTempOb.loadFromJSON(jo) ;
//			if(dw==null)
//				continue ;
//
//			name2jsontemp_ob.put(dw.getName(), dw);
//		}
//	}
	
	public void loadJSONTemps() throws Exception
	{
		//File dir = new File(AppConfig.getDataDirBase() + "/work/json_temps/");
		if (!tempDir.exists())
			return;
		File[] fs = tempDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f)
			{
				String fn = f.getName();
				return fn.startsWith("jt_") && fn.endsWith(".json");
			}
		});

		for (File deff : fs)
		{
			try
			{
//				String txt = Convert.readFileTxt(deff, "UTF-8");
//				JSONObject jo = new JSONObject(txt);
//				JSONTemp dw = JSONTemp.loadFromJSON(jo,this) ;
//				if(dw==null)
//					continue ;
				String fn = deff.getName() ;
				String name = fn.substring(3,fn.length()-5) ;
				if(name2jsontemp.get(name)!=null)
					continue ;
				JSONTemp dw = loadJT(name) ;
				if(dw==null)
					continue ;
	
				if(dw.sortByName)
					dw.sortInner();
				name2jsontemp.put(dw.getName(), dw);
			}
			catch(Exception ee)
			{
				System.out.println("load... jsontemp ["+deff.getName()+"] error  "+ee.getMessage()) ;
			}
		}
		
		fs = tempDir.listFiles(new FileFilter() {
			@Override
			public boolean accept(File f)
			{
				String fn = f.getName();
				return fn.startsWith("swagger2_") && fn.endsWith(".json");
			}
		});

		for (File deff : fs)
		{
			String fn = deff.getName() ;
			String name = fn.substring(9,fn.length()-5) ;
			try
			{
				LinkedHashMap<String,JSONTemp> n2jt = loadSwagger2JTs(name) ;
				if(n2jt==null)
					continue ;
				for(Map.Entry<String, JSONTemp> n2t:n2jt.entrySet())
				{
					JSONTemp tmpjt = n2t.getValue() ;
					tmpjt.name = name+"$"+tmpjt.name ;
					if(tmpjt.sortByName)
						tmpjt.sortInner();
					name2jsontemp.put(tmpjt.getName(), tmpjt) ;
				}
			}
			catch(Exception ee)
			{
				System.out.println("load...swagger2 jsontemp ["+deff.getName()+"] error  "+ee.getMessage()) ;
			}
		}
		
		
	}
	
	private JSONTemp loadJT(String name)
	{
		File jtf = new File(tempDir,"jt_"+name+".json") ;
		try
		{
			String txt = Convert.readFileTxt(jtf, "UTF-8");
			JSONObject jo = new JSONObject(txt);
			JSONTemp jt = JSONTemp.loadFromJSON(jo,this) ;
			jt.name = name ;
			
			File jtsample = new File(tempDir,"jt_"+name+".sample.txt") ;
			if(jtsample.exists())
				jt.sampleTxt = Convert.readFileTxt(jtsample, "UTF-8") ;
			
			return jt ;
		}
		catch(Exception ee)
		{
			System.out.println("load... jsontemp ["+name+"] error  "+ee.getMessage()) ;
			return null ;
		}
	}
	
	
	private LinkedHashMap<String,JSONTemp> loadSwagger2JTs(String name) throws IOException
	{
		File jtf = new File(tempDir,"swagger2_"+name+".json") ;
		if(!jtf.exists())
			return null ;
		String txt = Convert.readFileTxt(jtf, "utf-8") ;
		JSONObject jo = new JSONObject(txt) ;
		if(!"2.0".equals(jo.optString("swagger")))
			throw new IOException("no swagger 2.0 format") ;
		
		LinkedHashMap<String,JSONTemp> rets  =new LinkedHashMap<>() ;
		JSONObject defs = jo.optJSONObject("definitions") ;
		HashMap<String,JSONObject> n2defjo = new HashMap<>() ;
		for(Iterator<String> iter = defs.keys();iter.hasNext();)
		{
			String pn = iter.next() ;
			JSONObject def_jo = defs.getJSONObject(pn) ;
			n2defjo.put(pn, def_jo) ;
		}
		
		while(n2defjo.size()>0)
		{
			String pn = n2defjo.keySet().iterator().next();
			JSONObject def_jo = n2defjo.get(pn) ;
			parseSwagger2Def(pn,def_jo,n2defjo,rets);
		}
		return rets ;
	}
	
	private JSONTemp parseSwagger2Def(String name,JSONObject def_jo,HashMap<String,JSONObject> left_n2defjo,LinkedHashMap<String,JSONTemp> rets)
		throws IOException
	{
		if("UpdateAPIShipment".equals(name))
		{
			System.out.println("sss") ;
		}
		String tp = def_jo.getString("type") ;
		if(!"object".equals(tp))
		{
			throw new IOException("not object type") ;
		}
		String tt = def_jo.optString("title") ;
		JSONObject jo_props = def_jo.optJSONObject("properties") ;
		if(jo_props==null)
			return null;
		JSONArray reqjarr = def_jo.optJSONArray("required") ;
		List<String> reqnames = readJSONArrayList(reqjarr) ;

		JSONTemp jt = new JSONTemp(this,name,tt);
		jt.sortByName = true;
		for(Iterator<String> iter = jo_props.keys() ; iter.hasNext();)
		{
			String pn = iter.next() ;
			JSONObject po = jo_props.getJSONObject(pn) ;
			String type = po.optString("type") ;
			String ref = po.optString("$ref") ;
			boolean b_nullable = !reqnames.contains(pn) ;
			boolean b_array = false;
			String format = po.optString("format") ;
			String pattern = po.optString("pattern") ;
			String desc = po.optString("description") ;
			if(Convert.isNotNullEmpty(ref))
			{//single sub
				if(!ref.startsWith("#/definitions/"))
					throw new IOException("not support $ref ="+ref) ;
				String refn = ref.substring("#/definitions/".length()) ;
				JSONTemp ref_jt = rets.get(refn) ;
				if(ref_jt==null)
				{
					JSONObject tmpjo = left_n2defjo.get(refn) ;
					if(tmpjo==null)
						throw new IOException("$ref ="+ref+" has no definition found") ;
					ref_jt = parseSwagger2Def(refn,tmpjo,left_n2defjo,rets) ;
					if(ref_jt==null)
						throw new IOException("$ref ="+ref+" parse to JSONTemp failed") ;
				}
				JSONTemp.SubItem subitem = new JSONTemp.SubItem(pn,pn,desc,ref_jt,false,b_nullable);
				jt.subObjItems.put(subitem.name,subitem);
				continue ;
			}
			if("array".equals(type))
			{
				JSONObject jo_items = po.getJSONObject("items") ;
				type = jo_items.optString("type") ;
				ref = jo_items.optString("$ref") ;
				if(Convert.isNotNullEmpty(ref))
				{//sub item array
					if(!ref.startsWith("#/definitions/"))
						throw new IOException("not support $ref ="+ref) ;
					String refn = ref.substring("#/definitions/".length()) ;
					JSONTemp ref_jt = rets.get(refn) ;
					if(ref_jt==null)
					{
						JSONObject tmpjo = left_n2defjo.get(refn) ;
						if(tmpjo==null)
							throw new IOException("$ref ="+ref+" has no definition found") ;
						ref_jt = parseSwagger2Def(refn,tmpjo,left_n2defjo,rets) ;
						if(ref_jt==null)
							throw new IOException("$ref ="+ref+" parse to JSONTemp failed") ;
					}
					JSONTemp.SubItem subitem = new JSONTemp.SubItem(pn,pn,desc,ref_jt,true,b_nullable);
					jt.subObjItems.put(subitem.name,subitem);
					continue ;
				}
				
				if(Convert.isNullOrEmpty(type))
					throw new IOException("no type found in array with prop name="+pn) ;
				b_array = true ;
				format = jo_items.optString("format") ;
				pattern = jo_items.optString("pattern") ;
			}
			
			JSONTemp.PropItem pi = new JSONTemp.PropItem() ;
			pi.name = pn ;
			pi.title = pn ;
			pi.valTp = JSONTemp.valueOfStr(type) ;
			pi.desc =desc ;
			pi.example = po.opt("example") ;
			pi.pattern = pattern ;
			pi.format = format ;
			pi.bNullable = b_nullable;
			pi.bArray = b_array ;
			int mlen = po.optInt("minLength", -1);
			if(mlen>=0)
				pi.minLen = mlen ;
			mlen = po.optInt("maxLength", -1);
			if(mlen>0)
				pi.maxLen = mlen ;
			List<String> enums = readJSONArrayList(po.optJSONArray("enum")) ;
			if(enums!=null && enums.size()>0)
			{
				ArrayList<JSONTemp.ValOpt> valopts = new ArrayList<>() ;
				for(String en:enums)
					valopts.add(new JSONTemp.ValOpt(en,en)) ;
				pi.valOpts = valopts ;
			}
			jt.propItems.put(pi.name,pi) ;
		}
		
		left_n2defjo.remove(name) ;
		rets.put(name, jt) ;
		return jt ;
	}
	
	private static List<String> readJSONArrayList(JSONArray jarr)
	{
		ArrayList<String> rets = new ArrayList<>() ;
		if(jarr==null)
			return rets ;
		int len = jarr.length() ;
		for(int i = 0 ; i < len ; i ++)
		{
			rets.add(jarr.getString(i)) ;
		}
		return rets ;
	}
//	public JSONTempOb getJSONTempOb(String name)
//	{
//		return this.name2jsontemp_ob.get(name) ;
//	}
	
	public JSONTemp getJSONTemp(String name)
	{
		JSONTemp jt = this.name2jsontemp.get(name) ;
		if(jt!=null)
			return jt ;
		
		synchronized(this)
		{
			jt = this.name2jsontemp.get(name) ;
			if(jt!=null)
				return jt ;
			
			jt = loadJT(name);
			if(jt==null)
				return null ;
			this.name2jsontemp.put(name,jt) ;
			return jt ;
		}
	}
	
	public List<JSONTemp> listJSONTemps()
	{
		ArrayList<JSONTemp> rets = new ArrayList<>(this.name2jsontemp.size()) ;
		rets.addAll(this.name2jsontemp.values()) ;
		Collections.sort(rets);
		return rets ;
	}
	
	public boolean isEmpty()
	{
		//return this.name2jsontemp.size()<=0;
		return this.tempNum<=0 ;
	}
}
