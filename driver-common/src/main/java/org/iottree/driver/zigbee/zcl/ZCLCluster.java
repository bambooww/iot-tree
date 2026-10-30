package org.iottree.driver.zigbee.zcl;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.util.Convert;
import org.iottree.core.util.IJoIn;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONObject;

public class ZCLCluster implements Comparable<ZCLCluster>
{
	int id ;
	
	String name ;
	
	LinkedHashMap<String,ZCLAttr> name2attrs =new LinkedHashMap<>(); 
	
	LinkedHashMap<Integer,ZCLAttr> id2attrs = new LinkedHashMap<>(); 
	
	LinkedHashMap<String,ZCLCmd> name2cmds = new LinkedHashMap<>(); 
	LinkedHashMap<Integer,ZCLCmd> id2cmds = new LinkedHashMap<>(); 
	
	LinkedHashMap<String,ZCLCmd> cmds_rsp = null;
	
	public ZCLCluster(int id,String n)
	{
		this.id = id ;
		this.name =n ;
	}
	
	public int getID()
	{
		return this.id ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public LinkedHashMap<String,ZCLAttr> getNameAttributes()
	{
		return this.name2attrs ;
	}
	
	public LinkedHashMap<Integer,ZCLAttr> getIdAttributes()
	{
		return this.id2attrs ;
	}
	
	private ArrayList<ZCLAttr> tagDefAttrs = null ; 
	private ArrayList<Integer> tagDefAttrIds = null ; 
	
	public List<ZCLAttr> listTagDefAttributes()
	{
		if(tagDefAttrs!=null)
			return tagDefAttrs ;
		
		ArrayList<ZCLAttr> rets = new ArrayList<>() ;
		for(ZCLAttr att:this.id2attrs.values())
		{
			if(att.getTagDef()!=null)
				rets.add(att) ;
		}
		return tagDefAttrs = rets ;
	}
	
	public List<Integer> listTagDefAttrIds()
	{
		if(tagDefAttrIds!=null)
			return tagDefAttrIds ;
		List<ZCLAttr> attrs = listTagDefAttributes() ;
		ArrayList<Integer> rets = new ArrayList<>(attrs.size()) ;
		for(ZCLAttr attr:attrs)
			rets.add(attr.getID()) ;
		return tagDefAttrIds = rets ;
	}
	
	public ZCLAttr getAttributeById(int id)
	{
		if(this.id2attrs==null)
			return null ;
		return this.id2attrs.get(id) ;
	}
	
	public ZCLAttr getAttributeByName(String n)
	{
		if(this.name2attrs==null)
			return null ;
		return this.name2attrs.get(n) ;
	}
	
	public LinkedHashMap<String,ZCLCmd> getNameCmds()
	{
		return this.name2cmds ;
	}
	
	public LinkedHashMap<Integer,ZCLCmd> getIdCmds()
	{
		return this.id2cmds ;
	}
	
	public ZCLCmd getCmdById(int id)
	{
		if(this.id2cmds==null)
			return null ;
		return this.id2cmds.get(id) ;
	}
	
	public ZCLCmd getCmdByName(String n)
	{
		if(this.name2cmds==null)
			return null ;
		return this.name2cmds.get(n) ;
	}
	
	public LinkedHashMap<String,ZCLCmd> getCmdRsps()
	{
		return this.cmds_rsp ;
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = new JSONObject() ;
		ret.put("id", this.id).put("name", name) ;
		if(name2attrs!=null)
		{
			JSONObject jo = new JSONObject() ;
			ret.put("attributes", jo) ;
			for(ZCLAttr a:this.name2attrs.values())
			{
				jo.put(a.name,a.toJO()) ;
			}
		}
		
		if(name2cmds!=null)
		{
			JSONObject jo = new JSONObject() ;
			ret.put("commands", jo) ;
			for(ZCLCmd a:this.name2cmds.values())
			{
				jo.put(a.name,a.toJO()) ;
			}
		}
		
		if(cmds_rsp!=null)
		{
			JSONObject jo = new JSONObject() ;
			ret.put("commandsResponse", jo) ;
			for(ZCLCmd a:this.cmds_rsp.values())
			{
				jo.put(a.name,a.toJO()) ;
			}
		}
		
		
		return ret ;
	}
	

	@Override
	public int compareTo(ZCLCluster o)
	{
		return this.id-o.id;
	}
	
	
	private static ILogger log = LoggerManager.getLogger(ZCLCluster.class) ;
	

	static ZCLCluster parseCluster(JSONObject jo) throws Exception
	{
		int id = jo.optInt("ID",-1) ;
		String n = jo.optString("name") ;
		if(id<0 || Convert.isNullOrEmpty(n))
			return null ;
		ZCLCluster c =new ZCLCluster(id,n) ;
		JSONObject attrs_jo = jo.optJSONObject("attributes") ;
		if(attrs_jo!=null)
		{
//			c.name2attrs = new LinkedHashMap<>(); 
//			c.id2attrs = new LinkedHashMap<>();
			List<ZCLAttr> attrs = parseSubs(ZCLAttr.class,attrs_jo) ;
			for(ZCLAttr a:attrs)
			{
				c.name2attrs.put(a.name,a) ;
				c.id2attrs.put(a.id,a) ;
			}
		}
		
		JSONObject cmds_jo = jo.optJSONObject("commands") ;
		if(cmds_jo!=null)
		{
//			c.name2cmds = new LinkedHashMap<>();
//			c.id2cmds = new LinkedHashMap<>();
			List<ZCLCmd> cmds = parseSubs(ZCLCmd.class,cmds_jo) ;
			for(ZCLCmd a:cmds)
			{
				c.name2cmds.put(a.name,a) ;
				c.id2cmds.put(a.getID(),a) ;
			}
		}
		
		JSONObject cmds_rsp_jo = jo.optJSONObject("commandsResponse") ;
		if(cmds_rsp_jo!=null)
		{
			c.cmds_rsp = new LinkedHashMap<>(); 
			List<ZCLCmd> cmds = parseSubs(ZCLCmd.class,cmds_rsp_jo) ;
			for(ZCLCmd a:cmds)
			{
				c.cmds_rsp.put(a.name,a) ;
			}
		}
		
		return c ;
	}
	

	private static <T extends IJoIn> ArrayList<T> parseSubs(Class<T> c,JSONObject jo) throws Exception
	{
		ArrayList<T> rets = new ArrayList<>() ;
		Iterator<String> ikey = jo.keys() ;
		while(ikey.hasNext())
		{
			String k = ikey.next() ;
			JSONObject joc = jo.getJSONObject(k) ;
			T ins = c.getConstructor().newInstance() ;
			StringBuilder failedr = new StringBuilder() ;
			if(!ins.fromJO(joc, failedr))
			{
				if(log.isWarnEnabled())
					log.warn("parse "+c.getSimpleName()+" err:"+joc.toString());
				continue ;
			}
			rets.add(ins) ;
		}
		return rets ;
	}

}
