package org.iottree.core.ecp;

import java.util.ArrayList;
import java.util.LinkedHashMap;
import java.util.List;

import org.iottree.core.ecp.util.Jt;
import org.iottree.core.ecp.util.Point;
import org.iottree.core.ecp.util.PtId;
import org.json.JSONObject;

/**
 * joint that can in unit border or line mid point
 * 
 * @author jason.zhu
 *
 */
public class ECPInOut extends ECPBase
{
	// inner x ratio in unit [0-1]
	float in_x = 0 ;
	
	// inner y ratio in unit [0-1]
	float in_y = 0; 
	
	int r90 = 0 ; //0-right 1-down 2-left 3-up
			
	
	ECPNode owner ;
	
	// in=false out=false represent no inout meaning,like gnd
	boolean bOut = true ;
	
	boolean bIn = true ;
	
	public ECPInOut(ECPNode owner,String id)
	{
		super(id);
		
		this.owner = owner ;
	}
	
	public ECPInOut copyMe(ECPNode owner)
	{
		ECPInOut r = new ECPInOut(owner,this.getId()) ;
		r.setBasic(this.getTitle(), this.getDesc());
		r.in_x = this.in_x ;
		r.in_y = this.in_y ;
		r.bOut = this.bOut ;
		r.bIn = this.bIn ;
		return r ;
	}
//	@Override
//	public String getTP()
//	{
//		return TP;
//	}
	
	public ECPNet getNet()
	{
		return this.owner.getNet() ;
	}
	
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
	
	public int getDir90()
	{
		return this.r90 ;
	}
	
	public boolean isOut()
	{
		return this.bOut ;
	}
	
	public boolean isIn()
	{
		return this.bIn ;
	}
	
	public String getJtId()
	{
		return Jt.JT_TP_IO+":"+this.getUID() ;
	}
	
	@Override
	public boolean removeMe()
	{
		return this.owner.removeInOut(getId())!=null ;
	}
	
	public List<ECPConn> getConnsDirect()
    {//直接连接的conn
        ArrayList<ECPConn> r= new ArrayList<>() ;
        String jt_id = this.getJtId() ;
        LinkedHashMap<String,ECPConn> id2conn = this.getNet().getId2Conn() ;
        if(id2conn!=null)
        {
	        for(ECPConn item : id2conn.values())
	        {
	            String fjtid = item.getFromJtID();
	            String tjtid = item.getToJtID();
	            if(jt_id.equals(fjtid) || jt_id.equals(tjtid))
	                r.add(item) ;
	        }
        }
        return r ;
    }
	
	/**
	 * all related conns
	 * @return
	 */
	public List<ECPConn> getConnsRelated()
	{
		List<ECPConn> conns = getConnsDirect() ;
		
		return conns ;
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
		ret.put("ix", this.in_x).put("iy", this.in_y).put("r90", this.r90);
		if(owner!=null)
			ret.put("owner_id", this.owner.getId()) ;
		ret.put("out", this.bOut).put("in", bIn);
		return ret ;
	}
	
	public boolean fromJO(JSONObject jo)
	{
		super.fromJO(jo);
		this.in_x = jo.optFloat("ix",0);
		this.in_y = jo.optFloat("iy",0);
		this.r90 = jo.optInt("r90",0) ;
		this.bIn = jo.optBoolean("in",true) ;
		this.bOut = jo.optBoolean("out",true) ;
		return true;
	}
	
	public void updateStructByJO(JSONObject jo)
	{
		this.fromJO(jo) ;
	}
}
