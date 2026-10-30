package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

/**
 * Defines an entry in the binding table.
 *
 * A binding entry specifies a local endpoint, a remote endpoint, a
 * cluster ID and either the destination EUI64 (for unicast bindings) or the
 * 64-bit group address (for multicast bindings).
 */
public class BindingTableEntry
{
	/** The type of binding. uint8 */
	public BindingType type;
    /** The endpoint on the local node. uint8_t */
	public int local;
    /**
     * A cluster ID that matches one from the local endpoint's simple descriptor.
     * This cluster ID is set by the provisioning application to indicate which part an endpoint's functionality is bound
     * to this particular remote node and is used to distinguish between unicast and multicast bindings.
     * Note that a binding can be used to to send messages with any cluster ID, not just that listed in the binding.
     * uint16_t
     */
	public int clusterId ;
    /** The endpoint on the remote node (specified by identifier). uint8_t */
	public int remote;
    /**
     * A 64-bit identifier. This is either:
     * - The destination EUI64, for unicasts.
     * - A 16-bit multicast group address, for multicasts.
     */
    public String identifier;
    /** The index of the network the binding belongs to. uint8_t */
    public int networkIndex;
    
    public JSONObject toJO()
    {
    	return new JSONObject().putOpt("bind_tp", this.type).put("local", this.local).put("cluster_id", this.clusterId)
    			.put("remote", this.remote).putOpt("identifier",this.identifier).put("nwk_idx", this.networkIndex) ;
    }
}
