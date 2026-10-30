package org.iottree.driver.zigbee.ember.types;

import org.json.JSONObject;

public class RxPacketInfo
{
	public int senderShortAddr ;
	
	public String senderIEEEAddr = null;
	/**
     * The index of the entry in the binding table that matches the sender of the message or 0xFF if there is no matching entry.
     * A binding matches the message if:
     *   - The binding's source endpoint is the same as the message's destination endpoint
     *   - The binding's destination endpoint is the same as the message's source endpoint
     *   - The source of the message has been previously identified as the binding's remote node by a successful address discovery
     *     or by the application via a call to either ::sl_zigbee_set_reply_binding() or ::sl_zigbee_note_senders_binding().
     * uint8_t
     */
	public int bindingIdx ;
	/** The index of the entry in the address table that matches the sender of the message or 0xFF if there is no matching entry. uint8_t */
	public int addrIdx ;
	/** Link quality of the node that last relayed the current message. uint8_t */
	public int lastHopLqi ;
	/** Received signal strength indicator (RSSI) of the node that last relayed the message. int8_t */
	public int lastHopRssi ;
	
	/* Timestamp of the moment when Start Frame Delimiter (SFD) was received. uint32_t */
	public long lastHopTimestamp=0;
	
	public JSONObject toJO()
	{
		return new JSONObject().put("sender_addr", this.senderShortAddr).put("sender_ieee", this.senderIEEEAddr)
				.put("bind_idx", this.bindingIdx).put("addr_idx", this.addrIdx).put("last_hop_lqi", this.lastHopLqi)
				.put("last_hop_rssi", this.lastHopRssi).put("last_hop_ts", this.lastHopTimestamp) ;
	}
	
	public static final int STRUCT_LEN = 20 ;
}
