package org.iottree.driver.zigbee.ember.types;

/**
 * EmberSourceRouteDiscoveryMode
 * 
 * Types of source route discovery modes used by the concentrator.
 *
 * OFF no source route discovery is scheduled
 *
 * ON source routes discovery is scheduled, and it is triggered periodically
 *
 * RESCHEDULE  source routes discoveries are re-scheduled to be sent once immediately and then triggered periodically
 */
public enum EmberSrcRouteDiscMode
{
	/** off  */
    OFF(0x00),
    /** on  */
    ON(0x01),
    /** reschedule */
    RESCHEDULE(0x02);
    
    private final int val;

    EmberSrcRouteDiscMode(int val)
	{
		this.val =val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberSrcRouteDiscMode valOfInt(int v)
	{
		for (EmberSrcRouteDiscMode st : EmberSrcRouteDiscMode.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
