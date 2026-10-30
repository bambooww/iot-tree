package org.iottree.driver.zigbee.ember.types;

/** The type of method used for joining. uint8_t */
public enum EmberJoinMethod
{
	/** Devices normally use MAC association to join a network, which respects
     *  the "permit joining" flag in the MAC beacon.
     *  This value should be used by default.
     */
    MAC_ASSOCIATION(0),
    /** For networks where the "permit joining" flag is never turned
     *  on, devices will need to use a Zigbee NWK Rejoin.  This value causes the
     *  rejoin to be sent withOUT NWK security and the Trust Center will be
     *  asked to send the NWK key to the device.  The NWK key sent to the device
     *  can be encrypted with the device's corresponding Trust Center link key.
     *  That is determined by the ::EmberJoinDecision on the Trust Center
     *  returned by the ::emberTrustCenterJoinHandler().
     */
    NWK_REJOIN(1),
    /* For networks where the "permit joining" flag is never turned
     * on, devices will need to use a NWK Rejoin.  If those devices have been
     * preconfigured with the  NWK key (including sequence number), they can use
     * a secured rejoin.  This is only necessary for end devices since they need
     * a parent.  Routers can simply use the ::CONFIGURED_NWK_STATE
     * join method below.
     */
    NWK_REJOIN_HAVE_NWK_KEY(2),
    /** For networks where all network and security information is known
         ahead of time, a router device may be commissioned such that it does
        not need to send any messages to begin communicating on the network.
    */
    CONFIGURED_NWK_STATE(3),
    /** This enumeration causes an unencrypted Network Commissioning Request to be
         sent out with joinType set to initial join. The trust center may respond
        by establishing a new dynamic link key and then sending the network key.
        Network Commissioning Requests should only be sent to parents that support
        processing of the command.
    */
    NWK_COMMISSIONING_JOIN(4),
    /** This enumeration causes an unencrypted Network Commissioning Request to be
         sent out with joinType set to rejoin. The trust center may respond
        by establishing a new dynamic link key and then sending the network key.
        Network Commissioning Requests should only be sent to parents that support
        processing of the command.
    */
    NWK_COMMISSIONING_REJOIN(5),
    /** This enumeration causes an encrypted Network Commissioning Request to be
         sent out with joinType set to rejoin. This enumeration is used by devices
        that already have the network key and wish to recover connection to a
        parent or the network in general.
        Network Commissioning Requests should only be sent to parents that support
        processing of the command.
    */
    NWK_COMMISSIONING_REJOIN_HAVE_NWK_KEY(6);

	private final int val;

	EmberJoinMethod(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberJoinMethod valOfInt(int v)
	{
		for (EmberJoinMethod st : EmberJoinMethod.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
