package org.iottree.driver.zigbee.zspec.util;

public enum AddrMode
{
	not_present(0, "ADDRESS_NOT_PRESENT"), group_addr(1, "GROUP_ADDRESS")
		,addr_16bit(2, "ADDRESS_16_BIT"),addr_64bit(3, "ADDRESS_64_BIT"),broadcast(0xFF,"BROADCAST");

	private final int val;
	private final String title;

	AddrMode(int val, String title)
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
	
	
	public static AddrMode valOfInt(int v)
	{
		for(AddrMode am:AddrMode.values())
		{
			if(am.val==v)
				return am ;
		}
		return null ;
	}
}
