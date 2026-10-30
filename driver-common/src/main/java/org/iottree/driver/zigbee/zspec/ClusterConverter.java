package org.iottree.driver.zigbee.zspec;

import org.iottree.driver.zigbee.zcl.ZCLCluster;
import org.json.JSONArray;

/**
 * cluster def to iot-tree tags
 * 
 * @author jason.zhu
 */
public class ClusterConverter
{
	ZCLCluster clusterDef ;
	
	public ClusterConverter(ZCLCluster c_def)
	{
		this.clusterDef = c_def ;
	}
	
	public ZCLCluster getClusterDef()
	{
		return this.clusterDef ;
	}
	
	public JSONArray getRelatedTagsJArr()
	{
		JSONArray jarr = new JSONArray() ;
		switch(clusterDef.getID())
		{
		case 0x0006:
			setupCluster_genOnOff(jarr) ;
			break ;
		}
		return jarr ;
	}
	
	private void setupCluster_genOnOff(JSONArray jarr)
	{
		
	}
}
