package org.iottree.core.router;

import org.iottree.core.UAPrj;
import org.iottree.core.util.IdCreator;
import org.json.JSONObject;

public abstract class RouterOuter
{
	RouterManager belongTo ;
	
	UAPrj belongPrj ;
	
	String id ;
	
	String name = null ;
	
	String title = null ;
	
	String desc = null ;
	
	public RouterOuter(RouterManager rm)
	{
		this.belongTo = rm ;
		this.belongPrj = rm.belongTo ;
		this.id = IdCreator.newSeqId() ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public String getName()
	{
		return name;
	}

	public String getTitle()
	{
		return title;
	}

	public String getDesc()
	{
		return desc;
	}
	
	public abstract String getTp() ;
	
	public abstract RouterOuter newInstance(RouterManager rm) ;
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("id", id) ;
		jo.put("n", this.getName()) ;
		jo.put("tp", this.getTp()) ;
		return jo ;
	}
	
	protected boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		this.id = jo.getString("id") ;
		return true ;
	}
	
	public static RouterOuter transFromJO(RouterManager rm,JSONObject jo,StringBuilder failedr)
	{
		String tp = jo.getString("tp") ;
		RouterOuter ro = RouterManager.TP2OUTER.get(tp) ;
		if(ro==null)
		{
			failedr.append("no RouterOuter found with tp="+tp) ;
			return null;
		}

		RouterOuter ret = ro.newInstance(rm) ;
		if(ret.fromJO(jo,failedr))
			return null ;
		return ret ;
	}
}
