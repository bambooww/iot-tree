package org.iottree.driver.zigbee.zcl;

import java.util.HashMap;

import org.iottree.core.UAVal;
import org.iottree.core.util.Convert;
import org.iottree.core.util.IJoIn;
import org.iottree.driver.zigbee.zcl.util.DataType;
import org.iottree.driver.zigbee.zcl.util.TagDef;
import org.json.JSONArray;
import org.json.JSONObject;

/**
 * ZCL Parameter
 * 
 * @author jason.zhu
 */
public class ZCLParam implements IJoIn
{
	int idx;
	
	String name ;
	
	int dataTpV ;
	
	private DataType dataTp = null ; 
	

	boolean required = false;
	
	HashMap<String,String> specialStrv2Title = null ;
	
	TagDef tagDef = null ;
	
	public ZCLParam(int idx)
	{
		this.idx = idx ;
	}
	
	public int getIdx()
	{
		return idx ;
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
	
	public TagDef getTagDef()
	{
		return this.tagDef ;
	}
	
	public JSONObject toJO()
	{
		JSONObject r = new JSONObject().put("name", this.name)
				.put("data_tp_v", this.dataTpV).putOpt("data_tp", this.getDataType());
		if(this.required)
			r.put("required",true) ;
		if(this.specialStrv2Title!=null)
			r.putOpt("special", new JSONObject(this.specialStrv2Title));
		
		if(this.tagDef!=null)
			r.put("__tag", this.tagDef.toJO()) ;
		
		return r ;
	}

	@Override
	public boolean fromJO(JSONObject jo, StringBuilder failedr)
	{
		this.name = jo.optString("name") ;
		if(Convert.isNullOrEmpty(this.name))
			return false;
		this.dataTpV = jo.optInt("type") ;
		this.required = jo.optBoolean("required",false) ;
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
		Object tago = jo.opt("__tag") ;
		if(tago!=null)
			this.tagDef = TagDef.fromObj(getDataType(),tago) ;
		return true;
	}
}
