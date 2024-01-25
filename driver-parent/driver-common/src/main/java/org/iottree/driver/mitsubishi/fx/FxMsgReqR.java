package org.iottree.driver.mitsubishi.fx;

public class FxMsgReqR extends FxMsg
{
	//byte cmd = CMD_BR ; 
	
	//char addrTp = 'X' ;
	int baseAddr ;
	
	int startAddr = 0 ;
	//软元件点数
	int byteNum = 5;
	
	

//	public FxMsgReq asAddrTp(char c)
//	{
//		this.addrTp = c ;
//		return this ;
//	}
//	
//	public FxMsgReqR asCmd(byte cmd)
//	{
//		this.cmd = cmd ;
//		return this ;
//	}
	
	public FxMsgReqR asStartAddr(int baseaddr,int startaddr)
	{
		this.baseAddr = baseaddr ;
		this.startAddr = startaddr;
		return this ;
	}
	
	public FxMsgReqR asByteNum(int num)
	{
		if(num>=0x40)
			throw new IllegalArgumentException("reg num cannot big than 0x40") ;
		this.byteNum = num;
		return this ;
	}
	
	@Override
	public byte[] toBytes()
	{
		if(!this.bExt)
		{
			return toBytes_30();
		}
		
         int n = 13;//(5 + 10+2);
         byte[] bs = new byte[n];       //申请 发送数据缓存数量
        // int Rcount = (5 + RegistNum * 4+1);//STX+00FF+D*4+ETX
        // byte[] RxBytes = new byte[Rcount];       //申请 接收数据缓存数量

         bs[0] = STX;
         //bs[1] = this.cmd ;
         bs[1]='E';
         bs[2]=bs[3]='0';
         
         toAsciiHexBytes(this.baseAddr+this.startAddr, bs, 4, 4);
         
         toAsciiHexBytes(this.byteNum,bs,8,2) ;

         bs[10] = ETX ;
         int crc = calCRC(bs,1,10) ;
         toAsciiHexBytes(crc,bs,11,2) ;
         return bs;
	}
	
	public byte[] toBytes_30()
	{
         int n = 11;//(5 + 10+2);
         byte[] bs = new byte[n];       //申请 发送数据缓存数量
        // int Rcount = (5 + RegistNum * 4+1);//STX+00FF+D*4+ETX
        // byte[] RxBytes = new byte[Rcount];       //申请 接收数据缓存数量

         bs[0] = STX;
         bs[1] = CMD_BR ;
         
         toAsciiHexBytes(this.baseAddr+this.startAddr, bs, 2, 4);
         
         toAsciiHexBytes(this.byteNum,bs,6,2) ;

         bs[8] = ETX ;
         int crc = calCRC(bs,1,8) ;
         toAsciiHexBytes(crc,bs,9,2) ;
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
