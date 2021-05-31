package org.iottree.core.basic;

import org.iottree.core.util.Convert;
import org.iottree.core.util.xmldata.*;
import org.json.JSONObject;

import java.util.*;

/**
 * extends hashmap to support js map
 * @author jason.zhu
 */
public abstract class PropNode extends JSObMap
{
	private HashMap<String,Object> propn2Val = new HashMap<>() ;
	/**
	 * list node all supported PropGroup
	 * @return
	 */
	public abstract List<PropGroup> listPropGroups();
	
	
	public Object getPropValue(String groupn,String itemn)
	{
		String k = groupn+"."+itemn ;
		return propn2Val.get(k);
	}
	
	public String getPropValueStr(String groupn,String itemn,String defv)
	{
		Object v = getPropValue(groupn,itemn);
		if(v==null)
			return defv ;
		if(v instanceof String)
			return (String)v ;
		return v.toString();
	}
	
	public boolean getPropValueBool(String groupn,String itemn,boolean defv)
	{
		Object v = getPropValue(groupn,itemn);
		if(v==null)
			return defv ;
		if(v instanceof Boolean)
			return (Boolean)v ;
		return defv ;
	}
	
	public long getPropValueLong(String groupn,String itemn,long defv)
	{
		Object v = getPropValue(groupn,itemn);
		if(v==null)
			return defv ;
		if(v instanceof Number)
			return ((Number)v).longValue() ;
		return defv ;
	}
	
	public int getPropValueInt(String groupn,String itemn,int defv)
	{
		Object v = getPropValue(groupn,itemn);
		if(v==null)
			return defv ;
		if(v instanceof Number)
			return ((Number)v).intValue() ;
		return defv ;
	}
	
	public double getPropValueDouble(String groupn,String itemn,double defv)
	{
		Object v = getPropValue(groupn,itemn);
		if(v==null)
			return defv ;
		if(v instanceof Number)
			return ((Number)v).doubleValue() ;
		return defv ;
	}
	
	public PropGroup getPropGroup(String pgname)
	{
		List<PropGroup> pgs = listPropGroups();
		if(pgs==null)
			return null ;
		for(PropGroup pg:pgs)
		{
			if(pg.getName().contentEquals(pgname))
				return pg ;
		}
		return null ;
	}
	
	public PropItem getPropItem(String pgname,String piname)
	{
		PropGroup pg = getPropGroup(pgname);
		if(pg==null)
			return null ;
		return pg.getPropItem(piname) ;
	}
	
	public HashMap<String,Object> getPropValMap(List<PropGroup> pgs)
	{
		HashMap<String,Object> prop2val = new HashMap<>() ;
		if(pgs==null)
			return prop2val ;
	
		for(PropGroup pg:pgs)
		{
			List<PropItem> pis = pg.getPropItems() ;
			if(pis==null)
				continue ;
			String pgn = pg.getName() ;
			for(PropItem pi:pis)
			{
				String pin = pi.getName() ;
				Object v = this.getPropValue(pg.getName(), pi.getName()) ;
				if(v==null)
					continue ;
				prop2val.put(pgn+"."+pin, v) ;
			}
		}
		return prop2val ;
	}
	
	public boolean setPropValue(String pgn,String pin,String strval)
	{
		String k = pgn+"."+pin ;
		if(strval==null)
		{
			propn2Val.remove(k);
			return true;
		}
		PropItem pi = getPropItem(pgn,pin);
		if(pi==null)
			return false;
		Object v = PropItem.transStrToVal(pi.getVT(), strval) ;
		propn2Val.put(k, v);
		return true;
	}
	
	protected XmlData toPropNodeValXmlData()
	{
		XmlData xd = new XmlData() ;
		for(Map.Entry<String, Object> n2v:propn2Val.entrySet())
		{
			xd.setParamValue(n2v.getKey(), n2v.getValue());
		}
		return xd;
	}
	
	protected void fromPropNodeValXmlData(XmlData xd)
	{
		for(String n:xd.getParamNameSet())
		{
			Object v = xd.getParamValue(n);
			propn2Val.put(n, v);
		}
		onPropNodeValueChged();
	}
	
	public JSONObject toPropNodeValJSON()
	{
		JSONObject r = new JSONObject();
		List<PropGroup> pgs = listPropGroups();
		if(pgs==null||pgs.size()<=0)
			return r ;
		for(PropGroup pg:pgs)
		{
			String pgn = pg.getName() ;
			for(PropItem pi:pg.getPropItems())
			{
				String pin = pi.getName();
				Object pv = getPropValue(pgn,pin);
				if(pv==null)
					continue ;
				String k = pgn+"."+pin;
				r.put(k,pv);
			}
		}
		return r ;
	}
	
	public void fromPropNodeValJSON(JSONObject jobj)
	{
		boolean bv = false;
		for(String k:jobj.keySet())
		{
			List<String> ss = Convert.splitStrWith(k, ".");
			String strv = jobj.optString(k);
			if(strv==null)
				continue ;
			if(setPropValue(ss.get(0),ss.get(1), strv))
				bv = true ;
		}
		if(bv)
			onPropNodeValueChged();
	}
	
	protected abstract void onPropNodeValueChged() ;
}
