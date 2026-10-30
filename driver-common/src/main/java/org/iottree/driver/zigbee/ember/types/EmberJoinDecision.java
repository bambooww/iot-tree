package org.iottree.driver.zigbee.ember.types;

/** 
 * The decision made by the Trust Center when a node attempts to join. uint8_t 
 */
public enum EmberJoinDecision
{
	/** Allow the node to join. The node has the key. */
    USE_PRECONFIGURED_KEY(0),
    /** Allow the node to join. Send the key to the node. */
    SEND_KEY_IN_THE_CLEAR(1),
    /** Deny join. */
    DENY_JOIN(2),
    /** Take no action. */
    NO_ACTION(3),
    /** Allow rejoins only.*/
    ALLOW_REJOINS_ONLY(4);
    
    private final int val;

    EmberJoinDecision(int val)
	{
		this.val =val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberJoinDecision valOfInt(int v)
	{
		for (EmberJoinDecision st : EmberJoinDecision.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
