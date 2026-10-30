package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

/** Information discovered during a ZLL scan about the ZLL device's endpoint information. */
public class EmberZllDevInfoRecord
{
	public String ieeeAddress;
	/** uint8_t */
	public  int endpointId;
	/** uint16_t */
	public int profileId;
	/** uint16_t */
	public  int deviceId;
	/** uint8_t */
	public  int version;
	/** uint8_t */
	public int groupIdCount;
	
	public JSONObject toJO()
	{
		return new JSONObject().put("ieee", this.ieeeAddress).put("ep_id", this.endpointId).put("profile_id", this.profileId)
				.put("device_id", this.deviceId).put("ver", this.version).put("grp_id_cc", this.groupIdCount) ;
	}
}
