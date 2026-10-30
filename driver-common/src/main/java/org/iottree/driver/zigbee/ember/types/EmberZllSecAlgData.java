package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

/** Information about the ZLL security state and how to transmit the network key to the device securely. */
public class EmberZllSecAlgData
{
	 /** uint32_t */
    public long transactionId;
    /** uint32_t */
    public long responseId;
    /** uint16_t */
    public int bitmask;
    
    public JSONObject toJO()
    {
    	return new JSONObject().put("transactionid", this.transactionId).put("rsp_id", this.responseId)
    			.put("bitmask", this.bitmask) ;
    }
}
