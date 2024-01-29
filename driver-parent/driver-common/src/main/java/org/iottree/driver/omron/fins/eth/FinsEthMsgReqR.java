package org.iottree.driver.omron.fins.eth;

import java.io.IOException;
import java.io.OutputStream;

public class FinsEthMsgReqR extends FinsEthMsg
{
	short area ;
	int startAddr ;
	short startBit ;
	int readLen ;
	
	public FinsEthMsgReqR(short tar_clientid, short sor_clientid,
			short area,int start_addr,short start_bit,int read_len)
	{
		super(tar_clientid, sor_clientid);
		this.area = area ;
		this.startAddr = start_addr ;
		this.startBit = start_bit ;
		this.readLen = read_len ;
	}

	@Override
	protected short getMRC()
	{
		return 1;
	}

	@Override
	protected short getSRC()
	{
		return 1;
	}

	protected short getICF()
	{
		return 0x80;
	}
	

	@Override
	protected int getParamBytesNum()
	{
		return 6;
	}

	@Override
	protected void writeParam(OutputStream outputs) throws IOException
	{
		byte[] bs = new byte[6] ;
		bs[0] = (byte)area;
		outputs.write(bs);
		
		short2bytes((short)readLen,bs,4) ;
	}
	
}
