package org.iottree.driver.zigbee.ember;

import org.iottree.driver.zigbee.ember.util.ASHRstErr;

/**
 * send by NCP
 * 
 * @author jason.zhu
 *
 */
public class ASHFrmRstAck extends ASHFrame
{
	int version =0 ;
	
	int reset_code = 0 ;
	
	public ASHFrmRstAck()
	{
		super(TP_RSTACK) ;
	}
	
	public int getVersion()
	{
		return this.version ;
	}
	
	public int getResetCode()
	{
		return this.reset_code ;
	}
	
	public ASHRstErr getRstErr()
	{
		return ASHRstErr.valOfInt(this.reset_code) ;
	}
	
	@Override
	public int getDataFieldLen()
	{
		return 2;
	}

	@Override
	protected boolean packDataField(byte[] data, int offset,int dflen)
	{
		data[offset] = (byte)this.version ;
		data[offset+1] = (byte)this.reset_code ;
		return true;
	}

	@Override
	protected boolean unpackDataField(byte[] data, int offset, int dflen,StringBuilder failedr)
	{
		if(dflen!=2)
			return false;
		this.version = data[offset] & 0xFF ;
		this.reset_code = data[offset+1] & 0xFF ;
		return true ;
	}
	
	public String toString()
	{
		return "RSTACK ver="+this.getVersion()+" code="+this.getResetCode()+" reset_t="+this.getRstErr()+" version="+version;
	}
	
	@Override
	public String toShortString()
	{
		return "RSTACK reset_t="+this.getRstErr();
	}
}
