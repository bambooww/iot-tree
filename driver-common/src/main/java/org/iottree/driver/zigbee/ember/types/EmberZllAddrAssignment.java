package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

/** Network and group address assignment information. */
public class EmberZllAddrAssignment
{
	///** IEEE 802.15.4 node ID. Also known as short address. uint16 */
	public int nodeId;
	public int freeNodeIdMin;
	public int freeNodeIdMax;
	///** 16-bit Zigbee multicast group identifier. uint16_t */
	public int groupIdMin;//: EmberMulticastId;
	public int groupIdMax; //: EmberMulticastId;
	public int freeGroupIdMin;//: EmberMulticastId;
	public int freeGroupIdMax; //: EmberMulticastId;
	
	public JSONObject toJO()
	{
		return new JSONObject().put("node_id", this.nodeId).put("free_nodeid_min", this.freeNodeIdMin)
				.put("free_nodeid_max", this.freeNodeIdMax).put("grpid_min", this.groupIdMin).put("grpid_max", this.groupIdMax)
				.put("free_grpid_min", this.freeGroupIdMin).put("free_grpid_max", this.freeGroupIdMax) ;
	}
}
