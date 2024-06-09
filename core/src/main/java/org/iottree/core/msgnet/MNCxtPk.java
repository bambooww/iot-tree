package org.iottree.core.msgnet;

import java.io.IOException;
import java.io.Writer;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.iottree.core.UAPrj;
import org.iottree.core.UAVal;
import org.iottree.core.basic.ValAlert;
import org.iottree.core.cxt.JSObMap;
import org.iottree.core.cxt.JsMethod;
import org.iottree.core.cxt.JsProp;
import org.iottree.core.cxt.JsSub;
import org.iottree.core.cxt.UACodeItem;
import org.iottree.core.msgnet.MNBase.DivBlk;
import org.iottree.core.msgnet.MNCxtVar.KeepTP;
import org.iottree.core.util.Convert;
import org.iottree.core.util.JsonUtil;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class MNCxtPk extends JSObMap implements IMNCxtPk
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
	
	
	public Map<String,Object> CXT_PK_toMap()
	{
		HashMap<String,Object> rets = new HashMap<>() ;
		for(Map.Entry<String, Object> n2o:this.var2val.entrySet())
		{
			String n = n2o.getKey() ;
			Object o = n2o.getValue() ;
			if(o instanceof JSONObject)
				rets.put(n,((JSONObject)o).toMap()) ;
			else if(o instanceof JSONArray)
				rets.put(n,((JSONArray)o).toList()) ;
			else
				rets.put(n,o) ;
		}
		return rets ;
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

	protected void CXT_renderVarsDiv(List<DivBlk> divblks)
	{
		StringBuilder divsb = new StringBuilder() ;
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
		
		divblks.add(new DivBlk("vars",divsb.toString())) ;
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
	
	//  cxt 
	
	public void CXT_PK__renderTree(Writer w) throws IOException
	{
		Map<String,Object> map = CXT_PK_toMap() ;
		if(map==null)
			return ;
		CXT_PK_renderTree(w,map);
	}
	
	public void CXT_PK_renderTree(Writer w,Map<String,Object> map) throws IOException
	{
		w.write("<ul>");
		for(Map.Entry<String, Object> n2o:map.entrySet())
		{
			String n = Convert.plainToHtml(n2o.getKey()) ;
			Object o = n2o.getValue() ;
			w.write("<li >"+n) ;
			CXT_PK_renderTreeObj(w, o) ;
			w.write("</li>") ;
		}
		
		List<JsSub> subs = this.JS_get_subs() ;
		if(subs!=null)
		{
			for(JsSub sub:subs)
			{
				if(sub instanceof JsMethod) // || !sub.hasSub())
				{
					JsMethod jsm = (JsMethod)sub ;
					w.write("<li data-jstree='{\"icon\":\"method\"}'>"+sub.getName()) ;
					w.write(jsm.getParamsTitle()) ;
					w.write("</li>") ;
				}
				
				if(sub instanceof JsProp && ((JsProp)sub).isSysTag())
				{
					JsProp jsp = (JsProp)sub ;
					w.write("<li >"+sub.getName()) ;
					w.write(jsp.getSubTitle()) ;
					w.write("</li>") ;
				}
			}
		}
		
        w.write("</ul>");
	}
	
	
	void CXT_PK_renderTreeObj(Writer w,Object o) throws IOException
	{
		if(o instanceof List)
		{
			w.write(":[]") ;
			List<?> ll = (List<?>)o ;
			if(ll.size()>=0)
			{
				Object ob = ll.get(0) ;
				if(ob instanceof Map)
					CXT_PK_renderTree(w,(Map<String,Object>)ob) ;
				//else if(ob instanceof List)
				//	CXT_PK_renderTreeObj(w,Object o)
			}
		}
		else if(o instanceof Map)
		{
			CXT_PK_renderTree(w,(Map)o) ;
		}
		else
		{
			if(o!=null)
			{
				if(o instanceof Number)
					w.write(":number") ;
				else if(o instanceof String)
					w.write(":string");
				else if(o instanceof Boolean)
					w.write(":boolean");
				else
					w.write(":obj");
			}
		}
	}
	
	
	// js
	
	public Object JS_get(String  key)
	{
		Object obj  = super.JS_get(key) ;
		if(obj!=null)
			return obj ;
		
		if(this.var2val==null)
			return null ;
		return this.var2val.get(key) ;
	}
	
	public List<JsProp> JS_props()
	{
		List<JsProp> rets = super.JS_props() ;
		
		if(this.var2val!=null)
		{
			for(Map.Entry<String, Object> n2o:this.var2val.entrySet())
			{
				String n = n2o.getKey() ;
				Object v = n2o.getValue() ;
				rets.add(new JsProp(n,v,null,false,"",""));
			}
		}
		
		return rets ;
	}
	
//	public Class<?> JS_type(String key)
//	{
//		
//	}
}
