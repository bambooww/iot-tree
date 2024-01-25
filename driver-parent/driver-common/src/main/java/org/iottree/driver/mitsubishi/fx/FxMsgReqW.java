package org.iottree.driver.mitsubishi.fx;

public class FxMsgReqW extends FxMsg
{
	//char addrTp = 'X' ;
	int baseAddr ;
	
	int startAddr = 0 ;
	//软元件点数
	int byteNum = -1;
	
	byte[] byteVals = null; 

//	public FxMsgReq asAddrTp(char c)
//	{
//		this.addrTp = c ;
//		return this ;
//	}
	
//	public FxMsgReqW asCmd(byte cmd)
//	{
//		this.cmd = cmd ;
//		return this ;
//	}
	
	public FxMsgReqW asStartAddr(int baseaddr,int startaddr)
	{
		this.baseAddr = baseaddr ;
		this.startAddr = startaddr;
		return this ;
	}
	
	public FxMsgReqW asBytesVal(byte[] bs)
	{
		if(bs.length>=0x40)
			throw new IllegalArgumentException("reg num cannot big than 0x40") ;
		this.byteNum = bs.length;
		this.byteVals = bs ;
		return this ;
	}
	
	@Override
	public byte[] toBytes()
	{
		if(!this.bExt)
		{
			return toBytes_31();
		}
		
         int n = 13+this.byteNum*2;//(5 + 10+2);
         byte[] bs = new byte[n];       //申请 发送数据缓存数量
        // int Rcount = (5 + RegistNum * 4+1);//STX+00FF+D*4+ETX
        // byte[] RxBytes = new byte[Rcount];       //申请 接收数据缓存数量

         bs[0] = STX;
         bs[1] = 'E' ;
         bs[2]  ='1';
         bs[3] = '0';
         
         toAsciiHexBytes(this.baseAddr+this.startAddr, bs, 4, 4);
         
         toAsciiHexBytes(this.byteNum,bs,8,2) ;
         
         for(int i = 0  ; i < this.byteNum ; i ++)
        	 toAsciiHexBytes(this.byteVals[i],bs,10+i*2,2) ;

         bs[n-3] = ETX ;
         int crc = calCRC(bs,1,n-3) ;
         toAsciiHexBytes(crc,bs,n-2,2) ;
         return bs;
	}
	
	public byte[] toBytes_31()
	{
         int n = 11+this.byteNum*2;//(5 + 10+2);
         byte[] bs = new byte[n];       //申请 发送数据缓存数量
        // int Rcount = (5 + RegistNum * 4+1);//STX+00FF+D*4+ETX
        // byte[] RxBytes = new byte[Rcount];       //申请 接收数据缓存数量

         bs[0] = STX;
         bs[1] = CMD_BW ;
         
         toAsciiHexBytes(this.baseAddr+this.startAddr, bs, 2, 4);
         
         toAsciiHexBytes(this.byteNum,bs,6,2) ;
         
         for(int i = 0  ; i < this.byteNum ; i ++)
        	 toAsciiHexBytes(this.byteVals[i],bs,8+i*2,2) ;

         bs[n-3] = ETX ;
         int crc = calCRC(bs,1,n-3) ;
         toAsciiHexBytes(crc,bs,n-2,2) ;
         return bs;
	}

//	@Override
//	public byte[] toBytes()
//	{
//		 int n = 11;//(5 + 10+2);
//         byte[] bs = new byte[n];       //申请 发送数据缓存数量
//        // int Rcount = (5 + RegistNum * 4+1);//STX+00FF+D*4+ETX
//        // byte[] RxBytes = new byte[Rcount];       //申请 接收数据缓存数量
//
//         bs[0] = STX;
//         bs[1] = CMD_BR ;
//         
//         toAsciiHexBytes(this.startAddr, bs, 2, 4);
//         
//         toAsciiHexBytes(this.regNum,bs,6,2) ;
//
//         bs[8] = ETX ;
//         int crc = calCRC(bs,1,8) ;
//         toAsciiHexBytes(crc,bs,9,2) ;
//         return bs;
//	}
	
	
	public int getRetOffsetBytes()
	{
		return this.startAddr ;
	}
}
