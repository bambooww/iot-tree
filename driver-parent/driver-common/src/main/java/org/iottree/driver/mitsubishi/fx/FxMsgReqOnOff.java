package org.iottree.driver.mitsubishi.fx;

public class FxMsgReqOnOff extends FxMsg
{
	//char addrTp = 'X' ;
	int baseAddr ;
	
	int startAddr = 0 ;
	//软元件点数
	boolean bOn ;
	
	

//	public FxMsgReq asAddrTp(char c)
//	{
//		this.addrTp = c ;
//		return this ;
//	}
	
	public FxMsgReqOnOff asOnOrOff(boolean b_on)
	{
		this.bOn = b_on ;
		return this ;
	}
	
	public FxMsgReqOnOff asStartAddr(int baseaddr,int startaddr)
	{
		this.baseAddr = baseaddr ;
		this.startAddr = startaddr;
		return this ;
	}
	
	@Override
	public byte[] toBytes()
	{
		if(!this.bExt)
		{
			return toBytes_7_8();
		}
		
         int n = 10;//(5 + 10+2);
         byte[] bs = new byte[n];
         bs[0] = STX;
         bs[1] = 'E' ;
         if(this.bOn)
        	 bs[2] = CMD_FORCE_ON ;
         else
        	 bs[2] = CMD_FORCE_OFF ;
         
         int addr = this.baseAddr+this.startAddr ;
         int l = (byte)(addr & 0xFF) ;
         int h = (byte)(addr>>8 & 0xFF) ;
         toAsciiHexBytes(l, bs, 3, 2);
         toAsciiHexBytes(h, bs, 5, 2);
         
         bs[7] = ETX ;
         int crc = calCRC(bs,1,7) ;
         toAsciiHexBytes(crc,bs,8,2) ;
         return bs;
	}
	
	public byte[] toBytes_7_8()
	{
         int n = 9;//(5 + 10+2);
         byte[] bs = new byte[n];
         bs[0] = STX;
         if(this.bOn)
        	 bs[1] = CMD_FORCE_ON ;
         else
        	 bs[1] = CMD_FORCE_OFF ;
         
         toAsciiHexBytes(this.baseAddr+this.startAddr, bs, 2, 4);
         
         bs[6] = ETX ;
         int crc = calCRC(bs,1,6) ;
         toAsciiHexBytes(crc,bs,7,2) ;
         return bs;
	}

	public int getRetOffsetBytes()
	{
		return this.startAddr ;
	}
}
