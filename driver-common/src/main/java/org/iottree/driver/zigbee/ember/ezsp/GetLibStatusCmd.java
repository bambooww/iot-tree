package org.iottree.driver.zigbee.ember.ezsp;

import org.iottree.driver.zigbee.ember.EZSPCmd;
import org.iottree.driver.zigbee.ember.EmberManager;
import org.json.JSONObject;

public class GetLibStatusCmd extends EZSPCmd
{
	/**
	 * sl_zigbee_library_id_t	 uint8_t	A library identifier
	 * sl_zigbee_library_id_t libraryId  The ID of the library being queried.
	 */
	int libraryId ;
	
	public GetLibStatusCmd(EmberManager emgr)
	{
		super(emgr) ;
	}
	
	public GetLibStatusCmd(EmberManager emgr,int ezsp_ver,int lib_id)
	{
		super(emgr) ;
		this.libraryId = lib_id ;
	}
	
	@Override
	public int getFrmID()
	{
		return 0x01;
	}

	@Override
	public String getFrmName()
	{
		return "getLibraryStatus";
	}

	public int getLibraryId()
	{
		return this.libraryId ;
	}
	
	@Override
	protected int getParamsLen()
	{
		return 1;
	}

	
	@Override
	protected boolean packParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		bs[offset] = (byte)this.libraryId;
		return true;
	}

	@Override
	protected boolean unpackParams(byte[] bs, int offset, int len,StringBuilder failedr)
	{
		if(len<0)
			return false;
		this.libraryId = bs[offset] & 0xFF ;
		return true;
	}
	
	@Override
	public JSONObject toJO()
	{
		return super.toJO().put("lib_id",this.libraryId) ;
	}
}
