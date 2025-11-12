package org.iottree.core.basic;

import java.util.ArrayList;
import java.util.HashMap;

import org.iottree.core.util.Convert;
import org.iottree.core.util.logger.ILogger;
import org.iottree.core.util.logger.LoggerManager;
import org.json.JSONArray;
import org.json.JSONObject;

public class ValOptEnum extends ValOption
{
	private static ILogger log = LoggerManager.getLogger(ValOptEnum.class) ;
	
	public static final String TP = "enum" ;
	
	public static class Item<T>
	{
		public T val ;
		
		public String valTitle ;
		
		public Item(T val,String v_tt)
		{
			if(val==null)
				throw new IllegalArgumentException("val cannot be null") ;
			this.val = val ;
			this.valTitle = v_tt ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject()
					.put("v_str", this.val.toString()).putOpt("v_tt",this.valTitle) ;
		}
		
		public static Item<String> fromJO(JSONObject jo)
		{
			String vstr = jo.optString("v_str") ;
			if(Convert.isNullOrEmpty(vstr))
				return null ;
			return new Item<String>(vstr,jo.optString("v_tt")) ;
		}
	}
	
	private ArrayList<Item<String>> items =  new ArrayList<>() ;
	
	transient private HashMap<Long,Item<Long>> int2Item = null ;
	transient private HashMap<String,Item<String>> str2Item = null ;
	
	@Override
	public String getTP()
	{
		return TP;
	}
	
	public ArrayList<Item<String>> listEnumItems()
	{
		return this.items;
	}
	
	public HashMap<Long,Item<Long>> getInt2Item()
	{
		if(this.int2Item!=null)
			return this.int2Item ;
		
		HashMap<Long,Item<Long>> i2i = new HashMap<>() ;
		for(Item<String> item:items)
		{
			try
			{
				long val = Long.parseLong(item.val) ;
				i2i.put(val,new Item<Long>(val,item.valTitle)) ;
			}
			catch(Exception ee)
			{
				if(log.isWarnEnabled())
					log.warn(ee);
			}
		}
		return this.int2Item = i2i ;
	}
	
	public Item<Long> getItemByIntVal(long val)
	{
		return this.getInt2Item().get(val) ;
	}
	
	public HashMap<String,Item<String>> getStr2Item()
	{
		if(this.str2Item!=null)
			return this.str2Item ;
		
		HashMap<String,Item<String>> s2i = new HashMap<>() ;
		for(Item<String> item:items)
		{
			s2i.put(item.val,item) ;
		}
		return this.str2Item = s2i ;
	}
	
	public Item<String> getItemByStrVal(String val)
	{
		return this.getStr2Item().get(val) ;
	}
	

	@Override
	public String getOptTitle()
	{
		StringBuilder sb = new StringBuilder() ;
		for(Item<String> item:this.items)
		{
			sb.append(item.val).append("=");
			if(item.valTitle!=null)
				sb.append(item.valTitle) ;
			sb.append(" ") ;
		}
		return sb.toString();
	}
	
	@Override
	public String RT_getValOptTitle(Object val)
	{
		if(val==null)
			return null ;
		String strv = val.toString() ;
		Item<String> item = this.getItemByStrVal(strv) ;
		if(item==null)
			return null ;
		return item.valTitle ;
	}

	@Override
	public JSONObject toJO()
	{
		JSONObject jo = super.toJO();
		JSONArray jarr = new JSONArray() ;
		jo.put("items",jarr) ;
		for(Item<String> item:items)
		{
			jarr.put(item.toJO());
		}
		return jo ;
	}
	
	@Override
	protected void fromJO(JSONObject jo)
	{
		JSONArray jarr = jo.optJSONArray("items") ;
		if(jarr!=null)
		{
			for(int i= 0 ; i < jarr.length() ; i ++)
			{
				JSONObject tmpjo = jarr.getJSONObject(i) ;
				Item<String> item = Item.fromJO(tmpjo);
				if(item==null)
					continue ;
				this.items.add(item) ;
			}
		}
	}

}
