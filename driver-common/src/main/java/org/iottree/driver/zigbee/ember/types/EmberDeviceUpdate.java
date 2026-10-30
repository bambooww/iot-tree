package org.iottree.driver.zigbee.ember.types;

/**
 * sl_zigbee_device_update_t	uint8_t	The status of the device update.
 * 
 * @author jason.zhu
 */
public enum EmberDeviceUpdate
{
	STANDARD_SECURITY_SECURED_REJOIN(0),

	STANDARD_SECURITY_UNSECURED_JOIN(1),

	DEVICE_LEFT(2),

	STANDARD_SECURITY_UNSECURED_REJOIN(3);

	private final int val;

	EmberDeviceUpdate(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberDeviceUpdate valOfInt(int v)
	{
		for (EmberDeviceUpdate st : EmberDeviceUpdate.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
