package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

/** This data structure contains the metadata pertaining to an APS key */
public class SecManAPSKeyMeta
{
	public int bitmask ; //EmberKeyStruct. uint16_t: EmberKeyStructBitmask;
	/** valid only if bitmask & EMBER_KEY_HAS_OUTGOING_FRAME_COUNTER uint32_t */
	public long outgoingFrameCounter; //: number;
	/** valid only if bitmask & EMBER_KEY_HAS_INCOMING_FRAME_COUNTER uint32_t */
	public long incomingFrameCounter ; //: number;
	/** valid only if core_key_type == SL_ZB_SEC_MAN_KEY_TYPE_TC_LINK_WITH_TIMEOUT uint16_t */
	public int ttlInSeconds ; //: number; //
	
	public JSONObject toJO()
	{
		return new JSONObject().put("bitmask", this.bitmask).put("out_frm_cc", this.outgoingFrameCounter)
				.put("in_frm_cc", this.incomingFrameCounter).put("ttl_in_sec", this.ttlInSeconds) ;
	}
	
	public static final int STRUCT_LEN = 12 ;
	
	
}
