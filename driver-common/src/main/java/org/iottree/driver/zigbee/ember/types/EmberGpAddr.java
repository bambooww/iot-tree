package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

/**
 * GPD Address for sending and receiving a GPDF.
 * EmberGpAddress_gpdIeeeAddress | EmberGpAddress_sourceId;
 */
public abstract class EmberGpAddr
{
	/** Application identifier of the GPD. */
    public EmberGpAppId applicationId;
    /** Application endpoint, only used when application identifier is ::EMBER_GP_APPLICATION_IEEE_ADDRESS. uint8_t */
    public int endpoint;
    
    public abstract String getAddrStr() ;
    
	public JSONObject toJ()
    {
    	return new JSONObject().putOpt("gp_appid", this.applicationId)
    			.put("endpoint", this.endpoint) ;
    }
}
