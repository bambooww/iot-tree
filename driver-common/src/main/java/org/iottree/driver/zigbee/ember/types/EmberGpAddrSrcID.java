package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

public class EmberGpAddrSrcID extends EmberGpAddr
{
	/** The 32-bit source identifier is used when the application identifier is ::EMBER_GP_APPLICATION_SOURCE_ID. */
    public long sourceId;
    
    public EmberGpAddrSrcID()
    {
    	applicationId = EmberGpAppId.SOURCE_ID;
    }
    
    @Override
	public String getAddrStr()
	{
		return ""+sourceId;
	}
    
    public JSONObject toJ()
    {
    	return super.toJ().put("src_id", this.sourceId);
    }
}
