package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

public class EmberZllNetwork
{
	public EmberZigbeeNetwork zigbeeNetwork ;
	public EmberZllSecAlgData securityAlgorithm ;
	public String eui64; //: Eui64;
	public int nodeId;
	public EmberZllState state ;
	public EmberNodeType nodeType ;
	/** uint8_t */
	public int numberSubDevices;
	/** uint8_t */
	public int totalGroupIdentifiers;
	/** uint8_t */
	public int rssiCorrection;
	
	public JSONObject toJO()
	{
		return new JSONObject().putOpt("zigbee_nwk", zigbeeNetwork!=null?zigbeeNetwork.toJO():null)
				.putOpt("sec_alg", securityAlgorithm!=null?securityAlgorithm.toJO():null).putOpt("eui64", eui64)
				.put("node_id", this.nodeId).putOpt("state", this.state).put("node_tp", this.nodeType)
				.put("num_subdev", this.numberSubDevices).put("total_grp_id", this.totalGroupIdentifiers)
				.put("rssi_corr", this.rssiCorrection) ;
	}
}
