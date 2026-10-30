package org.iottree.driver.zigbee.ember.types;

/**
 * Derived keys are calculated when performing Zigbee crypto operations. The stack makes use of these derivations.
 * Compounding derivations can be specified by using an or-equals on two derived types if applicable;
 * this is limited to performing the key-transport, key-load, or verify-key hashes on either the TC Swap Out or TC Hashed Link keys.
 * uint16_t
 */
public enum SecManDerivedKeyType
{
	/** Perform no derivation; use the key as is. */
    NONE(0x0000),
    /** Perform the Key-Transport-Key hash. */
    KEY_TRANSPORT_KEY(0x0001),
    /** Perform the Key-Load-Key hash. */
    KEY_LOAD_KEY(0x0002),
    /** Perform the Verify Key hash. */
    VERIFY_KEY(0x0004),
    /** Perform a simple AES hash of the key for TC backup. */
    TC_SWAP_OUT_KEY(0x0008),
    /** For a TC using hashed link keys, hashed the root key against the supplied EUI in context. */
    TC_HASHED_LINK_KEY (0x0010);
    
	private final int val;
	// private final String title;

	SecManDerivedKeyType(int val) // , String title)
	{
		this.val = val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static SecManDerivedKeyType valOfInt(int v)
	{
		for (SecManDerivedKeyType st : SecManDerivedKeyType.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
