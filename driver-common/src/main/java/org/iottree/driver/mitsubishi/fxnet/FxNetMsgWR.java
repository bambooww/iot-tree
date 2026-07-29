package org.iottree.driver.mitsubishi.fxnet;

public class FxNetMsgWR extends FxNetMsg
{
	static byte[] CMD_BS2 = new byte[]{'W','R'} ;
	
	short readNum = -1 ;
	
	public FxNetMsgWR asReadNum(short rn)
	{
		this.readNum=rn ;
		return this ;
	}
	
	public byte[] getCmdBS2()
	{
		return CMD_BS2 ;
	}
	
	
	@Override
	public byte[] toBytes()
	{
		byte[] bs = new byte[17] ;
		bs[0] = ENQ ;
		toAsciiHexBytes(this.stationCode,bs,1,2) ;
		toAsciiHexBytes(this.pcCode,bs,3,2) ;
		bs[5] = CMD_BS2[0] ;
		bs[6] = CMD_BS2[1] ;
		
		bs[7] = msgWait ;
		byte[] addrbs = this.getStartAddrBS5() ;
		bs[8] = addrbs[0] ;
		bs[9] = addrbs[1] ;
		bs[10] = addrbs[2] ;
		bs[11] = addrbs[3] ;
		bs[12] = addrbs[4] ;
		
		toAsciiHexBytes(this.readNum,bs,13,2) ;
		int crc = calCRC(bs,1,14) ;
        toAsciiHexBytes(crc,bs,15,2) ;
		return bs;
	}

}
