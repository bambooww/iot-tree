package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

/** This data structure contains the metadata pertaining to an network key */
public class SecManNwkKeyInf
{
	public boolean networkKeySet;
	public boolean alternateNetworkKeySet;
	/** uint8_t */
	public int networkKeySequenceNumber;
	/** uint8_t */
	public int altNetworkKeySequenceNumber;
	/** uint32_t */
	public long networkKeyFrameCounter;
	
	public JSONObject toJO()
	{
		return new JSONObject().put("nwk_key_set",this.networkKeySet).put("alt_nwk_key_set", this.alternateNetworkKeySet)
				.put("nwk_key_seq", this.networkKeySequenceNumber).put("nwk_key_frm_c", this.networkKeyFrameCounter) ;
	}
	
	public static final int STRUCT_LEN = 8 ;
}
