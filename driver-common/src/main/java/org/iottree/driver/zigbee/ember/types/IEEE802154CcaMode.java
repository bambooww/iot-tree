package org.iottree.driver.zigbee.ember.types;

public enum IEEE802154CcaMode
{
	/** RSSI-based CCA. CCA reports a busy medium upon detecting any energy above -75 (default RAIL_CsmaConfig_t.ccaThreshold). */
    RSSI(0),
    /**
     * Signal Identifier-based CCA. CCA reports a busy medium only upon the detection of a signal compliant with this standard
     * with the same modulation and spreading characteristics of the PHY that is currently in use.
     */
    SIGNAL(1),
    /**
     * RSSI or signal identifier-based CCA. CCA reports a busy medium on either detecting any energy above
     * -75 (default RAIL_CsmaConfig_t.ccaThreshold) or detection of a signal compliant with this standard with the same modulation
     * and spreading characteristics of the PHY that is currently in use.
     */
    SIGNAL_OR_RSSI(2),
    /**
     * RSSI and signal identifier-based CCA. CCA reports a busy medium only on detecting any energy above -75 (default RAIL_CsmaConfig_t.ccaThreshold)
     * of a signal compliant with this standard with the same modulation and spreading characteristics of the PHY that is currently in use.
     */
    SIGNAL_AND_RSSI(3),
    /** ALOHA. Always transmit CCA=1. CCA always reports an idle medium. */
    ALWAYS_TRANSMIT(4);
    
    private final int val;

    IEEE802154CcaMode(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static IEEE802154CcaMode valOfInt(int v)
	{
		for (IEEE802154CcaMode st : IEEE802154CcaMode.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
