package org.iottree.driver.zigbee.zcl;

import java.util.HashMap;

import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IJoIn;
import org.iottree.driver.zigbee.zcl.util.DataType;
import org.iottree.driver.zigbee.zcl.util.TagDef;
import org.json.JSONObject;

/**
 * ZCL Attribute 
 * @author jason.zhu
 */
public class ZCLAttr implements IJoIn
{
	int id ;
	
	String name ;
	
	int dataTpV ; //DataType
	
	private DataType dataTp = null ; 
	
	boolean required = false;
	
	boolean write = false;//
	
	boolean writeOptional = false;
	
	String minStr = null ;
	
	String maxStr = null ;
	
	String defaultStr = null ;
	
	int maxLen = -1 ;
	
	HashMap<String,String> specialStrv2Title = null ;
	
	TagDef tagDef = null ;
	
	public ZCLAttr()
	{}
	
	public int getID()
	{
		return this.id ;
	}
	
	public String getName()
	{
		return this.name ;
	}
	
	public int getDataTpV()
	{
		return this.dataTpV ;
	}
	
	public DataType getDataType()
	{
		if(this.dataTp!=null)
			return this.dataTp ;
		return this.dataTp = DataType.valOfInt(this.dataTpV) ;
	}
	
	public UAVal.ValTP getUAValTP()
	{
		DataType dt = getDataType() ;
		if(dt==null)
			return null ;
		String tpstr = dt.getUAValTP() ;
		if(Convert.isNullOrEmpty(tpstr))
			return null ;
		return UAVal.getValTp(tpstr) ;
	}
	
	public boolean isRequired()
	{
		return this.required ;
	}
	
	public boolean isWrite()
	{
		return this.write ;
	}
	
	public boolean isWriteOptional()
	{
		return this.writeOptional ;
	}
	
	public String getMinStr()
	{
		return this.minStr;
	}
	
	public String getMaxStr()
	{
		return this.maxStr;
	}
	
	public String getDefaultStr()
	{
		return this.defaultStr;
	}
	
	public TagDef getTagDef()
	{
		return this.tagDef ;
	}
	
	public JSONObject toJO()
	{
		JSONObject r = new JSONObject().put("id", this.id).put("name", this.name)
				.put("data_tp_v", this.dataTpV).putOpt("data_tp", this.getDataType());
		if(this.required)
			r.put("required",true) ;
		if(write)
			r.put("write", true) ;
		if(writeOptional)
			r.put("writeOptional", true) ;
		r.putOpt("min_str",this.minStr);
		r.putOpt("max_str",this.maxStr);
		r.putOpt("default_str",this.defaultStr);
		if(this.maxLen>0)
			r.put("max_len", this.maxLen) ;
		if(this.specialStrv2Title!=null)
			r.putOpt("special", new JSONObject(this.specialStrv2Title));
		if(this.tagDef!=null)
			r.put("__tag", this.tagDef.toJO()) ;
		return r ;
	}
	
	@Override
	public boolean fromJO(JSONObject jo, StringBuilder failedr)
	{
		this.id = jo.optInt("ID") ;
		this.name = jo.optString("name") ;
		if(this.id<0 || Convert.isNullOrEmpty(this.name))
			return false;
		this.dataTpV = jo.optInt("type") ;
		this.required = jo.optBoolean("required",false) ;
		this.write = jo.optBoolean("write",false) ;
		this.writeOptional = jo.optBoolean("writeOptional",false) ;
		
		Object o = jo.opt("default") ;
		if(o!=null)
			this.defaultStr = o.toString() ;
		
		o = jo.opt("max") ;
		if(o!=null)
			this.maxStr = o.toString() ;
		
		o = jo.opt("min") ;
		if(o!=null)
			this.minStr = o.toString() ;
		
		this.maxLen = jo.optInt("maxLen") ;
		
		JSONObject sp_jo = jo.optJSONObject("special") ;
		if(sp_jo!=null&&sp_jo.length()>0)
		{
			this.specialStrv2Title = new HashMap<>()  ;
			for(String tmpk : sp_jo.keySet())
			{
				String tt = sp_jo.getString(tmpk) ;
				this.specialStrv2Title.put(tmpk,tt) ;
			}
		}
		
		this.tagDef = TagDef.fromObj(getDataType(),jo.opt("__tag")) ;
		return true ;
	}
}
