package org.iottree.driver.zigbee.ember;

import org.iottree.driver.zigbee.ember.util.ASHRstErr;

/**
 * 协议错误，需要重新同步
 * 
 * send by NCP
 * 
 * @author jason.zhu
 */
public class ASHFrmError extends ASHFrame
{
	int version =0 ;
	
	int err_code = 0 ;
	
	public ASHFrmError()
	{
		super(TP_ERROR) ;
	}
	
	public int getVersion()
	{
		return this.version ;
	}
	
	public int getErrCode()
	{
		return this.err_code ;
	}
	
	public ASHRstErr getRstErr()
	{
		return ASHRstErr.valOfInt(this.err_code) ;
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
		data[offset+1] = (byte)this.err_code ;
		return true;
	}

	@Override
	protected boolean unpackDataField(byte[] data, int offset, int dflen,StringBuilder failedr)
	{
		if(dflen!=2)
			return false;
		this.version = data[offset] & 0xFF ;
		this.err_code = data[offset+1] & 0xFF ;
		return true ;
	}

	public String toString()
	{
		return "ERROR code="+this.getErrCode()+" err_t="+this.getRstErr();
	}
	
	@Override
	public String toShortString()
	{
		return "ERROR err_t="+this.getRstErr();
	}
}
