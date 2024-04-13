package org.iottree.core.router;

import java.util.ArrayList;
import java.util.List;

import org.iottree.core.UAPrj;
import org.iottree.core.UATag;
import org.iottree.core.util.IdCreator;
import org.json.JSONArray;
import org.json.JSONObject;

public abstract class DataPacker
{
	RouterManager belongTo ;
	
	UAPrj belongPrj ;
	
	String id ;
	
	
	public DataPacker(RouterManager rm)
	{
		this.belongTo = rm ;
		this.belongPrj = rm.belongTo ;
		this.id = IdCreator.newSeqId() ;
	}
	
	public String getId()
	{
		return id ;
	}
	
	public abstract String getName() ;
	
	public abstract String getTitle() ;
	
	public abstract String getDesc() ;
	
	public abstract String getTp() ;
	
	public abstract String getPackData() throws Exception;
	
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
	
	public static DataPacker transFromJO(RouterManager rm,JSONObject jo,StringBuilder failedr)
	{
		String tp = jo.getString("tp") ;
		
		DataPacker dp = null ;
		switch(tp)
		{
		case DataPackerSelTags.TP:
			dp = new DataPackerSelTags(rm) ;
		default:
			break ;
		}
		
		if(dp==null)
			return null ;
		
		if(dp.fromJO(jo,failedr))
			return null ;
		return dp ;
	}
}
