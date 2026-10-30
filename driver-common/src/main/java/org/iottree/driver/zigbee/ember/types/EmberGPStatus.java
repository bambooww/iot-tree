package org.iottree.driver.zigbee.ember.types;

public enum EmberGPStatus
{
	/** Success Status */
	OK(0),
	/** Match Frame */
	MATCH(1),
	/** Drop Frame */
	DROP_FRAME(2),
	/** Frame Unprocessed */
	UNPROCESSED(3),
	/** Frame Pass Unprocessed */
	PASS_UNPROCESSED(4),
	/** Frame TX Then Drop */
	TX_THEN_DROP(5),
	/** No Security */
	NO_SECURITY(6),
	/** Security Failure */
	AUTH_FAILURE(7);

	private final int val;

	EmberGPStatus(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberGPStatus valOfInt(int v)
	{
		for (EmberGPStatus st : EmberGPStatus.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
