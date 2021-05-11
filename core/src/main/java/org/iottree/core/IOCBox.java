package org.iottree.core;

import java.util.List;

import org.iottree.core.util.xmldata.DataTranserJSON;
import org.iottree.core.util.xmldata.XmlData;
import org.json.JSONArray;
import org.json.JSONObject;

public interface IOCBox extends IOC
{
	
	
	public JSONObject OC_getPropsJSON();
	
	public void OC_setPropsJSON(JSONObject jo);
	
	/**
	 * true node may has sub unit
	 * @return
	 */
	public boolean OC_supportSub();
	
	public List<IOCBox> OC_getSubs();
	/**
	 * 
	 * @return
	 */
	//public JSONObject OCUnit_getDynJSON() ;
	
	public static void injectToXmlData(XmlData xd,IOCBox u)
	{
		JSONObject jobj = u.OC_getPropsJSON();
		if(jobj==null)
			return;
		XmlData tmpxd = new XmlData() ;
		tmpxd.fromPropJSONObject(jobj);
		xd.setSubDataSingle("_oc_box", tmpxd);
	}
	
	public static void extractFromXmlData(XmlData xd,IOCBox u)
	{
		XmlData tmpxd = xd.getSubDataSingle("_oc_box");
		if(tmpxd==null)
			tmpxd = xd.getSubDataSingle("_oc_unit");
		if(tmpxd==null)
			return;
		JSONObject jobj = tmpxd.toPropJSONObject();
		u.OC_setPropsJSON(jobj);
	}
	
	
	
	public static JSONObject transOCMemberToJSON(IOCMember u)
	{
		JSONObject jobj = u.OC_getPropsJSON();
		if(jobj==null)
			jobj = new JSONObject() ;
		
		//jobj.put("_cn", u.OCUnit_getUnitClass());
		//if(u instanceof IOCUnit)
		jobj.put("_oc_tp", "member");
		jobj.put("member_tp", u.getMemberTp()) ;
		jobj.put("id", u.getId());
		String n = u.getName();
		if(n==null)
			n = "" ;
		jobj.put("name", n);
		String t = u.getTitle();
		if(t==null)
			t = "" ;
		jobj.put("title", t);
		
		//
		if(u instanceof IOCList)
		{
			IOCList ulist = (IOCList)u;
			jobj.put("oc_list", true) ;
			JSONArray head = ulist.OCList_getListHead();
			jobj.put("head",head);
			List<Object> items = ulist.OCList_getItems() ;
			JSONArray arr = new JSONArray();
			jobj.put("items", arr) ;
			if(items!=null)
			{
				for(Object ob:items)
				{
					try
					{
						JSONObject tmpjo = DataTranserJSON.extractJSONFromObj(ob);
						 if(tmpjo==null)
							 continue;
						 arr.put(tmpjo);
					}
					catch(Exception e)
					{
						e.printStackTrace();
					}
				}
			}
		}
		
		if(!u.OC_supportSub())
			return jobj;
		List<IOCBox> subus = u.OC_getSubs() ;
		JSONArray subjs = new JSONArray() ;
		jobj.put("_oc_sub", subjs);
		if(subus==null||subus.size()<=0)
			return jobj ;
		
		for(IOCBox subu:subus)
		{
			if(subu instanceof IOCList)
			{
				JSONObject subj = transOCListToJSON((IOCList)subu) ;
				subjs.put(subj) ;
			}
		}
		
		return jobj;
	}
	
	
	public static JSONObject transOCUnitToJSON(IOCUnit u)
	{
		JSONObject jobj = u.OC_getPropsJSON();
		if(jobj==null)
			jobj = new JSONObject() ;
		
		//jobj.put("_cn", u.OCUnit_getUnitClass());
		//if(u instanceof IOCUnit)
		jobj.put("_oc_tp", "unit");
		jobj.put("unitName", u.OCUnit_getUnitTemp());
		jobj.put("id", u.getId());
		String n = u.getName();
		if(n==null)
			n = "" ;
		jobj.put("name", n);
		String t = u.getTitle();
		if(t==null)
			t = "" ;
		jobj.put("title", t);
		
		if(!u.OC_supportSub())
			return jobj;
		List<IOCBox> subus = u.OC_getSubs() ;
		JSONArray subjs = new JSONArray() ;
		jobj.put("_oc_sub", subjs);
		if(subus==null||subus.size()<=0)
			return jobj ;
		
		for(IOCBox subu:subus)
		{
			if(subu instanceof IOCUnit)
			{
				JSONObject subj = transOCUnitToJSON((IOCUnit)subu) ;
				subjs.put(subj) ;
			}
			else if(subu instanceof IOCMember)
			{
				JSONObject subj = transOCMemberToJSON((IOCMember)subu) ;
				subjs.put(subj) ;
			}
			else if(subu instanceof IOCList)
			{
				JSONObject subj = transOCListToJSON((IOCList)subu) ;
				subjs.put(subj) ;
			}
		}
		
		return jobj;
	}
	
	public static JSONObject dynOCUnitToJSON(IOCUnit u,long lastdt)
	{
		JSONObject jobj = new JSONObject() ;
		
		jobj.put("_oc_tp", "unit");
		jobj.put("id", u.getId());
		
		if(u instanceof IOCDyn)
		{
			JSONObject dyn = ((IOCDyn)u).OC_getDynJSON(lastdt);
			if(dyn!=null)
				jobj.put("_oc_dyn", dyn);
		}
		if(!u.OC_supportSub())
			return jobj;
		List<IOCBox> subus = u.OC_getSubs() ;
		JSONArray subjs = new JSONArray() ;
		jobj.put("_oc_sub", subjs);
		if(subus==null||subus.size()<=0)
			return jobj ;
		
		for(IOCBox subu:subus)
		{
			if(subu instanceof IOCUnit)
			{
				JSONObject subj = dynOCUnitToJSON((IOCUnit)subu,lastdt) ;
				subjs.put(subj) ;
			}
			else if(subu instanceof IOCList)
			{
				JSONObject subj = dynOCListToJSON((IOCList)subu,lastdt) ;
				subjs.put(subj) ;
			}
		}
		
		return jobj;
	}
	
	static JSONObject transOCListToJSON(IOCList u)
	{
		JSONObject jobj = u.OC_getPropsJSON();
		if(jobj==null)
			jobj = new JSONObject() ;

		jobj.put("_oc_tp", "list");
		jobj.put("id", u.getId());
		String n = u.getName();
		if(n==null)
			n = "" ;
		jobj.put("name", n);
		String t = u.getTitle();
		if(t==null)
			t = "" ;
		jobj.put("title", t);
		JSONArray head = u.OCList_getListHead();
		jobj.put("head",head);
		List<Object> items = u.OCList_getItems() ;
		JSONArray arr = new JSONArray();
		jobj.put("items", arr) ;
		if(items!=null)
		{
			for(Object ob:items)
			{
				try
				{
					JSONObject tmpjo = DataTranserJSON.extractJSONFromObj(ob);
					 if(tmpjo==null)
						 continue;
					 arr.put(tmpjo);
				}
				catch(Exception e)
				{
					e.printStackTrace();
				}
			}
		}
		return jobj;
	}
	
	
	static JSONObject dynOCListToJSON(IOCList u,long lastdt)
	{
		JSONObject jobj = new JSONObject() ;

		jobj.put("_oc_tp", "list");
		jobj.put("id", u.getId());
		if(u instanceof IOCDyn)
		{
			JSONObject dyn = ((IOCDyn)u).OC_getDynJSON(lastdt);
			if(dyn!=null)
				jobj.put("_oc_dyn", dyn);
		}
		
		List<Object> items = u.OCList_getItems() ;
		JSONArray arr = new JSONArray() ;
		jobj.put("items", arr) ;
		if(items!=null)
		{
			for(Object ob:items)
			{
				if(!(ob instanceof IOCDyn))
					continue ;
				
				IOCDyn ocdyn = (IOCDyn)ob;
				JSONObject dynob = ocdyn.OC_getDynJSON(lastdt);
				if(dynob==null)
					continue ;
				 JSONObject tmpjo = new JSONObject() ;
				 tmpjo.put("id", ocdyn.getId());
				 tmpjo.put("_oc_dyn", dynob) ;
				 
				 arr.put(tmpjo);
			}
		}
		return jobj;
	}
	
	
	public static void transJSONToOCUnit(IOCBox u,JSONObject jobj)
	{
		//String uid = u.OCUnit_getId();
		
		//jobj.put("_cn", u.OCUnit_getUnitClass());
		u.OC_setPropsJSON(jobj);
		String n= jobj.optString("name", "");
		String t = jobj.optString("title", "");
		//u.OC_setBaseVal(n, t);
		
		if(!u.OC_supportSub())
			return;
		
		List<IOCBox> subus = u.OC_getSubs() ;
		if(subus==null||subus.size()<=0)
			return ;
		JSONArray subjs = jobj.optJSONArray("_oc_sub");
		if(subjs!=null)
		{
			for(IOCBox subu:subus)
			{
				String tmpid = subu.getId();
				JSONObject subj = findSubJOBJ(subjs,tmpid) ;
				if(subj==null)
					continue ;
				transJSONToOCUnit(subu,subj) ;
			}
		}
		
		subjs = jobj.optJSONArray("_oc_members");
		if(subjs!=null)
		{
			for(IOCBox subu:subus)
			{
				String tmpid = subu.getId();
				JSONObject subj = findSubJOBJ(subjs,tmpid) ;
				if(subj==null)
					continue ;
				transJSONToOCUnit(subu,subj) ;
			}
		}
	}
	
	
	static JSONObject findSubJOBJ(JSONArray subjs,String id)
	{
		if(id==null)
			return null ;
		int len = subjs.length();
		for(int i = 0 ; i < len ; i ++)
		{
			JSONObject tmpo = subjs.getJSONObject(i);
			String tmpid = tmpo.optString("id") ;
			if(tmpid==null)
				continue ;
			if(id.equals(tmpid))
				return tmpo ;
		}
		return null ;
	}
}
