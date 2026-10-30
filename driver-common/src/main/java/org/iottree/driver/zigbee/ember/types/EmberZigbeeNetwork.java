package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

public class EmberZigbeeNetwork
{
	/** uint16_t */
    public int panId;
    /** uint8_t */
    public int channel;
    /** bool */
    public boolean allowingJoin;
    /** uint8_t[EXTENDED_PAN_ID_SIZE] */
    public String extendedPanId;
    /** uint8_t */
    public int stackProfile;
    /** uint8_t */
    public int nwkUpdateId;
    
    public JSONObject toJO()
    {
    	return new JSONObject().put("panid", this.panId).put("ch", this.channel).put("allow_join", this.allowingJoin)
    			.putOpt("ext_panid", this.extendedPanId).put("stack_prof", this.stackProfile).put("nwk_upid", this.nwkUpdateId) ;
    }
}
