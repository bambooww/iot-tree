package org.iottree.driver.zigbee.zcl;

public class ZCLGenCmdID
{
	public final static String[] IDS = {
			"Read Attributes",
			"Read Attributes Response",
			"Write Attributes",
			"Write Attributes Undivided",
			"Write Attributes Response",
			"Write Attributes No Response",
			"Configure Reporting",
			"Configure Reporting Response",
			"Read Reporting Configuration",
			"Read Reporting Configuration Response",
			"Report attributes",
			"Default Response",
			"Discover Attributes",
			"Discover Attributes Response",
			"Read Attributes Structured",
			"Write Attributes Structured",
			"Write Attributes Structured response",
			"Discover Commands Received",
			"Discover Commands Received Response",
			"Discover Commands Generated",
			"Discover Commands Generated Response",
			"Discover Attributes Extended",
			"Discover Attributes Extended Response"
	} ; //23
	
	int cmdID ;
	
	public ZCLGenCmdID(int cmd_id)
	{
		if(cmd_id<0 || cmd_id>0x16)
			throw new IllegalArgumentException("cmd_id must in [0x00-0x16]");
		
		this.cmdID = cmd_id ;
	}
	
	public int getCmdID()
	{
		return this.cmdID ;
	}
	
	public String getCmdTitle()
	{
		return IDS[this.cmdID] ;
	}
}
