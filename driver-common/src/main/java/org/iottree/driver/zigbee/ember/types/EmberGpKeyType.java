package org.iottree.driver.zigbee.ember.types;

public enum EmberGpKeyType
{
	/** No Key */
	NONE(0x00),
	/** GP Security Key Type is Zigbee Network Key */
	NWK(0x01),
	/** GP Security Key Type is Group Key */
	GPD_GROUP(0x02),
	/** GP Security Key Type is Derived Network Key */
	NWK_DERIVED(0x03),
	/** GP Security Key Type is Out Of Box Key */
	GPD_OOB(0x04),
	/** GP Security Key Type is GPD Derived Key */
	GPD_DERIVED(0x07);

	private final int val;

	EmberGpKeyType(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EmberGpKeyType valOfInt(int v)
	{
		for (EmberGpKeyType st : EmberGpKeyType.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
