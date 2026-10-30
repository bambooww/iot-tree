package org.iottree.driver.zigbee.zspec;

import java.util.List;

import org.iottree.core.UADev;
import org.iottree.core.UATag;
import org.iottree.driver.zigbee.zcl.ZCLAttr;
import org.iottree.driver.zigbee.zcl.util.AttrVal;
import org.iottree.driver.zigbee.zcl.util.DataType;
import org.iottree.driver.zigbee.zcl.util.TagDef;
import org.json.JSONObject;

public class ZSAttrVal implements Comparable<ZSAttrVal>
{
	ZSCluster owner ;
	
	ZCLAttr attrDef ;
	
	//AttrVal val = null ;
	private Object objV = null ;
	
	private long updateDT = -1 ;
	
	public ZSAttrVal(ZSCluster owner,ZCLAttr attrdef)
	{
		this.owner = owner ;
		this.attrDef = attrdef ;
	}
	
	public ZSAttrVal(ZSCluster owner,int attr_id)
	{
		this.owner = owner ;
		this.attrDef = owner.getAttributeDef(attr_id) ;
		if(this.attrDef==null)
			throw new IllegalArgumentException("no attribute def found with id="+attr_id) ;
	}
	
//	public ZSAttrVal(ZSCluster owner,int attr_id,Object objv,long update_dt)
//	{
//		ZCLAttr attr = owner.getAttributeDef(attr_id) ;
//		if(attr==null)
//			throw new IllegalArgumentException("no attribute def found with id="+attr_id) ;
//		
//		this.owner = owner ;
//		this.val = new AttrVal(attr,objv) ;
//		this.updateDT = update_dt ;
//	}
	
	public ZSCluster getOwner()
	{
		return this.owner ;
	}
	
	public int getAttrId()
	{
		return this.attrDef.getID();
	}

	public ZCLAttr getAttrDef()
	{
		return attrDef;
	}
	

	public DataType getDataType()
	{
		return this.attrDef.getDataType() ;
	}
	
	public String getValTP()
	{
		DataType dtp = getDataType() ;
		if(dtp==null)
			return null ;
		return dtp.getUAValTP() ;
	}
	
	public Object getObjVal()
	{
		return this.objV; //val.getDataVal().getObjVal() ;
	}
	

	public long getUpdateDT()
	{
		return this.updateDT ;
	}
	
	public void RT_setObjVal(long update_dt,Object objv)
	{
		this.updateDT = update_dt ;
		this.objV = objv ;
	}
	
	public String getTagAddr()
	{
		return this.owner.calcTagAddr(this.getAttrId()) ;
	}
	
	public TagDef getTagDef()
	{
		return this.attrDef.getTagDef() ;
	}
	
	public List<UATag> getRelatedTag()
	{
		String addr = this.getTagAddr() ;
		UADev dev = this.owner.getOwner().getOwner().getRelatedUADev() ;
		if(dev==null)
			return null ;
		return dev.filterTagsAllByAddr(addr) ;
	}
	
//	public AttrVal getVal()
//	{
//		return this.val ;
//	}
	
	
	

	public JSONObject RT_toJO()
	{
		JSONObject r = new JSONObject() ;
		String tagaddr = this.getTagAddr() ;
		r.put("tag_addr", tagaddr) ;
		r.putOpt("val", this.getObjVal()) ;
		r.put("update_dt",this.updateDT) ;
		
		r.put("attr_id", this.getAttrId()) ;
		TagDef td = this.getTagDef() ;
		if(td!=null)
		{
			r.putOpt("unit",td.getUnit()) ;
		}
		return r ;
	}
	
	
	public JSONObject toJO()
	{
		JSONObject r = new JSONObject();
		r.put("up_dt",this.updateDT) ;
		r.putOpt("val", this.objV) ;
		r.put("attr_id", this.getAttrId()) ;
		return r ;
	}
	
	public static ZSAttrVal fromJO(ZSCluster c,JSONObject jo)
	{
		int attr_id = jo.optInt("attr_id", -1) ;
		if(attr_id<0)
			return null ;
		ZSAttrVal r = new ZSAttrVal(c,attr_id) ;
		r.updateDT = jo.optLong("up_dt",-1) ;
		r.objV = jo.opt("val") ;
		return r ;
	}
	
	void RT_onUpdate(Object objv)
	{
		this.objV = objv ;
		this.updateDT = System.currentTimeMillis() ;
	}

	@Override
	public int compareTo(ZSAttrVal o)
	{
		return this.getAttrId() - o.getAttrId();
	}
}
