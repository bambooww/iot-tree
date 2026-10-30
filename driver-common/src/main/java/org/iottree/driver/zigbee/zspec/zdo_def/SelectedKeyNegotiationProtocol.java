package org.iottree.driver.zigbee.zspec.zdo_def;


public enum SelectedKeyNegotiationProtocol
{
	/** (Zigbee 3.0 Mechanism) */
	RESERVED(0),
	/** SPEKE using Curve25519 with Hash AES-MMO-128 */
	SPEKE_CURVE25519_AESMMO128(1),
	/** SPEKE using Curve25519 with Hash SHA-256 */
	SPEKE_CURVE25519_SHA256(2);

	// 3 – 255 Reserved
	private final int val;

	SelectedKeyNegotiationProtocol(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static SelectedKeyNegotiationProtocol valOfInt(int v)
	{
		for (SelectedKeyNegotiationProtocol st : SelectedKeyNegotiationProtocol.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
