package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.DataUtil;
import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.MulticastTableEntry;
import org.json.JSONObject;

/**
 * Sets an entry in the multicast table.
 * 
 * @author jason.zhu
 */
public class MulticastTableEntrySetCmd extends EZSPCmd
{
	public static final int FRAME_ID = 0x0064 ;
	public static final String FRAME_NAME = "setMulticastTableEntry" ;
	/**
		uint8_t index  The index of a multicast table entry
	 */
	int index ;
	
	/**
	 * sl_zigbee_multicast_id_t	uint16_t	16-bit ZigBee multicast group identifier.
	 * 
	 sl_zigbee_multicast_table_entry_t value The contents of the multicast entry.
	 */
	MulticastTableEntry entry ;
	
	public MulticastTableEntrySetCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public MulticastTableEntrySetCmd(EmberManager emgr,int idx,MulticastTableEntry entry)
	{
		super(emgr) ;
		this.index = idx ;
		this.entry = entry;
	}
	
	@Override
	public int getFrmID()
	{
		return FRAME_ID;
	}

	@Override
	public String getFrmName()
	{
		return FRAME_NAME ;
	}

	
	public int getIndex()
	{
		return this.index ;
	}
	
	public MulticastTableEntry getEntry()
	{
		return this.entry ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 1+MulticastTableEntry.STRUCT_LEN;
	}

	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<5)
		{
			if(failedr!=null)
				failedr.append("params len<5") ;
			return false;
		}
		bs[offset] = (byte)this.index ;
		this.entry.pack(bs, offset+1) ;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<5)
		{
			if(failedr!=null)
				failedr.append("params len<5") ;
			return false;
		}
		this.index = bs[offset] & 0xFF ;
		this.entry = new MulticastTableEntry() ;
		return this.entry.unpack(bs, offset+1)>0 ;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("index",this.index).put("entry", this.entry.toJO()) ;
	}
}
