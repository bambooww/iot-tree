package org.iottree.driver.common.modbus;

import java.io.InputStream;
import java.io.OutputStream;

import kotlin.NotImplementedError;

public class ModbusCmdErr extends ModbusCmd
{
	short errCode = 0x04 ;
	
	short reqFC ;
	short addr ;
	
	public ModbusCmdErr(ModbusCmd.Protocol proto,
			byte[] mbap4tcp,short addr,short req_fc,short errcode)
	{
		this.protocal = proto ;
		this.mbap4Tcp = mbap4tcp;
		this.reqFC = req_fc ;
		this.addr = addr ;
		errCode = errcode ;
	}
	
	public ModbusCmdErr()
	{}
	
	public byte[] getRespData()
	{
		return createRespError(this,addr,reqFC) ;
	}

	@Override
	public short getFC()
	{
		return reqFC;
	}

	@Override
	public int calRespLenRTU()
	{
		throw new NotImplementedError();
	}

	@Override
	protected int reqRespRTU(OutputStream ous, InputStream ins) throws Exception
	{
		throw new NotImplementedError();
	}

	@Override
	protected int reqRespTCP(OutputStream ous, InputStream ins) throws Exception
	{
		throw new NotImplementedError();
	}
}
