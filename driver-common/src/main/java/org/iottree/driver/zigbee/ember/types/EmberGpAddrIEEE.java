package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

public class EmberGpAddrIEEE extends EmberGpAddr
{
	/** The IEEE address is used when the application identifier is ::EMBER_GP_APPLICATION_IEEE_ADDRESS. */
    public String gpdIeeeAddress;
    
    
    public EmberGpAddrIEEE()
    {
    	applicationId = EmberGpAppId.IEEE_ADDRESS;
    }
    
    @Override
	public String getAddrStr()
	{
		return gpdIeeeAddress;
	}
    
    public JSONObject toJ()
    {
    	return super.toJ().putOpt("ieee", this.gpdIeeeAddress) ;
    }

	
}
