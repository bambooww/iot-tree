package org.iottree.core.basic.ce;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class ExchgModule
{
	String name ;
	
	String title ;
	
	List<ExchgObj> exchgObjs = null ;
	
	public ExchgModule()
	{}
	
	public ExchgModule(String n,String t,List<ExchgObj> exchg_objs)
	{
		this.name = n ;
		this.title = t ;
		this.exchgObjs = exchg_objs ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		return this.title ;
	}
	
	public List<ExchgObj> listExchgObjs()
	{
		return this.exchgObjs ;
	}
	
	public JSONObject toExchgJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("n", this.name) ;
		jo.put("t", this.title) ;
		JSONArray jarr = new JSONArray() ;
		jo.put("objs", jarr) ;
		if(exchgObjs!=null)
		{
			for(ExchgObj eo:this.exchgObjs)
				jarr.put(eo.toExchgJO()) ;
		}
		return jo ;
	}
	
	public boolean formExchgJO(ExchgModuleAdp adp,ExchgModule m,JSONObject jo)
	{
		this.name = jo.optString("n") ;
		if(Convert.isNullOrEmpty(this.name))
			return false;
		this.title = jo.optString("t") ;
		JSONArray jarr = jo.optJSONArray("objs") ;
		if(jarr!=null)
		{
			ArrayList<ExchgObj> objs = new ArrayList<>() ;
			int nn = jarr.length() ; 
			for(int i = 0 ; i < nn ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				String tp = tmpjo.optString("tp") ;
				if(Convert.isNullOrEmpty(tp))
					continue ;
				ExchgObj eo = adp.createExchgObjByTP(tp) ;
				if(eo==null)
					continue ;
				if(!eo.fromExchgJO(m, tmpjo))
				{
					continue ;
				}
				objs.add(eo) ;
			}
			this.exchgObjs = objs ;
		}
		return true ;
	}
}
