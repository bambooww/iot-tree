package org.iottree.driver.zigbee.ember.util;

import org.iottree.driver.zigbee.zspec.util.AddrMode;

public enum ASHRstErr
{
//	public static final int RESET_UNKNOWN = 0x00;
//	public static final int RESET_EXTERNAL = 0x01;
//	public static final int RESET_POWER_ON = 0x02;
//	public static final int RESET_WATCHDOG = 0x03;
//	public static final int RESET_ASSERT = 0x06;
//	public static final int RESET_BOOTLOADER = 0x09;
//	public static final int RESET_SOFTWARE = 0x0B;
//	public static final int ERROR_EXCEED_ACK_TIMEOUT_C = 0x51;
//	public static final int ERROR_CHIP_SPEC_RESET = 0x80;
	
	RESET_UNKNOWN(0, "unknown")
	,RESET_EXTERNAL(0x01, "external")
	,RESET_POWER_ON(0x02, "power_on")
	,RESET_WATCHDOG(0x03, "watchdog")
	,RESET_ASSERT(0x06, "assert")
	,RESET_BOOTLOADER(0x09, "bootloader")
	,RESET_SOFTWARE(0x0B, "software")
	,ERROR_EXCEED_ACK_TIMEOUT_C(0x51, "exceed_ack_to")
	,ERROR_CHIP_SPEC_RESET(0x80, "chip_spec_reset")
	;

	private final int val;
	private final String title;

	ASHRstErr(int val, String title)
	{
		this.val = val;
		this.title = title;
	}

	public int getValInt()
	{
		return val;
	}

	public String getTitle()
	{
		return title;
	}

	public static ASHRstErr valOfInt(int v)
	{
		for (ASHRstErr am : ASHRstErr.values())
		{
			if (am.val == v)
				return am;
		}
		return null;
	}
}
