package org.iottree.driver.zigbee.zspec.zdo_def;

public enum SelectedPreSharedSecret
{
	/** Symmetric Authentication Token */
	SYMMETRIC_AUTHENTICATION_TOKEN(0),
	/** Pre-configured link-ley derived from installation code */
	PRECONFIGURED_LINKKEY_DERIVED_FROM_INSTALL_CODE(1),
	/** Variable-length pass code (for PAKE protocols) */
	PAKE_VARIABLE_LENGTH_PASS_CODE(2),
	/** Basic Authorization Key */
	BASIC_AUTHORIZATION_KEY(3),
	/** Administrative Authorization Key */
	ADMIN_AUTHORIZATION_KEY(4),
	// 5 – 254 Reserved,
	/** Anonymous Well-Known Secret */
	ANONYMOUS_WELLKNOWN_SECRET(255);

	private final int val;

	SelectedPreSharedSecret(int val)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static SelectedPreSharedSecret valOfInt(int v)
	{
		for (SelectedPreSharedSecret st : SelectedPreSharedSecret.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
