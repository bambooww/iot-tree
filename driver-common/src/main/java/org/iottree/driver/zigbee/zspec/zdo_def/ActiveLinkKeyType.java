package org.iottree.driver.zigbee.zspec.zdo_def;

public enum ActiveLinkKeyType
{
	NOT_UPDATED(0x00),

	KEY_REQUEST_METHOD(0x01),

	UNAUTHENTICATED_KEY_NEGOTIATION(0x02),

	AUTHENTICATED_KEY_NEGOTIATION(0x03),

	APPLICATION_DEFINED_CERTIFICATE_BASED_MUTUAL_AUTHENTICATION(0x04);

	private final int val;

	ActiveLinkKeyType(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static ActiveLinkKeyType valOfInt(int v)
	{
		for (ActiveLinkKeyType st : ActiveLinkKeyType.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
