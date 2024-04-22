package org.iottree.core.router;

import org.iottree.core.UAPrj;
import org.iottree.core.util.ILang;
import org.iottree.core.util.IdCreator;
import org.json.JSONObject;

public abstract class RouterJoin implements ILang
{
	RouterManager belongTo ;
	
	UAPrj belongPrj ;
	
	RouterNode belongNode ;
	
	String name ;
	
	String title = null;
	
	String desc = null ;
	
	public RouterJoin(RouterNode node,String name) //,String title,String desc)
	{
		this.belongTo = node.belongTo ;
		if(this.belongTo!=null)
			this.belongPrj = this.belongTo.belongTo ;
//		this.id = IdCreator.newSeqId() ;
		this.belongNode = node ;
		this.name = name ;
//		this.title = title ;
//		this.desc = desc ;
	}
	

	public RouterNode getBelongNode()
	{
		return this.belongNode ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public String getTitle()
	{
		if(this.title!=null)
			return title ;
		return g("j_"+this.name) ;
	}
	
	public String getDesc()
	{
		if(this.desc!=null)
			return this.desc ;
		return g("j_"+this.name,"desc") ;
	}
	
	public void setTitleDesc(String t,String d)
	{
		this.title = t ;
		if(this.title==null)
			this.title = "" ;
		this.desc = d ;
		if(this.desc==null)
			this.desc = d ;
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject() ;
		//jo.put("id", id) ;
		jo.put("n", this.getName()) ;
		return jo ;
	}
	
	public JSONObject toListJO()
	{
		JSONObject jo = this.toJO() ;
		jo.putOpt("t", this.getTitle()) ;
		jo.putOpt("d", this.getDesc()) ;
		return jo;
	}
	
	protected boolean fromJO(JSONObject jo,StringBuilder failedr)
	{
		//this.id = jo.getString("id") ;
		return true ;
	}
	

	private long rtLastDT = -1 ;
	
	private String rtLastErr = null ;
	
	private Exception rtLastExp = null ;
	
	private Object rtLastData = null ;
	

	void RT_setLastData(Object data)
	{
		this.rtLastDT = System.currentTimeMillis() ;
		this.rtLastData = data ;
	}
	
	void RT_fireLastErr(String err,Exception ee)
	{
		this.rtLastDT = System.currentTimeMillis() ;
		this.rtLastErr = err ;
		this.rtLastExp = ee ;
	}
	
	public long RT_getLastDT()
	{
		return this.rtLastDT ;
	}
	
	public Object RT_getLastData()
	{
		return this.rtLastData ;
	}
	
	public JSONObject RT_getRunInf()
	{
		JSONObject jo = new JSONObject() ;
		jo.put("rt_err_dt", this.rtLastDT) ;
		jo.putOpt("rt_last_err", this.rtLastErr) ;
		
		//jo.putOpt(, value)
		return jo ;
	}
}