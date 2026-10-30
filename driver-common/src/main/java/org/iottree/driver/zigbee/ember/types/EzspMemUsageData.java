package org.iottree.driver.zigbee.ember.types;

/** v17+ */
public enum EzspMemUsageData
{
	/** Gets the total available heap size in bytes */
    TOTAL_HEAP_SIZE(0x01),
    /** Gets the used heap size in bytes at the time requested */
    CURRENT_USED_HEAP_SIZE(0x02),
    /** Gets the "high watermark" of the heap (the highest the heap has been) in bytes at the time requested */
    CURRENT_HEAP_HIGH_WATERMARK(0x03),
    /** Gets the used heap size in bytes at the time after sl_system_init */
    INIT_USED_HEAP_SIZE(0x04),
    /** Gets the "high watermark" of the heap (the highest the heap has been) in bytes at the time after sl_system_init */
    INIT_HEAP_HIGH_WATERMARK(0x05);
    
    private final int val;

    EzspMemUsageData(int val)
	{
		this.val =val;
	}

	public int getIntVal()
	{
		return this.val;
	}

	public static EzspMemUsageData valOfInt(int v)
	{
		for (EzspMemUsageData st : EzspMemUsageData.values())
		{
			if (st.val == v)
				return st;
		}
		return null;
	}
}
