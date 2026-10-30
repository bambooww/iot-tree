package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPRsp;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.iottree.driver.zigbee.ember.types.MulticastTableEntry;
import org.iottree.driver.zigbee.ember.util.SLStatus;
import org.json.JSONObject;

public class MulticastTableEntryGetRsp extends EZSPRsp
{
	public static final int FRAME_ID = 0x0063 ;
	public static final String FRAME_NAME = "getMulticastTableEntry" ;
	
	/**
	 * sl_status_t	uint32_t	See sl_status.h for an enumerated list.
	 sl_status_t status
	SL_STATUS_OK if the value was read successfully, SL_STATUS_ZIGBEE_EZSP_ERROR otherwise. Errors could be SL_ZIGBEE_EZSP_ERROR_INVALID_ID if the NCP does not recognize valueId, SL_ZIGBEE_EZSP_ERROR_INVALID_VALUE if the length of the returned value exceeds the size of local storage allocated to receive it.
	 */
	SLStatus status ;
	
	/**
	 * The contents of the multicast entry.
	 * 
	 sl_zigbee_multicast_table_entry_t value The contents of the multicast entry.
	 */
	MulticastTableEntry entry ;
	
	public MulticastTableEntryGetRsp(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	@Override
	public int getFrmID()
	{
		return FRAME_ID;
	}

	@Override
	public String getFrmName()
	{
		return FRAME_NAME;
	}

	@Override
	protected int getParamsLen()
	{
		if (this.owner.getEZSPVersion() < 0x0e)
			return 1+MulticastTableEntry.STRUCT_LEN;
		else
			return 4+MulticastTableEntry.STRUCT_LEN;
	}

	
	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
		{
			if(failedr!=null)
				failedr.append("unpack params len<0");
			return false;
		}
		SLStatus[] rets = new SLStatus[1] ;
		int rlen = UTIL_readStatus(this.owner.getEZSPVersion(), bs, offset,rets) ;
		this.status = rets[0]; //DataUtil.getUInt32(bs, offset) ;//0-3
		if(this.status!=SLStatus.OK)
			return true;
		this.entry = new MulticastTableEntry() ;
		return this.entry.unpack(bs, offset+rlen)>0 ;
	}
	
	public SLStatus getStatus()
	{
		return this.status ;
	}
	
	public MulticastTableEntry getMulticastTableEntry()
	{
		return this.entry ;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().putOpt("status",this.status).putOpt("entry", this.entry.toJO()) ;
	}

}
