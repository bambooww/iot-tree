package org.iottree.core.ecp;

import java.util.ArrayList;

import org.iottree.core.util.Convert;
import org.json.JSONArray;
import org.json.JSONObject;

public class ECPComp extends ECPNode
{
	public static class Head implements Comparable<Head>
	{
		public ECPCompLib lib ;
		
		public String comp_id ;
		
		public String title ;
		
		public long update_dt ;
		
		Head(ECPCompLib lib,String id,String t,long dt)
		{
			this.lib = lib;
			this.comp_id = id ;
			this.title = t ;
			this.update_dt = dt ;
		}
		
		public String getUID()
		{
			return lib.getContainerUID()+"-"+this.comp_id ;
		}
		
		public JSONObject toJO()
		{
			return new JSONObject().putOpt("uid", getUID()).putOpt("id", comp_id)
					.putOpt("t", this.title).putOpt("dt", this.update_dt) ;
		}
		
		public static Head fromJO(ECPCompLib lib,JSONObject jo)
		{
			String id = jo.optString("id") ;
			if(Convert.isNullOrEmpty(id))
				return null ;
			//String uid = jo.optString("uid") ;
			String t = jo.optString("t",id) ;
			long dt = jo.optLong("dt") ;
			return new Head(lib,id,t,dt) ;
		}

		@Override
		public int compareTo(Head o)
		{
			long dv = o.update_dt-this.update_dt;
			if(dv>0) return 1 ;
			else if(dv<0) return -1 ;
			return 0 ;
		}
	}
	
	public static class LibHeads
	{
		public String title ;
		public ArrayList<Head> heads = new ArrayList<>() ;
		
		public JSONObject toJO()
		{
			JSONObject ret = new JSONObject().putOpt("t",this.title) ;
			JSONArray jarr = new JSONArray() ;
			ret.put("heads", jarr) ;
			for(Head h:heads)
				jarr.put(h.toJO()) ;
			return ret ;
		}
		
		public void fromJO(JSONObject jo)
		{
			this.title = jo.optString("t") ;
			JSONArray jarr = jo.optJSONArray("heads") ;
			if(jarr!=null)
			{
				
			}
		}
	}
	
	public static final String TP = "comp";
	
	ECPCompLib owner ;
	
	int id_cc = 0 ;
	
	public ECPComp(ECPCompLib owner,String id)
	{
		super(null, id, null);
		
		//this.setTitle(title) ;
		this.owner = owner ;
		
	}
	
	ECPComp(ECPCompLib owner,String id,ECPNode node)
	{
		super(null, id,node);
		
		//this.setTitle(title) ;
		this.owner = owner ;
		
	}
	
	public Head getHead()
	{
		return new Head(owner,this.getId(),this.getTitle(),-1) ;
	}
	
	public ECPCompLib getOwnerLib()
	{
		return this.owner ;
	}
	
	public String getUID()
	{
		return this.owner.getContainerUID()+"-"+this.getId() ;
	}

	@Override
	public boolean removeMe()
	{
		return false;
	}

	@Override
	public String getNodeTP()
	{
		return TP;
	}

	public JSONObject toJO()
	{
		JSONObject ret = super.toJO() ;
		ret.put("id_cc", this.id_cc) ;
		return ret ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		if(!super.fromJO(jo))
			return false;
		this.id_cc = jo.optInt("id_cc",0) ;
		return true ;
	}
	
	public void updateStructByJO(JSONObject jo)
	{
		super.updateStructByJO(jo);
		this.id_cc = jo.optInt("id_cc",0) ;
	}
	
	public void save() throws Exception
	{
		this.owner.saveComp(this);
	}
}
