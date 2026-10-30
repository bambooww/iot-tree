package org.iottree.driver.zigbee.zspec.util;

import org.json.JSONObject;

/**
 * LogicalType/
ComplexDescriptorAvailable/
UserDescriptorAvailable

 * @author jason.zhu
 *
 */
public class NodeLogicTpCDUD
{
	int val ;
	
	public NodeLogicTpCDUD(int v)
	{
		this.val = v ;
	}
	
	public LogicalType getLogicalType()
	{
		return LogicalType.valOfInt(this.val & 0x07);  //bit 0-2
	}
	
	/**
	 * Indicates if complex descriptor is available for the node
	 * @return
	 */
	public boolean isComplexDescAvailable()
	{
		return (this.val & 0x08) > 0 ; // bit 3
	}
	
	/**
	 *  Indicates if user descriptor is available for the node
	 * @return
	 */
	public boolean isUserDescAvailable()
	{
		return (this.val & 0x10) > 0 ; // bit 4
	}
	
	public JSONObject toJO()
	{
		JSONObject jo = new JSONObject();
		toJO(jo) ;
		return jo;
	}
	
	public void toJO(JSONObject jo)
	{
		jo.put("logic_tp",this.getLogicalType().toString()).put("complex_av", this.isComplexDescAvailable())
				.put("user_av", this.isUserDescAvailable()) ;
	}
	
	public String toString()
	{
		return "logic_tp="+this.getLogicalType()+" complex_desc="+isComplexDescAvailable()+" user_desc="+isUserDescAvailable() ;
	}
}
