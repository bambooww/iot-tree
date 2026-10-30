package org.iottree.driver.zigbee.ember.types;

/** Manufacturing token IDs used by ezspGetMfgToken(). */
public enum EzspMfgTokenId
{
	/** Custom version (2 bytes). */
    CUSTOM_VERSION(0x00),
    /** Manufacturing string (16 bytes). */
    STRING(0x01),
    /** Board name (16 bytes). */
    BOARD_NAME(0x02),
    /** Manufacturing ID (2 bytes). */
    MANUF_ID(0x03),
    /** Radio configuration (2 bytes). */
    PHY_CONFIG(0x04),
    /** Bootload AES key (16 bytes). */
    BOOTLOAD_AES_KEY(0x05),
    /** ASH configuration (40 bytes). */
    ASH_CONFIG(0x06),
    /** EZSP storage (8 bytes). */
    SL_ZIGBEE_EZSP_STORAGE(0x07),
    /**
     * Radio calibration data (64 bytes). 4 bytes are stored for each of the 16 channels.
     * This token is not stored in the Flash Information Area.
     * It is updated by the stack each time a calibration is performed.
     */
    SL_ZIGBEE_EZSP_STACK_CAL_DATA(0x08),
    /** Certificate Based Key Exchange (CBKE) data (92 bytes). */
    CBKE_DATA(0x09),
    /** Installation code (20 bytes). */
    INSTALLATION_CODE(0x0a),
    /**
     * Radio channel filter calibration data (1 byte). This token is not stored in the Flash Information Area.
     * It is updated by the stack each time a calibration is performed.
     */
    SL_ZIGBEE_EZSP_STACK_CAL_FILTER(0x0b),
    /** Custom EUI64 MAC address (8 bytes). */
    CUSTOM_EUI_64(0x0c),
    /** CTUNE value (2 byte). */
    CTUNE(0x0d);
    
    private final int val;

    EzspMfgTokenId(int val)
	{
		this.val= val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EzspMfgTokenId valOfInt(int v)
	{
		for (EzspMfgTokenId st : EzspMfgTokenId.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
