package org.iottree.driver.s7.ppi;

import org.iottree.core.UAVal.ValTP;
import org.iottree.core.util.Convert;

/**
 * read write request
 * 
 * SD  LE  LER SD DA SA FC CC GU DU FCS ED
 * 
 * 0   1   2   3   4  5   6   7   8   9  10 11 12 13 14 15 16 17 18 19 20 21    22 23  24  25  26    27  28 29 30   31    32
 * 68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10    02 00  02  00  00   82   00 00 00   66    16
 * 68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10    20 00  01  00  00   20   00 00 01   22    16 
 * 68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10    02 00  01  00  00   82   00 00 00   65    16
 * 68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10    01 00  01  00  00   82   00 00 00   64 16
 * 68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10    82 00 01 00 00      82 00 00 00 E5 16
 * 68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10    02 00 04 00 00      1F 00 01 40 46 16
 * 
 * 68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10    1F 00 03 00 00      1F 00 00 20 41 16
 * 68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10     02 00 04 00 00     1F 00 01 40 46 16
 * 68 1B 1B 68 02 00 6C 32 01 00 00 00 00 00 0E 00 00 04 01 12 0A 10     1F 00 04 00 00     1F 00 00 28 4A 16
 * sd  le      sd DA SA FC          CC              GU                                       VT     RC     MT  MC  OFFSET     FCS  ED
 *                                                                                        DU ---------------------------------------------
 * VT (value type)  Bit=01  B=02 W=04 D=06
 * RC (read count)    max=208 0xDE
 * MT (mem type)   01=V  00=Other
 * MC (mem type code)   
 *       S=04 SM=05 AI=06 AQ=07 C = 1E  T=1F I=81  Q=82 M=83 V=84
 * @author jason.zhu
 */
public class PPIMsgReqR extends PPIMsgReq
{
int offsetBytes = 0 ;
	
	int inBit = -1 ;//-1 is null
	
	//PPIMemValTp memValTp = null ;
	
	int readNum = 1 ; //bytes num or TC numbre
	
	
	@Override
	protected short getStartD()
	{
		return SD_REQ;
	}
	
	public short getFC()
	{
		return 0x6C ;
	}
	
//	/**
//	 * mem byte * 8
//	 * 
//	 * @param offset
//	 * @return
//	 */
//	public PPIMsgReq withOffset(int offset)
//	{
//		this.offset = offset;
//		return this;
//	}
	
//	public PPIMsgReq withAddrByte(PPIMemTp mtp, int byteoffsets,int inbit,int readnum)
//	{
//		super.withAddrByte(mtp, byteoffsets, inbit);
//		
//		this.num = (short)readnum;
//		
//		return this;
//	}

//	public PPIMsgReq withReadNum(short n)
//	{
//		if(n<=0)
//			throw new IllegalArgumentException("num must bigger than 0");
//		this.num = n ;
//		return this ;
//	}
	
//	public short getReadNum()
//	{
//		return this.num ;
//	}

	public int getOffsetBytes()
	{
		return this.offsetBytes ;
	}
	
	public int getRetOffsetBytes()
	{
		return this.offsetBytes ;
	}
	
	public int getInBits()
	{
		return this.inBit ;
	}

	public int getOffsetBits()
	{
		return this.offsetBytes*8 + (this.inBit>=0?this.inBit:0) ;
	}
	
	public boolean isBitReq()
	{
		return this.inBit>=0;
	}
	
	public int getReadNum()
	{
		return this.readNum ;
	}

	public PPIMsgReq withAddr(String addr,ValTP vtp)
	{
		StringBuilder failedr = new StringBuilder() ;
		PPIAddr paddr = PPIAddr.parsePPIAddr(addr,vtp,failedr) ;
		if(paddr!=null)
		{
			this.memTp = paddr.getMemTp() ;
			this.offsetBytes = paddr.getOffsetBytes() ;
			this.inBit = paddr.getInBits() ;
			this.readNum = paddr.getBytesNum() ;
		}
		return this;
	}
	
	
	
	
	public PPIMsgReq withAddrByte(PPIMemTp mtp, int byteoffsets,int inbit,int bytesnum)
	{
		this.memTp = mtp ;
		this.offsetBytes = byteoffsets;
		this.inBit = inbit ;
		this.readNum = bytesnum ;
		return this;
	}
	
	private short calLen()
	{
		return 27;
	}
	
	@Override
	public byte[] toBytes()
	{
		short le =  calLen() ;
		byte[] rets = new byte[le+6];
		rets[0] = rets[3] = (byte)getStartD();
		rets[1] = rets[2] = (byte)le;
		rets[4] = (byte)da ;
		rets[5] = (byte)sa;
		rets[6] = (byte)getFC();
		rets[7] = 0x32;
		rets[8] = 0x01;
		rets[9] = rets[10] = rets[11] = rets[12] = 0x0;
		rets[13] = 0x0;
		rets[14] = 0x0E;
		rets[15] = 0x00;
		rets[16] = 0x00;
		rets[17] = 0x04;
		rets[18] = 0x01;
		rets[19] = 0x12;
		rets[20] = 0x0A;
		rets[21] = 0x10;
		//
		rets[22] = (byte)PPIMemValTp.B.getVal();//memTp.getVal() ;
		if(memTp==PPIMemTp.T || memTp==PPIMemTp.C)
			rets[22] = (byte)PPIMemTp.T.getVal() ;
		rets[23] = 0x0;
		rets[24] = (byte)this.getReadNum();
		rets[25] = 0x0;
		
		//PPIMemTp mtp = ppiAddr.getMemTp();
		if(memTp==PPIMemTp.V)
			rets[26] = 0x01; //other
		else
			rets[26] = 0x0; //other
		rets[27] = (byte)memTp.getVal();
		
		int offaddr = this.getOffsetBits();
		//if(memTp==PPIMemTp.T)
		//	offaddr = this.getOffsetBytes();
		rets[28] = (byte)((offaddr >> 16) & 0xFF) ;
		rets[29] = (byte)((offaddr >> 8) & 0xFF) ;
		rets[30] = (byte)(offaddr & 0xFF) ;
		
		rets[31] = calChkSum(rets,4,27) ;
		rets[32] = 0x16 ;//end
		
		return rets ;
	}
	
}
