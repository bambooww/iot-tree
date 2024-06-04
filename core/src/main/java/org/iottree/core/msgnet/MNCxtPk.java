package org.iottree.core.msgnet;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iottree.core.UAPrj;
import org.iottree.core.msgnet.MNCxtVar.KeepTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.JsonUtil;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class MNCxtPk implements IMNCxtPk
{
	static ILogger log = LoggerManager.getLogger(MNCxtPk.class) ;
	
	private LinkedHashMap<String,MNCxtVar> name2var = new LinkedHashMap<>() ;
	
	private HashMap<String,Object> var2val = new HashMap<>() ;
	
	public LinkedHashMap<String,MNCxtVar> CXT_getVarsAll()
	{
		return name2var ;
	}
	
	public HashSet<String> CXT_getVarNameSet()
	{//copy new set
		HashSet<String> rets = new HashSet<>() ;
		rets.addAll(name2var.keySet()) ;
		return rets ;
	}
	
	public List<String> CXT_getVarNamesAll()
	{//copy new set
		ArrayList<String> rets = new ArrayList<>() ;
		rets.addAll(name2var.keySet()) ;
		for(String varn:var2val.keySet())
		{
			if(name2var.containsKey(varn))
				continue ;
			rets.add(varn) ;
		}
		return rets ;
	}
	
	public MNCxtVar CXT_getVar(String name)
	{
		return name2var.get(name) ;
	}
	
	public void CXT_setVar(String name,MNCxtVar cv)
	{
		if(cv==null)
		{
			this.name2var.remove(name) ;
			return ;
		}
		
		this.name2var.put(name, cv) ;
	}
	
	public JSONObject CXT_getDefJO()
	{
		JSONObject jo = new JSONObject() ;
		
		JSONArray jarr = new JSONArray() ;
		for(MNCxtVar cv:name2var.values())
		{
			jarr.put(cv.toJO()) ;
		}
		jo.put("cxt_vars", jarr) ;
		return jo ;
	}
	
	public boolean CXT_setDefJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("cxt_vars") ;
		if(jarr!=null)
		{
			int n = jarr.length() ;
			for(int i = 0 ; i < n ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				StringBuilder sb = new StringBuilder() ;
				MNCxtVar v = MNCxtVar.fromJO(tmpjo, sb) ;
				if(v==null)
				{
					log.warn("load MNCxtVar failed:"+tmpjo.toString());
					continue ;
				}
				name2var.put(v.getName(), v) ;
			}
		}
		return true ;
	}
	
	
	
	//protected abstract RTCxtBase RT_getCxtBase() ;
	
	protected void RT_CXT_injectSavedVals(JSONObject savedvals)
	{
		HashSet<String> def_varnames = this.CXT_getVarNameSet() ;
		if(savedvals!=null)
		{
			for(String n:savedvals.keySet())
			{
				Object ob = savedvals.get(n) ;
				if(def_varnames.remove(n))
					var2val.put(n, ob) ;
			}
		}
		
		//add left defined vars
		for(String n:def_varnames)
		{
			MNCxtVar var = this.CXT_getVar(n) ;
			if(var==null)
				continue ;
			Object defv = var.getDefaultVal() ;
			if(defv==null)
				continue ;
			this.var2val.put(n, defv) ;
		}
	}
	
	
	protected JSONObject RT_CXT_extractValsForSave()
	{
		JSONObject jo = new JSONObject() ;
		for(MNCxtVar var:this.CXT_getVarsAll().values())
		{
			if(var.getKeepTP()!=KeepTP.save)
				continue ;
			Object v = var2val.get(var.getName()) ;
			if(v==null)
				continue ;
			jo.put(var.getName(), v) ;
		}
		return jo ;
	}

	private boolean RT_bDirty = false;
	
	void RT_CXT_clearDirty()
	{
		this.RT_bDirty =false;
	}
	
	public boolean RT_CXT_isDirty()
	{
		return this.RT_bDirty ;
	}
	
	protected final void RT_CXT_fireDirty(boolean b)
	{
		RT_bDirty = b;
	}
	
	public void RT_CXT_setVarVal(String var_n,Object val)
	{
		if(val==null)
		{
			if(var2val.remove(var_n)!=null)
				RT_CXT_fireDirty(true) ;
			return ;
		}
		
		Object oldv = var2val.get(var_n) ;
		if(val.equals(oldv))
			return ;
		var2val.put(var_n,val) ;
		RT_CXT_fireDirty(true) ;
	}
	
	public Object RT_CXT_getVarVal(String var_n)
	{
		return var2val.get(var_n) ;
	}
	
	public void RT_CXT_clean()
	{
		this.var2val.clear();
	}

	protected void CXT_renderVarsDiv(StringBuilder divsb)
	{
		divsb.append("<div tp='vars' class=\"rt_blk\"><div style='background-color:#aaaaaa'>Vars<button onclick=\"cxt_add_var()\">add</button></div><table>");
		divsb.append("<tr>");
		divsb.append("<td>Name</td>");
		divsb.append("<td>Type</td>");
		divsb.append("<td>Keep In</td>");
		divsb.append("<td>Default</td>");
		divsb.append("<td>Current</td>");
		divsb.append("<td></td>");
		divsb.append("</tr>");
		for(MNCxtVar vv:this.name2var.values())
		{
			divsb.append("<tr>");
			divsb.append("<td>").append(vv.getName()).append("</td>");
			divsb.append("<td>").append(vv.getValTPT()).append("</td>");
			divsb.append("<td>").append(vv.getKeepTPT()).append("</td>");
			divsb.append("<td>").append(vv.getDefaultValStr()).append("</td>");
			String curv = "" ;
			Object ov = RT_CXT_getVarVal(vv.getName());
			if(ov!=null)
				curv = ov.toString() ;
			divsb.append("<td>").append(curv).append("</td>");
			divsb.append("<td><button onclick=\"\">set</button></td>");
			divsb.append("</tr>");
		}
		for(Map.Entry<String, Object> v2v:var2val.entrySet())
		{
			String varn = v2v.getKey() ;
			if(name2var.containsKey(varn))
				continue ;
			divsb.append("<tr>");
			divsb.append("<td>").append(varn).append("</td>");
			divsb.append("<td>").append("").append("</td>");
			divsb.append("<td>").append("").append("</td>");
			divsb.append("<td></td>");
			String curv = "" ;
			Object ov = v2v.getValue() ;
			if(ov!=null)
				curv = ov.toString() ;
			divsb.append("<td>").append(curv).append("</td>");
			divsb.append("<td><button onclick=\"cxt_add_var('"+varn+"')\">add</button></td>");
			divsb.append("</tr>");
		}
		divsb.append("</table></div>") ;
	}
	
	// CXT_PK

	@Override
	public List<String> CXT_PK_getSubNames()
	{
		return CXT_getVarNamesAll();
	}

	@Override
	public List<String> CXT_PK_getSubNamesW()
	{
		return null;// not limit
	}

	@Override
	public List<MNCxtValTP> CXT_PK_getSubLimit(String subname)
	{
		MNCxtVar cxtv = this.name2var.get(subname) ;
		if(cxtv==null)
			return null;
		MNCxtValTP vtp = cxtv.getValTP() ;
		if(vtp==null)
			return null ;
		return Arrays.asList(vtp) ;
	}

	@Override
	public Object CXT_PK_getSubVal(String subname)
	{
		Object ov = this.var2val.get(subname);
		if(ov!=null)
			return ov ;
		
		List<String> ss = Convert.splitStrWith(subname, ".") ;
		if(ss.size()<=1)
			return ov ;
		
		ov = this.var2val.get(ss.get(0)) ;
		if(ov==null)
			return null ;
		
		if(ov instanceof JSONObject)
		{
			ss.remove(0) ;
			return JsonUtil.getValByPath((JSONObject)ov, ss) ;
		}
		
		return null ;
	}

	@Override
	public boolean CXT_PK_setSubVal(String subname, Object subv, StringBuilder failedr)
	{
		if(subv==null)
		{
			RT_CXT_setVarVal(subname,subv) ;
			return true ;
		}
		MNCxtValTP vtp = null ;
		MNCxtVar cxtv = this.name2var.get(subname) ;
		if(cxtv!=null)
			vtp = cxtv.getValTP() ;
		if(vtp!=null)
		{
			subv = vtp.fitObjToVal(subv, failedr);
			if(subv==null)
				return false;
		}
		RT_CXT_setVarVal(subname,subv) ;
		return true;
	}
}
