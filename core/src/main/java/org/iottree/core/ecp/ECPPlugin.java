package org.iottree.core.ecp;

import org.json.JSONObject;

public class ECPPlugin extends ECPBase
{
	// inner x ratio in unit [0-1]
	float in_x = 0 ;
	
	// inner y ratio in unit [0-1]
	float in_y = 0; 
	
	ECPNode owner ;
	
	
	public ECPPlugin(ECPNode owner,String id)
	{
		super(id);
		this.owner = owner ;
	}
	
	public ECPPlugin copyMe(ECPNode nd)
	{
		ECPPlugin r = new ECPPlugin(nd,this.getId()) ;
		r.setBasic(this.getTitle(), this.getDesc());
		r.in_x = this.in_x ;
		r.in_y = this.in_y ;
		return r ;
	}
//	@Override
//	public String getTP()
//	{
//		return TP;
//	}
	
	public String getUID()
	{
		return this.owner.getId()+"-"+this.getId() ;
	}
	
	public ECPNode getOwner()
	{
		return this.owner ;
	}
	
	public float getInX()
	{
		return this.in_x ;
	}
	
	public float getInY()
	{
		return this.in_y ;
	}
	
	
	@Override
	public boolean removeMe()
	{
		return this.owner.removePlugin(getId())!=null ;
	}
	
	
	JSONObject toRefJO()
	{//for line save
		JSONObject r = new JSONObject()
				.put("id",this.getId());
		if(owner!=null)
			r.put("owner_id", this.owner.getId()) ;
		return r ;
	}
	
	public JSONObject toJO()
	{
		JSONObject ret = super.toJO() ;
		ret.put("ix", this.in_x).put("iy", this.in_y);
		if(owner!=null)
			ret.put("owner_id", this.owner.getId()) ;
		return ret ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		super.fromJO(jo);
		this.in_x = jo.optFloat("ix",0);
		this.in_y = jo.optFloat("iy",0);
		return true;
	}
	
	public void updateStructByJO(JSONObject jo)
	{
		this.fromJO(jo) ;
	}
}
